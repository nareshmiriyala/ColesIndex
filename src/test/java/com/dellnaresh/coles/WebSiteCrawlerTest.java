package com.dellnaresh.coles;

import org.junit.Before;
import org.junit.Test;

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


    }
}