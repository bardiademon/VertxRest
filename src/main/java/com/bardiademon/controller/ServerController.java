package com.bardiademon.controller;

import com.bardiademon.Application;
import com.bardiademon.conf.Config;
import com.bardiademon.conf.ServerConfig;
import com.bardiademon.controller.annotation.NonSingleton;
import com.bardiademon.controller.annotation.Rest;
import com.bardiademon.controller.handler.JwtHandler;
import com.bardiademon.data.enums.RequestMethod;
import com.bardiademon.data.mapper.ConfigMapper;
import com.bardiademon.rest.ResponseNotFound;
import com.bardiademon.utils.DbConnection;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public final class ServerController extends AbstractVerticle {

    private final static Logger logger = LogManager.getLogger(Application.class);

    private static final Map<String, RestController<?, ?>> restClasses = new HashMap<>();

    @Getter
    private static Config config;

    @Getter
    private static JWTAuth jwt;

    private void serverReady() {
        logger.info("Successfully server ready");
    }

    @Override
    public void start(final Promise<Void> startPromise) {

        logger.info("Server starting...");

        final ConfigRetriever configRetriever = ConfigRetriever.create(vertx);
        configRetriever.getConfig().onSuccess(e ->
        {
            logger.info("Successfully get config: {}" , e);
            config = ConfigMapper.jsonConfigToClassConfig(e);
            logger.info("Successfully mapped config json file: {}" , config);

            JwtHandler.getJwtHandler().jwtConfig(vertx).onSuccess(jwtAuth -> {

                logger.info("Successfully jwt config: {}" , jwtAuth);

                ServerController.jwt = jwtAuth;

                DbConnection.connect(vertx).onSuccess(sqlConnection -> {
                    logger.info("Successfully connected to database: {}" , config.dbConfig());

                    final HttpServer httpServer = vertx.createHttpServer();
                    final Router router = Router.router(vertx);
                    routerHandler(router);
                    httpServer.requestHandler(router);

                    final ServerConfig serverConfig = config.serverConfig();
                    logger.info("Listing server: {}" , serverConfig);

                    final String host = serverConfig.host();
                    final int port = serverConfig.port();

                    httpServer.listen(port , host)
                            .onSuccess(successListen -> {
                                logger.info("Server run on {}:{}" , host , port);
                                serverReady();
                                startPromise.complete();
                            })
                            .onFailure(fail -> {
                                logger.error("Fail to run server on {}:{}" , host , port , fail);
                                startPromise.fail(fail);
                            });
                }).onFailure(fail -> {
                    logger.error("Fail to connect database" , fail);
                    startPromise.fail(fail);
                });
            }).onFailure(fail -> {
                logger.error("Fail to jwt config" , fail);
                startPromise.fail(fail);
            });


        }).onFailure(fail -> {
            logger.error("Fail to get config" , fail);
            startPromise.fail(fail);
        });
    }

    private void routerHandler(final Router router) {
        logger.trace("Starting router handler");

        router.route().handler(BodyHandler.create());

        if (config.serverConfig().statik().enable()) {
            final StaticHandler staticHandler = StaticHandler.create(config.serverConfig().statik().path());
            staticHandler.setDirectoryListing(config.serverConfig().statik().directoryListing());
            final Route route = router.route(config.serverConfig().statik().router());
            route.handler(staticHandler);
        }

        final Set<Class<? extends RestController>> classes = new Reflections(config.serverConfig().restPackage()).getSubTypesOf(RestController.class);
        for (final Class<?> clazz : classes) {
            final Rest rest = clazz.getAnnotation(Rest.class);
            if (rest != null) {
                final String[] paths = rest.path();
                if (paths != null && paths.length > 0) {
                    for (final String path : paths) {
                        final Route route = router.route();
                        route.path(path).produces(rest.produces()).consumes(rest.consumes());
                        if (rest.method() != null && !rest.method().equals(RequestMethod.ANY)) {
                            route.method(rest.method().httpMethod);
                        }
                        route.handler(routingContext -> requestHandler(routingContext , clazz , rest));
                        logger.info("Successfully set rest handler: {}" , rest);
                    }
                } else {
                    logger.error("Fail set router, Path is empty");
                }
            }
        }

        router.route().handler(routingContext -> requestHandler(routingContext , ResponseNotFound.class , ResponseNotFound.class.getAnnotation(Rest.class)));

        logger.info("Successfully set routers");
    }

    private void requestHandler(final RoutingContext routingContext , final Class<?> clazz , final Rest rest) {
        logger.trace("Starting request handler, Class: {} , Rest: {}" , clazz , rest);

        try {
            final RestController<?, ?> restController;
            if (clazz.getAnnotation(NonSingleton.class) == null) {
                if (restClasses.containsKey(clazz.getName())) {
                    restController = restClasses.get(clazz.getName());
                } else {
                    restController = (RestController<?, ?>) clazz.getConstructor().newInstance();
                    restClasses.put(clazz.getName() , restController);
                }
            } else {
                restController = (RestController<?, ?>) clazz.getConstructor().newInstance();
            }
            logger.trace("Starting router controller: {}" , rest);
            RouterController.handle(routingContext , restController , rest);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            logger.error("Fail to rest class, Class: {} , Rest: {}" , clazz , rest , e);
        }
    }

    public static URL getResource(final String path) {
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }
}
