package com.bardiademon;

import com.bardiademon.controller.ServerController;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Application {

    Logger logger = LogManager.getLogger(Application.class);

    static void main(final String[] args) {
        logger.info("Powered by [Java,Vertx,bardiademon]");

        Vertx.vertx().deployVerticle(ServerController.class.getName())
                .onSuccess(success -> logger.info("Successfully server: {}" , success))
                .onFailure(fail -> logger.error("Fail server" , fail));
    }

}
