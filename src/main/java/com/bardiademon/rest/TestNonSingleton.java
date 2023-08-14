package com.bardiademon.rest;

import com.bardiademon.controller.RestController;
import com.bardiademon.controller.annotation.NonSingleton;
import com.bardiademon.controller.annotation.Rest;
import com.bardiademon.controller.handler.RestHandler;
import com.bardiademon.data.dto.NothingDto;
import com.bardiademon.data.enums.RequestMethod;

@NonSingleton
@Rest(method = RequestMethod.POST, path = "/test/non-singleton", db = false)
public class TestNonSingleton extends RestController<NothingDto, Integer> {

    private static int counter = 0;

    public TestNonSingleton() {
        logger.trace("NEW INSTANCE: {} , COUNTER: {}" , TestNonSingleton.class , (++counter));
    }

    @Override
    public void handler(RestHandler<NothingDto, Integer> restHandler) throws Exception {
        restHandler.success(counter);
    }
}
