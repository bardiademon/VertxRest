package com.bardiademon.data.mapper;

import com.google.gson.Gson;
import io.vertx.core.json.JsonObject;

public class DtoMapper {

    private static final Gson GSON = new Gson();

    public static <T> T toDto(final JsonObject body , final Class<T> t) {
        return GSON.fromJson(body.toString() , t);
    }
}
