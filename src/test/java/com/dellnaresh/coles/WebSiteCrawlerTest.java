package com.dellnaresh.coles;

import com.dellnaresh.model.Categorie;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ncmiriyala on 13/02/2017.
 */
public class WebSiteCrawlerTest {
    public static final String BACK_TO_SCHOOL = "back-to-school";
    private WebSiteCrawler webSiteCrawler;

    @Before
    public void setUp() throws Exception {
    webSiteCrawler=new WebSiteCrawler();

    }

    @Test
    public void shouldGetAllCategories() throws Exception {
        assertNotNull(webSiteCrawler.getCategories());
        assertEquals(webSiteCrawler.getCategories().size(),18);


    }
    @Test
    public void shouldGetAllSubCategories() throws Exception {
        List<Categorie> subCategoriesFor = webSiteCrawler.getAllChildCategories(BACK_TO_SCHOOL);
        assertNotNull(subCategoriesFor);
        subCategoriesFor.stream().forEach(System.out::println);
        assertEquals(subCategoriesFor.size(),18);

    }
    @Test
    public void shouldGetProductJson() throws Exception {
        List<String> productJson = webSiteCrawler.getProductJson();
        assertNotNull(productJson);
        System.out.println(productJson.size());


    }

    @Test
    public void shouldGetProductsAsJsonObjects() throws Exception{
        System.out.println(webSiteCrawler.getProductsAsJson().stream().count());
    }

    @Test
    public void testJsonObject()throws Exception{
        String content = new String(Files.readAllBytes(Paths.get("src\\test\\java\\com\\dellnaresh\\coles\\product.json")));
        List<String> produts=new ArrayList<>();
        produts.add(content);
        List<JSONObject> productjsonList = webSiteCrawler.getProductjsonList(produts);
        productjsonList.stream().forEach(json->{ System.out.println(json.toString());
            Object p = json.get("p");
            String s = p.toString();
            System.out.println(s);
        });
        System.out.println(productjsonList);
        Gson gson=new Gson();
        JsonElement jsonElement = gson.toJsonTree(content);
        System.out.println(jsonElement.isJsonArray());
    }
}