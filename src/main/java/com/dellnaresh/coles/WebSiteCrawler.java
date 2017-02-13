package com.dellnaresh.coles;
import org.json.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by ncmiriyala on 13/02/2017.
 */
public class WebSiteCrawler {
    private Logger logger= LoggerFactory.getLogger(WebSiteCrawler.class);
    public String getCategories() throws IOException {
        Document doc = Jsoup.connect("https://shop.coles.com.au/a/a-national/everything/browse").get();
        Elements links = doc.select("div[data-colrs-all-categories]");
        logger.info("Json Category list {}",links.text());
        getSeoTokens(links.text());
        return links.text();
    }

    private List<String> getSeoTokens(String json){
        JSONObject obj = new JSONObject(json);

        JSONArray arr = obj.getJSONArray("catalogGroupView");
        for (int i = 0; i < arr.length(); i++)
        {
            String seo_token = arr.getJSONObject(i).getString("seo_token");
            logger.info("Token {}",seo_token);
        }
        return null;
    }
}
