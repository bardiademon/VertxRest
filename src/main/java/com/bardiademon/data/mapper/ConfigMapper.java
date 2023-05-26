package com.bardiademon.data.mapper;

import com.bardiademon.conf.Config;
import com.google.gson.Gson;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ConfigMapper {
    private final static Logger logger = LogManager.getLogger(ConfigMapper.class);

    public static Config jsonConfigToClassConfig(final JsonObject jsonObject) {
        logger.info("Mapping: {}" , jsonObject.toString());
        return (new Gson()).fromJson(jsonObject.toString() , Config.class);
    }
}
