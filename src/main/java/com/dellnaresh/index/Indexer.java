package com.dellnaresh.index;

import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by nmiriyal on 20/02/2017.
 */
public interface Indexer {
    void indexMesssages(List<JSONObject> messages);
}
