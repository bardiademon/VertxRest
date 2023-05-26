package com.bardiademon.service;

import io.vertx.core.AsyncResult;
import io.vertx.ext.sql.ResultSet;

public class Service {

    protected Service() {
    }

    protected boolean isEmptySelect(final AsyncResult<ResultSet> handler) {
        return handler == null || handler.result() == null || handler.result().getRows() == null || handler.result().getRows().isEmpty();
    }
}
