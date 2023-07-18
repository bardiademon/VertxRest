package com.bardiademon.data.mapper;

import io.vertx.core.json.JsonObject;

import java.time.LocalDateTime;

public final class Mapper {

    private Mapper() {
    }

    public static LocalDateTime toLocalDateTime(final JsonObject row , final String key) {
        return (row == null || row.isEmpty() || !row.containsKey(key) || row.getValue(key) == null || !(row.getValue(key) instanceof LocalDateTime) ? null : (LocalDateTime) row.getValue(key));
    }
}
