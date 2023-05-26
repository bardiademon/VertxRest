package com.bardiademon.controller;

public abstract class RestController<REQUEST, RESPONSE> {

    public RestController() {
    }

    abstract public void handler(final Handler<REQUEST, RESPONSE> handler) throws Exception;
}
