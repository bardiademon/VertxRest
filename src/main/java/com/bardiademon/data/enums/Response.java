package com.bardiademon.data.enums;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum Response {
    BAD_REQUEST(HttpResponseStatus.BAD_REQUEST.code() , "Bad Request"),
    SERVER_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR.code() , "Internal Server Error"),
    SUCCESSFULLY(HttpResponseStatus.OK.code() , "Successfully"),
    TEST(HttpResponseStatus.OK.code() , "This is a test message"),
    INVALID_NAME(HttpResponseStatus.BAD_REQUEST.code() , "This name is invalid"),
    RESPONSE_NOT_FOUND(HttpResponseStatus.NOT_FOUND.code() , "This path not found"),
    FAIL_AUTHENTICATION(HttpResponseStatus.UNAUTHORIZED.code() , "Unauthorized access to the requested resource requires authentication"),
    USER_NOT_FOUND(HttpResponseStatus.BAD_REQUEST.code() , "This user not found"),
    INVALID_EMAIL(HttpResponseStatus.BAD_REQUEST.code() , "This email is invalid"),
    INVALID_PASSWORD(HttpResponseStatus.BAD_REQUEST.code() , "This password is invalid"),
    //
    ;
    private final static Logger logger = LogManager.getLogger(Response.class);
    public final int statusCode;
    public final String message;

    Response(final int statusCode , final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public static Response toEnum(final String name) {
        try {
            return valueOf(name);
        } catch (Exception e) {
            logger.error("Fail to enum" , e);
        }
        return null;
    }
}
