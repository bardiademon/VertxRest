package com.bardiademon.data.excp;

import com.bardiademon.data.enums.Response;

public class ResponseException extends Exception {

    public final Response response;

    public ResponseException(final Response response) {
        super(response.name());
        this.response = response;
    }

}
