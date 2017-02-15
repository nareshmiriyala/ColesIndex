package com.dellnaresh.coles;

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

/**
 * Created by ncmiriyala on 13/02/2017.
 */
public class WebSiteCrawler {
    public static final String PRODUCTS = "\"products\":";
    private String searchUrlStart = "https://shop.coles.com.au/online/a-national/";
    private String searchUrlEnd = "?tabType=everything&tabId=everything&personaliseSort=false";
    ;
    private Logger logger = LoggerFactory.getLogger(WebSiteCrawler.class);

    public List<String> getCategories() throws IOException {
        Document doc = Jsoup.connect("https://shop.coles.com.au/a/a-national/everything/browse").get();
        Elements links = doc.select("div[data-colrs-all-categories]");
        return getSeoTokens(links.text());
    }

    public List<String> getProductJson() throws Exception {
        List<String> categories = getCategories();
        List<String> productJson = new ArrayList<>();
        categories.stream().forEach(categorie -> {
            try {

                Document doc = Jsoup.connect(getUrlString(categorie)).get();
                String str = doc.html();
                if (!str.isEmpty() && str.contains(PRODUCTS)) {
                    String result = str.substring(str.indexOf(PRODUCTS) + 1, str.indexOf("}]"));
                    productJson.add(result + "}]");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return productJson;

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
            System.out.println(json);
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                productsAsJsonObjects.add(jsonArray.getJSONObject(0));
            }
        });
        return productsAsJsonObjects;
    }

    private String removeProductsFromJson(String json) {
        return json.replaceAll("products\":", "");
    }
}
