package com.dellnaresh.coles;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by nmiriyal on 20/02/2017.
 */
public interface Crawler {
    List<JSONObject> getProductsAsJson() throws Exception;

    List<JSONObject> getProductjsonList(List<String> productJson);
}
