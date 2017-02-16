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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;

/**
 * Created by ncmiriyala on 13/02/2017.
 */
public class WebSiteCrawler {
    public static final String PRODUCTS = "\"products\":";
    public static final String CATALOG_GROUP_VIEW = "catalogGroupView";
    public static final String SEO_TOKEN = "seo_token";
    private String searchUrlStart = "https://shop.coles.com.au/online/a-national/";
    private String searchUrlEnd = "?tabType=everything&tabId=everything&personaliseSort=false";
    List<String> productJson = new ArrayList<>();
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
                            .withName(name).withSeo_token(seo_token).withParentCategory(parentCategory).build();
                    checkAndAddAsChildIfTheListContainsTheParent(tokens,categorie,parentCategory);
//                    tokens.add(categorie);
                    getTokens(jsonObject, tokens,seo_token);
                }

            }
        }
    }

    private void checkAndAddAsChildIfTheListContainsTheParent(List<Categorie> tokens, Categorie categorie, String parentCategory) {
        if(! addCategorie(tokens, categorie, parentCategory)){
            tokens.add(categorie);
        }
    }

    private boolean addCategorie(List<Categorie> tokens, Categorie categorie, String parentCategory) {
        Optional<Categorie> any = tokens.stream().filter(getCategoriePredicate(parentCategory)).findAny();
        if(any.isPresent()){
            any.get().addChildCategorie(categorie);
            return true;
        }else {
            tokens.stream().forEach(token->{
                addCategorie(token.getChildCategorie(),categorie,parentCategory);
            });
            return false;
        }

    }

    private Predicate<Categorie> getCategoriePredicate(String parentCategory) {
        return token -> token.getSeo_token().equals(parentCategory);
    }

    public List<String> getProductJson() throws Exception {


        List<String> categories = getCategories();
        if (nonNull(categories)) {
            categories.stream().forEach(categorie -> {
                try {

                    getAndAddProductsJson(productJson, categorie);
                    getSubCategorieProducts(categorie);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return productJson;

    }

    private void getSubCategorieProducts(String categorie) throws IOException {
        List<String> subCategoriesFor = getSubCategoriesFor(categorie);
        if (nonNull(subCategoriesFor)) {
            subCategoriesFor.stream().forEach(subCategorie -> {
                try {
                    getAndAddProductsJson(productJson, subCategorie);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    getSubCategorieProducts(categorie + "/" + subCategorie);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void getAndAddProductsJson(List<String> productJson, String categorie) throws IOException {
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
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            String seo_token = arr.getJSONObject(i).getString("seo_token");
            tokens.add(seo_token);
        }
        return tokens;
    }

    public List<JSONObject> getProductsAsJson() throws Exception {
        List<String> productJson = getProductJson();
        List<JSONObject> productsAsJsonObjects = new ArrayList<>();
        productJson.stream().forEach(json -> {

            json = removeProductsFromJson(json);
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                productsAsJsonObjects.add(jsonArray.getJSONObject(i));
            }
        });
        return productsAsJsonObjects;
    }

    private String removeProductsFromJson(String json) {
        return json.replaceAll("products\":", "");
    }
}
