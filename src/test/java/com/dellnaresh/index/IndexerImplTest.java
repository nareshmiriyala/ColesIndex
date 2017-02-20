package com.dellnaresh.index;

import com.dellnaresh.coles.Crawler;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


/**
 * Created by nmiriyal on 20/02/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexerImplTest {
    @Autowired
    private Indexer indexer;

    @Autowired
    private Crawler webCrawler;

    @Test
    public void testIndexMessage() throws Exception {
        List<JSONObject> productsAsJson = webCrawler.getProductsAsJson();
        indexer.indexMesssages(productsAsJson);

    }
}