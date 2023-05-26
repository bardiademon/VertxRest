package com.bardiademon.data.enums;

import io.vertx.core.http.HttpMethod;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public enum RequestMethod implements Serializable {
    ANY(null),
    OPTIONS(HttpMethod.OPTIONS),

    GET(HttpMethod.GET),

    HEAD(HttpMethod.HEAD),

    POST(HttpMethod.POST),

    PUT(HttpMethod.PUT),

    DELETE(HttpMethod.DELETE),

    TRACE(HttpMethod.TRACE),

    CONNECT(HttpMethod.CONNECT),

    PATCH(HttpMethod.PATCH),

    PROPFIND(HttpMethod.PROPFIND),

    PROPPATCH(HttpMethod.PROPPATCH),

    MKCOL(HttpMethod.MKCOL),

    COPY(HttpMethod.COPY),

    MOVE(HttpMethod.MOVE),

    LOCK(HttpMethod.LOCK),

    UNLOCK(HttpMethod.UNLOCK),

    MKCALENDAR(HttpMethod.MKCALENDAR),

    VERSION_CONTROL(HttpMethod.VERSION_CONTROL),

    REPORT(HttpMethod.REPORT),

    CHECKOUT(HttpMethod.CHECKOUT),

    CHECKIN(HttpMethod.CHECKIN),

    UNCHECKOUT(HttpMethod.UNCHECKOUT),

    MKWORKSPACE(HttpMethod.MKWORKSPACE),

    UPDATE(HttpMethod.UPDATE),

    LABEL(HttpMethod.LABEL),

    MERGE(HttpMethod.MERGE),

    BASELINE_CONTROL(HttpMethod.BASELINE_CONTROL),

    MKACTIVITY(HttpMethod.MKACTIVITY),

    ORDERPATCH(HttpMethod.ORDERPATCH),

    ACL(HttpMethod.ACL),

    SEARCH(HttpMethod.SEARCH)
    //
    ;

    public final HttpMethod httpMethod;

}
