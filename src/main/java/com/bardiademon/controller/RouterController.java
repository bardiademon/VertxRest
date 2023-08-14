package com.bardiademon.controller;

import com.bardiademon.controller.annotation.NonSingleton;
import com.bardiademon.controller.annotation.Rest;
import com.bardiademon.controller.handler.RestHandler;
import com.bardiademon.controller.handler.JwtHandler;
import com.bardiademon.controller.handler.ResponseHandler;
import com.bardiademon.data.Model.ServerResponse;
import com.bardiademon.data.dto.NothingDto;
import com.bardiademon.data.entity.UserEntity;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.enums.UserRole;
import com.bardiademon.data.excp.ResponseException;
import com.bardiademon.data.mapper.DtoMapper;
import com.bardiademon.data.repository.UserRepository;
import com.bardiademon.data.validation.Validation;
import com.bardiademon.service.UserService;
import com.bardiademon.utils.DbConnection;
import com.bardiademon.utils.RemoveUploadedFile;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public final class RouterController<REQUEST, RESPONSE> extends AbstractVerticle implements RestHandler<REQUEST, RESPONSE> {

    private static final Map<String, Validation<?>> validations = new HashMap<>();

    private final static Logger logger = LogManager.getLogger(RouterController.class);
    private final RoutingContext routingContext;
    private final RestController<REQUEST, RESPONSE> routerRestController;
    private final Rest rest;
    private SQLConnection sqlConnection;
    private REQUEST request;
    private final UserRepository userRepository;
    private Promise<Void> promise;
    private UserEntity userEntity;

    private RouterController(final RoutingContext routingContext , final RestController<REQUEST, RESPONSE> routerRestController , final Rest rest) {
        this.routingContext = routingContext;
        this.routerRestController = routerRestController;
        this.userRepository = new UserService();
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
                logger.trace("Starting rest controller, Class: {} , Request: {}" , routerRestController.getClass().getName() , request);
                routerRestController.handler(this);
            } catch (Exception e) {
                logger.error("Fail to handler" , e);
                serverError();
            }
        }).onFailure(fail -> {
            logger.error("Fail to request handler");
            fail(fail);
        });

    }

    private Future<Void> requestHandler() {
        final Promise<Void> promise = Promise.promise();

        Future.all(authentication() , body()).onSuccess(success -> {

            logger.trace("Successfully authentication: {}" , userEntity);
            logger.trace("Successfully body");

            promise.complete();

        }).onFailure(fail -> {
            logger.error("Fail to [authentication,body]" , fail);
            promise.fail(fail);
        });

        return promise.future();
    }


    private Future<Void> body() {

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
                    return promise.future();
                }

                final JsonObject bodyJson;
                try {
                    bodyJson = new JsonObject(body.asString());

                    request = (REQUEST) DtoMapper.toDto(bodyJson , rest.dto());
                    logger.trace("Successfully mapping request: {}" , request);

                    final Class<?>[] validator = rest.validator();
                    if (validator != null && validator.length > 0) {
                        try {
                            for (final Class<?> clazz : validator) {
                                final Validation<REQUEST> validation;
                                if (clazz.getAnnotation(NonSingleton.class) == null) {
                                    if (validations.containsKey(clazz.getName())) {
                                        validation = (Validation<REQUEST>) validations.get(clazz.getName());
                                    } else {
                                        validation = (Validation<REQUEST>) clazz.getConstructor().newInstance();
                                        validations.put(clazz.getName() , validation);
                                    }
                                } else {
                                    validation = (Validation<REQUEST>) clazz.getConstructor().newInstance();
                                }
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

            } catch (Exception e) {
                logger.error("Fail to request handler" , e);
                promise.fail(e);
                badRequest();
            }
        } else promise.complete();

        return promise.future();
    }

    private Future<Void> authentication() {
        final Promise<Void> promise = Promise.promise();

        if (!rest.authentication()) {
            logger.warn("authentication is disable");
            promise.complete();
            return promise.future();
        }

        if (!rest.db() || sqlConnection == null) {
            logger.error("Fail to authentication");
            promise.fail(new ResponseException(Response.FAIL_AUTHENTICATION));
            return promise.future();
        }

        final String authentication = routingContext.request().getHeader(ServerController.getConfig().signInJwtConfig().headerName());

        if (authentication == null || authentication.trim().isEmpty()) {
            promise.fail(new ResponseException(Response.FAIL_AUTHENTICATION));
            return promise.future();
        }

        final String[] authenticationSplit = authentication.trim().split(" ");
        if (authenticationSplit.length != 2 || !authenticationSplit[0].equals("Bearer") || authenticationSplit[1] == null || authenticationSplit[1].trim().isEmpty()) {
            promise.fail(new ResponseException(Response.FAIL_AUTHENTICATION));
            return promise.future();
        }

        final String token = authenticationSplit[1].trim();
        JwtHandler.getJwtHandler().authenticate(token).onSuccess(user -> {
            try {
                if (user.expired()) {
                    logger.error("Expired token: {}" , user);
                    promise.fail(new ResponseException(Response.FAIL_AUTHENTICATION));
                    return;
                }

                logger.info("Successfully authenticate: {}" , user);

                final long id = user.principal().getLong(ServerController.getConfig().signInJwtConfig().claimsUserIdKey());

                userRepository.fetchUserById(sqlConnection , id).onSuccess(userEntity -> {

                    logger.info("Successfully fetch user by id: {}" , userEntity);

                    final Set<UserRole> userRoles = userEntity.getUserRoles();
                    if (rest.roles() != null && rest.roles().length > 0 && !Arrays.asList(rest.roles()).contains(UserRole.ANY)
                            && (userRoles == null || userRoles.isEmpty() || userRoles.stream().noneMatch(item -> UserRole.admins().contains(item) || Arrays.asList(rest.roles()).contains(item)))
                    ) {
                        logger.error("Access denied -> Rest: {}, User: {}" , rest , userEntity);
                        promise.fail(new ResponseException(Response.ACCESS_DENIED));
                        return;
                    }

                    logger.trace("Access was successfully granted: RestRoles: {} , UserRoles: {}" , rest.roles() , userEntity.getUserRoles());

                    this.userEntity = userEntity;
                    promise.complete();

                }).onFailure(fail -> {
                    logger.error("Fail to fetch user by id: {}" , user , fail);
                    promise.fail(new ResponseException(Response.FAIL_AUTHENTICATION));
                });

            } catch (Exception e) {
                logger.error("Fail to authenticate: {}" , token , e);
                promise.fail(new ResponseException(Response.FAIL_AUTHENTICATION));
            }

        }).onFailure(fail -> {
            logger.error("Fail to authenticate: {}" , token , fail);
            promise.fail(new ResponseException(Response.FAIL_AUTHENTICATION));
        });

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

    private Future<SQLConnection> connectDbResponse() {
        logger.info("Starting connect db response");

        final Promise<SQLConnection> promise = Promise.promise();

        DbConnection.connect(vertx)
                .onSuccess(sqlConnection -> {
                    logger.trace("Successfully connect to database");
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
    public void fail(final Throwable throwable) {
        complete(true , throwable , null);
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
    public void success(RESPONSE info) {
        complete(false , null , new ServerResponse<>(info , Response.SUCCESSFULLY));
    }

    @Override
    public void success(Response response) {
        complete(false , null , new ServerResponse<>(response));
    }

    private void complete(final boolean fail , final Throwable throwable , final ServerResponse<RESPONSE> serverResponse) {
        logger.info("Result router starting...");

        if (fail) {
            logger.error("Fail router" , throwable);

            DbConnection.rollback(sqlConnection);
            RemoveUploadedFile.remove(routingContext);

            if (serverResponse == null) {
                logger.error("Response message message is null" , throwable);

                try {
                    ResponseHandler.response(routingContext , Response.valueOf(throwable.getMessage()));
                } catch (Exception e) {
                    logger.error("Fail throwable message to response value" , throwable);
                    ResponseHandler.response(routingContext , Response.SERVER_ERROR);
                }

                promise.fail(throwable);
                return;
            }

            ResponseHandler.response(routingContext , serverResponse.getResponse());

            promise.fail(throwable);
            return;
        }

        logger.trace("Successfully router: {}" , serverResponse);

        final ResponseHandler<RESPONSE> responseHandler = ResponseHandler.response(routingContext);
        if (serverResponse.getInfo() != null) responseHandler.setInfo(serverResponse.getInfo());
        responseHandler.setResponse(serverResponse.getResponse());
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

    @Override
    public UserEntity user() {
        return userEntity;
    }


}
