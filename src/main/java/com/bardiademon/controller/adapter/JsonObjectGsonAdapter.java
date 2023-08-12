package com.bardiademon.controller.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.vertx.core.json.JsonObject;

import java.io.IOException;

public final class JsonObjectGsonAdapter extends TypeAdapter<JsonObject> {
    private static final Gson GSON = new Gson();


    @Override
    public void write(final JsonWriter jsonWriter , final JsonObject jsonObject) throws IOException {
        if (jsonObject == null) {
            jsonWriter.nullValue();
        } else {
            try {
                jsonWriter.jsonValue(jsonObject.encode());
            } catch (Exception e) {
                jsonWriter.jsonValue(GSON.toJson(jsonObject.getMap()));
            }
        }
    }

    @Override
    public JsonObject read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.hasNext()) {
            final String value = jsonReader.nextString();
            if (value != null) {
                return new JsonObject(value);
            }
        }
        return null;
    }

}