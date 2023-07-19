package com.bardiademon.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RestController<REQUEST, RESPONSE> {

    protected final Logger logger;

    public RestController() {
        final Class<?> subclass = RestController.class.getDeclaringClass();
        logger = LogManager.getLogger(subclass == null ? this : subclass);
    }

    abstract public void handler(final Handler<REQUEST, RESPONSE> handler) throws Exception;
}
