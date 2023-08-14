package com.bardiademon.service;

import com.bardiademon.controller.RestController;
import com.bardiademon.utils.RepositoryUtil;
import io.vertx.core.AsyncResult;
import io.vertx.ext.sql.ResultSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Service extends RepositoryUtil {

    protected final Logger logger;

    protected Service() {
        logger = LogManager.getLogger(this);
    }
}
