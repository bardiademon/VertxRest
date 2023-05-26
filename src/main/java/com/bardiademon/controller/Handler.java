package com.bardiademon.controller;

import com.bardiademon.data.Model.ServerResponse;
import com.bardiademon.data.enums.Response;
import io.vertx.core.Vertx;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;

public interface Handler<REQUEST, RESPONSE> {
    void fail(final Throwable throwable , final ServerResponse<RESPONSE> response);

    void fail(final Throwable throwable , final RESPONSE info , final Response response);

    void fail(final ServerResponse<RESPONSE> response);

    void fail(final RESPONSE info , final Response response);

    void fail(final Response response);

    void success(final ServerResponse<RESPONSE> serverResponse);

    void success(final RESPONSE info , final Response response);

    void success(final Response response);

    REQUEST request();

    RoutingContext routingContext();

    Vertx vertx();

    SQLConnection sqlConnection();
}
