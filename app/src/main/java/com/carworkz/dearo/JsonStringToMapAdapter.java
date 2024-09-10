package com.carworkz.dearo;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import javax.annotation.Nullable;

public final class JsonStringToMapAdapter {


    @FromJson
    @Nullable
    @JsonStringToMap
    Map<String, String> fromJson(String jsonString) {
        try {
            Moshi moshi = new Moshi.Builder().build();
            Type type = Types.newParameterizedType(Map.class, String.class, String.class);
            JsonAdapter<Map<String, String>> adapter = moshi.adapter(type);
            return adapter.fromJson(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @ToJson
    String toJson(@JsonStringToMap Map<String, String> jsonObject) {
        return jsonObject.toString();
    }
}
