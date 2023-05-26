package com.bardiademon.controller;

import com.bardiademon.data.dto.NothingDto;
import com.bardiademon.data.enums.Response;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
@Setter
@ToString
public final class ResponseHandler<T> {

    private final static Logger logger = LogManager.getLogger(ResponseHandler.class);

    private final HttpServerResponse httpServerResponse;

    private Response response;
    private T info;

    private final RoutingContext routingContext;

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
                .put("info" , info instanceof NothingDto ? null : info);

        httpServerResponse.headers().add("Content-Type" , "application/json");
        httpServerResponse.headers().add("Content-Length" , String.valueOf(response.toString().length()));

        httpServerResponse.write(response.toString());
        httpServerResponse.end();
        httpServerResponse.close();
    }
}
