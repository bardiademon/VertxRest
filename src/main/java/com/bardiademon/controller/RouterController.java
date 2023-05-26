package com.bardiademon.controller;

import com.bardiademon.data.Model.ServerResponse;
import com.bardiademon.data.dto.NothingDto;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.excp.ResponseException;
import com.bardiademon.data.mapper.DtoMapper;
import com.bardiademon.data.validation.Validation;
import com.bardiademon.utils.DbConnection;
import com.bardiademon.utils.RemoveUploadedFile;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RouterController<REQUEST, RESPONSE> extends AbstractVerticle implements Handler<REQUEST, RESPONSE> {

    private final static Logger logger = LogManager.getLogger(RouterController.class);
    private final RoutingContext routingContext;
    private final RestController<REQUEST, RESPONSE> routerRestController;
    private final Rest rest;
    private SQLConnection sqlConnection;
    private REQUEST request;

    private Promise<Void> promise;

    private RouterController(final RoutingContext routingContext , final RestController<REQUEST, RESPONSE> routerRestController , final Rest rest) {
        this.routingContext = routingContext;
        this.routerRestController = routerRestController;
        this.rest = rest;
    }

    public static <REQUEST, RESPONSE> void handle(final RoutingContext routingContext , final RestController<REQUEST, RESPONSE> routerRestController , final Rest rest) {
        final RouterController<REQUEST, RESPONSE> routerController = new RouterController<>(routingContext , routerRestController , rest);
        Vertx.vertx().deployVerticle(routerController)
                .onSuccess(success -> logger.info("Successfully -> Finish promise"))
                .onFailure(fail -> logger.error("Fail -> Finish promise" , fail))
                .onComplete(handler -> routerController.closeVertx());
    }

    @Override
    public void start(final Promise<Void> promise) {
        this.promise = promise;

        if (rest.db()) {
            connectDbResponse().onSuccess(sqlConnection -> {
                RouterController.this.sqlConnection = sqlConnection;
                handle();
            });
            return;
        }

        handle();
    }

    private void handle() {
        requestHandler().onSuccess(voidAsyncResult -> {
            try {
                routerRestController.handler(this);
            } catch (Exception e) {
                logger.error("Fail to handler" , e);
                serverError();
            }
        });

    }

    private Future<Void> requestHandler() {
        final Promise<Void> promise = Promise.promise();

        if (rest.dto() == null || rest.dto().getName().equals(NothingDto.class.getName())) {
            logger.info("Rest.dto is null");
            promise.complete();
            return promise.future();
        }

        if (rest.consumes().equals("application/json")) {
            try {

                final RequestBody body = routingContext.body();

                if (body == null) {
                    logger.error("Fail to get body");
                    promise.fail(new Throwable("Fail to get body"));
                    badRequest();
                } else {
                    final JsonObject bodyJson;
                    try {
                        bodyJson = new JsonObject(body.asString());

                        request = (REQUEST) DtoMapper.toDto(bodyJson , rest.dto());
                        logger.info("Successfully mapping request: {}" , request);

                        final Class<?>[] validator = rest.validator();
                        if (validator != null && validator.length > 0) {
                            try {
                                for (final Class<?> clazz : validator) {
                                    final Validation<REQUEST> validation = (Validation<REQUEST>) clazz.getConstructor().newInstance();
                                    validation.validation(request);
                                }
                            } catch (ResponseException e) {
                                logger.error("Fail to validation" , e);
                                fail(new ServerResponse<>(e.response));
                                return promise.future();
                            } catch (Exception e) {
                                logger.error("Fail to validation" , e);
                                badRequest();
                                promise.fail(e);
                                return promise.future();
                            }
                        }

                        promise.complete();

                    } catch (Exception e) {
                        logger.error("Fail to body string to json: {}" , body.toString());
                        promise.fail(e);
                        badRequest();
                    }
                }
            } catch (Exception e) {
                logger.error("Fail to request handler" , e);
                promise.fail(e);
                badRequest();
            }
        } else promise.complete();

        return promise.future();
    }

    private void serverError() {
        fail(new ServerResponse<>(Response.SERVER_ERROR));
    }

    private void badRequest() {
        fail(new ServerResponse<>(Response.BAD_REQUEST));
    }

    private void closeVertx() {
        vertx.close(voidAsyncResult -> {
            if (voidAsyncResult.failed()) {
                logger.error("Fail to close vertx" , voidAsyncResult.cause());
                return;
            }

            logger.info("Successfully close vertx");
        });
    }

    private void end() {
        closeVertx();
    }

    private Future<SQLConnection> connectDbResponse() {
        logger.info("starting connect db response");

        final Promise<SQLConnection> promise = Promise.promise();

        DbConnection.connect(vertx)
                .onSuccess(sqlConnection -> {
                    logger.info("Successfully connect to database");
                    promise.complete(sqlConnection);
                })
                .onFailure(fail -> {
                    logger.info("Fail to connect database" , fail);
                    serverError();
                    promise.fail(fail);
                });

        return promise.future();
    }

    @Override
    public void fail(final ServerResponse<RESPONSE> response) {
        complete(true , null , response);
    }

    @Override
    public void fail(final Throwable throwable , final ServerResponse<RESPONSE> response) {
        complete(true , throwable , response);
    }

    @Override
    public void success(final ServerResponse<RESPONSE> response) {
        complete(false , null , response);
    }

    @Override
    public void fail(final Throwable throwable , final RESPONSE info , final Response response) {
        complete(false , throwable , new ServerResponse<>(info , response));
    }

    @Override
    public void fail(final RESPONSE info , final Response response) {
        complete(false , null , new ServerResponse<>(info , response));
    }

    @Override
    public void fail(final Response response) {
        complete(true , null , new ServerResponse<>(response));
    }

    @Override
    public void success(final RESPONSE info , final Response response) {
        complete(false , null , new ServerResponse<>(info , response));
    }

    @Override
    public void success(Response response) {
        complete(false , null , new ServerResponse<>(response));
    }

    private void complete(final boolean fail , final Throwable throwable , final ServerResponse<RESPONSE> response) {
        logger.info("Result router starting...");

        if (fail) {
            logger.error("Fail router" , throwable);

            DbConnection.rollback(sqlConnection);
            RemoveUploadedFile.remove(routingContext);

            if (response == null) {
                logger.error("Response message message is null" , throwable);
                ResponseHandler.response(routingContext , Response.SERVER_ERROR);
                return;
            }

            ResponseHandler.response(routingContext , response.getResponse());

            promise.fail(throwable);

            return;
        }

        logger.info("Successfully router: {}" , response);

        final ResponseHandler<RESPONSE> responseHandler = ResponseHandler.response(routingContext);
        if (response.getInfo() != null) responseHandler.setInfo(response.getInfo());
        responseHandler.setResponse(response.getResponse());
        responseHandler.apply();

        DbConnection.commitClose(sqlConnection);

        promise.complete();
    }

    public REQUEST request() {
        return request;
    }

    @Override
    public RoutingContext routingContext() {
        return routingContext;
    }

    @Override
    public Vertx vertx() {
        return super.getVertx();
    }

    @Override
    public SQLConnection sqlConnection() {
        return sqlConnection;
    }


}
