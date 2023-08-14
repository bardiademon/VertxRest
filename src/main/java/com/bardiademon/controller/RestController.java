package com.bardiademon.controller;

import com.bardiademon.controller.handler.RestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RestController<REQUEST, RESPONSE> {

    protected final Logger logger;

    public RestController() {
        logger = LogManager.getLogger(this);
        logger.trace("New instance rest controller: {}" , this);
    }

    abstract public void handler(final RestHandler<REQUEST, RESPONSE> restHandler) throws Exception;
}
