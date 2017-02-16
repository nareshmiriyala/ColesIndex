package com.dellnaresh.coles;

import com.dellnaresh.model.Categorie;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ncmiriyala on 13/02/2017.
 */
public class WebSiteCrawlerTest {
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
        List<Categorie> subCategoriesFor = webSiteCrawler.getAllChildCategories("back-to-school");
        assertNotNull(subCategoriesFor);
        subCategoriesFor.stream().forEach(System.out::println);


    }
    @Test
    public void shouldGetProductJson() throws Exception {
       assertNotNull(webSiteCrawler.getProductJson());


    }

    @Test
    public void shouldGetProductsAsJsonObjects() throws Exception{
        webSiteCrawler.getProductsAsJson().stream().forEach(System.out::println);
    }

    @Test
    public void testJsonObject()throws Exception{
        String content = new String(Files.readAllBytes(Paths.get("src\\test\\java\\com\\dellnaresh\\coles\\product.json")));
        Gson gson=new Gson();
        JsonElement jsonElement = gson.toJsonTree(content);
        System.out.println(jsonElement.isJsonArray());
    }
}