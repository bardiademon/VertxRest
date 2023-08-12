package com.bardiademon.controller.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.vertx.core.json.JsonArray;

import java.io.IOException;
import java.util.List;

public final class JsonArrayGsonAdapter extends TypeAdapter<JsonArray> {

    private static final Gson GSON = new Gson();

    @Override
    public void write(final JsonWriter jsonWriter , final JsonArray jsonArray) throws IOException {
        if (jsonArray == null) {
            jsonWriter.nullValue();
        } else {
            try {
                jsonWriter.jsonValue(jsonArray.encode());
            } catch (Exception e) {
                jsonWriter.jsonValue(GSON.toJson(jsonArray.stream().toList()));
            }
        }
    }

    @Override
    public JsonArray read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.hasNext()) {
            final String value = jsonReader.nextString();
            if (value != null) {
                return new JsonArray(value);
            }
        }
        return null;
    }

}