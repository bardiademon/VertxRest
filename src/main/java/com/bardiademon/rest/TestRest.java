package com.bardiademon.rest;

import com.bardiademon.controller.Handler;
import com.bardiademon.controller.RestController;
import com.bardiademon.controller.Rest;
import com.bardiademon.data.dto.TestDto;
import com.bardiademon.data.enums.RequestMethod;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.enums.UserRole;
import com.bardiademon.data.validation.TestValidation;

@Rest(method = RequestMethod.POST, path = {"/tst" , "/test"}, db = false, dto = TestDto.class, validator = TestValidation.class)
public final class TestRest extends RestController<TestDto, TestDto> {
    @Override
    public void handler(final Handler<TestDto, TestDto> handler) throws Exception {
        handler.success(handler.request() , Response.TEST);
    }
}
