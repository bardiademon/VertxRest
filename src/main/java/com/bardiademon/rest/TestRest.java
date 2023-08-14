package com.bardiademon.rest;

import com.bardiademon.controller.handler.RestHandler;
import com.bardiademon.controller.RestController;
import com.bardiademon.controller.annotation.Rest;
import com.bardiademon.data.dto.TestDto;
import com.bardiademon.data.enums.RequestMethod;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.validation.TestValidation;

@Rest(method = RequestMethod.POST, path = {"/tst" , "/test"}, db = false, dto = TestDto.class, validator = TestValidation.class)
public final class TestRest extends RestController<TestDto, TestDto> {

    public TestRest() {
        logger.trace("Test Rest");
    }

    @Override
    public void handler(final RestHandler<TestDto, TestDto> restHandler) throws Exception {
        restHandler.success(restHandler.request() , Response.TEST);
    }
}
