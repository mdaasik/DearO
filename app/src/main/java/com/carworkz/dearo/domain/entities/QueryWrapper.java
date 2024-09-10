package com.carworkz.dearo.domain.entities;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by ambab on 21/8/17.
 */

@SuppressWarnings("CanBeFinal")
@Json(name = "filter")
public class QueryWrapper {


    @Json(name = "include")
    List<String> includeString;

    @Json(name = "order")
    List<String> order;

    @Json(name = "where")
    Where where;

    @Json(name = "limit")
    int limit;

    @Json(name = "skip")
    int skip;

    public QueryWrapper(List<String> includeString, String statusValue, List<String> order, int limit, int skip) {
        this.includeString = includeString;
        this.order = order;
        this.limit = limit;
        this.skip = skip;
        where = new Where();
        where.setStatus(statusValue);
    }

    public JSONObject toJson() {

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<QueryWrapper> jsonAdapter = moshi.adapter(QueryWrapper.class);
        String json = jsonAdapter.toJson(this);
        JSONObject jsonObject = null;
        try {

            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static class Where {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
