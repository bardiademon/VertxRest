package com.bardiademon.controller.handler;

import com.bardiademon.data.Model.ServerResponse;
import com.bardiademon.data.entity.UserEntity;
import com.bardiademon.data.enums.Response;
import io.vertx.core.Vertx;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;

public interface RestHandler<REQUEST, RESPONSE> {
    void fail(final Throwable throwable , final ServerResponse<RESPONSE> response);

    void fail(final Throwable throwable);

    void fail(final Throwable throwable , final RESPONSE info , final Response response);

    void fail(final ServerResponse<RESPONSE> response);

    void fail(final RESPONSE info , final Response response);

    void fail(final Response response);

    void success(final ServerResponse<RESPONSE> serverResponse);

    void success(final RESPONSE info , final Response response);

    void success(final RESPONSE info);

    void success(final Response response);

    REQUEST request();

    RoutingContext routingContext();

    Vertx vertx();

    SQLConnection sqlConnection();

    UserEntity user();
}
