package com.bardiademon.controller;

import com.bardiademon.Application;
import com.bardiademon.controller.adapter.*;
import com.bardiademon.data.enums.DateTimeFormatPattern;
import com.bardiademon.data.enums.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@ToString
public final class ResponseHandler<T> {

    private final static Logger logger = LogManager.getLogger(ResponseHandler.class);

    private static final Gson GSON;

    private final HttpServerResponse httpServerResponse;

    private Response response;
    private T info;

    private final RoutingContext routingContext;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls()
                .registerTypeAdapter(LocalDateTime.class , new LocalDateTimeGsonAdapter())
                .registerTypeAdapter(JsonObject.class , new JsonObjectGsonAdapter())
                .registerTypeAdapter(JsonArray.class , new JsonArrayGsonAdapter())
                .registerTypeAdapter(Time.class , new TimeGsonAdapter())
                .registerTypeAdapter(LocalDate.class , new LocalDateGsonAdapter());
        GSON = gsonBuilder.create();
    }

    private ResponseHandler(final RoutingContext routingContext) {
        this.routingContext = routingContext;
        httpServerResponse = routingContext.response();
    }

    public static <T> ResponseHandler<T> response(final RoutingContext routingContext) {
        return new ResponseHandler<>(routingContext);
    }

    public static void response(final RoutingContext routingContext , final Response response) {
        final ResponseHandler<Void> responseHandler = ResponseHandler.response(routingContext);
        responseHandler.setResponse(response);
        responseHandler.apply();
    }

    public void apply() {

        logger.info("Apply response model: {}" , this);

        httpServerResponse.setStatusCode(response.statusCode);

        final JsonObject response = new JsonObject()
                .put("code" , this.response.name())
                .put("message" , this.response.message)
                .put("info" , info());

        logger.trace("Response: {}" , response);

        Application.getConfig().responseHeader().keySet().forEach(key -> httpServerResponse.headers().add(key , Application.getConfig().responseHeader().get(key).getAsString()));
        httpServerResponse.headers().add("Content-Type" , "application/json");
        httpServerResponse.headers().add("Content-Length" , String.valueOf(response.encode().length()));

        httpServerResponse.write(response.encode());
        httpServerResponse.end();
        httpServerResponse.close();
    }

    public Object info() {
        try {

            if (info == null) {
                return null;
            }

            if (info instanceof String || info instanceof Short || info instanceof Integer || info instanceof Long || info instanceof Float || info instanceof Double || info instanceof Byte) {
                return info;
            }

            if (info instanceof Enum<?>) {
                return info;
            }

            if (info instanceof final LocalDateTime localDateTime) {
                return localDateTime.format(DateTimeFormatPattern.DATE_TIME_PATTERN.pattern);
            }
            if (info instanceof final LocalDate localDate) {
                return DateTimeFormatPattern.DATE_PATTERN.pattern.format(localDate);
            }
            if (info instanceof final LocalTime localTime) {
                return localTime.format(DateTimeFormatPattern.TIME_PATTERN.pattern);
            }
            if (info instanceof final Time time) {
                return time.toLocalTime().format(DateTimeFormatPattern.TIME_PATTERN.pattern);
            }

            final String jsonStr = GSON.toJson(info);
            try {
                return new JsonObject(jsonStr);
            } catch (Exception ex1) {
                logger.error("Json string is not a json object: {}" , jsonStr , ex1);
                try {
                    return new JsonArray(jsonStr);
                } catch (Exception ex2) {
                    logger.error("Json string is not a json array: {}" , jsonStr , ex2);
                }
            }
        } catch (Exception ex) {
            logger.error("Fail to mapping info: {}" , info , ex);
        }

        return info;
    }
}
