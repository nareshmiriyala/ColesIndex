package com.dellnaresh.coles;

import com.dellnaresh.model.Categorie;
import com.dellnaresh.model.CategorieBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Created by ncmiriyala on 13/02/2017.
 */
public class WebSiteCrawler {
    public static final String PRODUCTS = "\"products\":";
    public static final String BACK_TO_SCHOOL = "back-to-school";
    public static final String CATALOG_GROUP_VIEW = "catalogGroupView";
    public static final String SEO_TOKEN = "seo_token";
    private String searchUrlStart = "https://shop.coles.com.au/online/a-national/";
    private String searchUrlEnd = "?tabType=everything&tabId=everything&personaliseSort=false";
    List<String> productJson = Collections.synchronizedList(new ArrayList<>());
    private Logger logger = LoggerFactory.getLogger(WebSiteCrawler.class);

    public List<String> getCategories() throws IOException {
        Document doc = Jsoup.connect("https://shop.coles.com.au/a/a-national/everything/browse").get();
        Elements links = doc.select("div[data-colrs-all-categories]");
        return getSeoTokens(links.text());
    }

    public List<String> getSubCategoriesFor(String category) throws IOException {
        Document doc = Jsoup.connect("https://shop.coles.com.au/a/a-national/everything/browse/" + category).get();
        Elements links = doc.select("div[data-colrs-all-categories]");
        return getSeoTokens(links.text());
    }

    public List<Categorie> getAllChildCategories(String category) throws IOException {
        Document doc = Jsoup.connect("https://shop.coles.com.au/a/a-national/everything/browse/" + category).get();
        Elements links = doc.select("div[data-colrs-all-categories]");
        return getAllChildTokens(links.text());
    }

    private List<Categorie> getAllChildTokens(String json) {
        JSONObject obj = new JSONObject(json);
        List<Categorie> tokens = new ArrayList<>();
        getTokens(obj, tokens,"");
        updateChildUrl(tokens);
        return tokens;
    }

    private void getTokens(JSONObject obj, List<Categorie> tokens,String parentCategory) {
        if (obj.has(CATALOG_GROUP_VIEW)) {
            JSONArray arr = obj.getJSONArray(CATALOG_GROUP_VIEW);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject jsonObject = arr.getJSONObject(i);
                if(jsonObject.has(SEO_TOKEN)) {
                    String seo_token = jsonObject.getString(SEO_TOKEN);

                    int level = jsonObject.getInt("level");
                    String name = jsonObject.getString("name");
                    String id = jsonObject.getString("uniqueID");
                    Categorie categorie = CategorieBuilder.aCategorie().withLevel(level).withId(Integer.parseInt(id))
                            .withName(name).withSeo_token(seo_token).withParentCategory(parentCategory)
                            .build();
                    checkAndAddAsChildIfTheListContainsTheParent(tokens,categorie,parentCategory);
//                    tokens.add(categorie);
                    getTokens(jsonObject, tokens,seo_token);
                }

            }
        }
    }

    private void checkAndAddAsChildIfTheListContainsTheParent(List<Categorie> tokens, Categorie categorie, String parentCategory) {
        List<String> updateIfAdded=new ArrayList<>();
        addCategorie(tokens, categorie, parentCategory,updateIfAdded);
        if(updateIfAdded.size()==0)
            tokens.add(categorie);
        }

    private void addCategorie(List<Categorie> tokens, Categorie categorie, String parentCategory,List<String> updateIfAadded) {
        Optional<Categorie> any = tokens.parallelStream().filter(getCategoriePredicate(parentCategory)).findAny();
        if(any.isPresent()){
            any.get().addChildCategorie(categorie);
            updateIfAadded.add("True added");
        }else {
            tokens.parallelStream().forEach(token->{
                addCategorie(token.getChildCategorie(),categorie,parentCategory,updateIfAadded);
            });
        }

    }

    private Predicate<Categorie> getCategoriePredicate(String parentCategory) {
        return token -> token.getSeo_token().equals(parentCategory);
    }

    public List<String> getProductJson() throws Exception {


        List<Categorie> categories = getAllChildCategories(BACK_TO_SCHOOL);

        if (nonNull(categories)) {
            categories.parallelStream().forEach(categorie -> {
                try {
                    getAndAddProductsJson(productJson, categorie.getUrl());
                    getChildProductJson(categorie);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return productJson;

    }

    private void updateChildUrl(List<Categorie> categories) {

        updateUrl(categories);
    }

    private void updateUrl(List<Categorie> categories) {
        categories.parallelStream().forEach(cat->{

            cat.getChildCategorie().parallelStream().forEach(child->{
                 child.setUrl(cat.getUrl()+"/"+child.getSeo_token());
                updateUrl(cat.getChildCategorie());
            });

        });
    }

    private void getChildProductJson(Categorie categorie) {
        List<Categorie> childCategorie = categorie.getChildCategorie();
        childCategorie.parallelStream().forEach(childCat->{
            try {
                String url = childCat.getUrl();
                logger.info("Getting products for url {}",url);
                getAndAddProductsJson(productJson, url);
                getChildProductJson(childCat);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void getAndAddProductsJson(List<String> productJson, String categorie) throws IOException {
        if(categorie.matches(".*\\d+.*")){
            return;
        }
        Document doc = Jsoup.connect(getUrlString(categorie)).get();
        String str = doc.html();
        if (!str.isEmpty() && str.contains(PRODUCTS)) {
            String result = str.substring(str.indexOf(PRODUCTS) + 1, str.indexOf("}]"));
            productJson.add(result + "}]");
        }
    }

    private String getUrlString(String categorie) {
        return searchUrlStart + categorie + searchUrlEnd;
    }

    private List<String> getSeoTokens(String json) {
        JSONObject obj = new JSONObject(json);

        JSONArray arr = obj.getJSONArray("catalogGroupView");
        List<String> tokens = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < arr.length(); i++) {
            String seo_token = arr.getJSONObject(i).getString("seo_token");
            tokens.add(seo_token);
        }
        return tokens;
    }

    public List<JSONObject> getProductsAsJson() throws Exception {
        List<String> productJson = getProductJson();
        List<JSONObject> productsAsJsonObjects = Collections.synchronizedList(new ArrayList<>());
        productJson.stream().forEach(json -> {

            json = removeProductsFromJson(json);
//            logger.info("Json message: {}",json);
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    productsAsJsonObjects.add(jsonArray.getJSONObject(i));
                }
            }catch (Exception e){
                logger.error("Exception parsing json object json '{}'",json,e);
            }

        });
        return productsAsJsonObjects;
    }

    private String removeProductsFromJson(String json) {
        return json.replaceAll("products\":", "");
    }
}
