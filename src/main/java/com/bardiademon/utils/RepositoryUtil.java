package com.bardiademon.utils;

import io.vertx.core.AsyncResult;
import io.vertx.ext.sql.ResultSet;

public class RepositoryUtil {
    public static boolean isEmptySelect(final AsyncResult<ResultSet> handler) {
        return handler == null || handler.result() == null || handler.result().getRows() == null || handler.result().getRows().isEmpty();
    }
}
