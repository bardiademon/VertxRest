package com.bardiademon.rest;

import com.bardiademon.controller.handler.RestHandler;
import com.bardiademon.controller.annotation.Rest;
import com.bardiademon.controller.RestController;
import com.bardiademon.data.dto.NothingDto;
import com.bardiademon.data.enums.Response;

@Rest(db = false)
public final class ResponseNotFound extends RestController<NothingDto, NothingDto> {
    @Override
    public void handler(final RestHandler<NothingDto, NothingDto> restHandler) throws Exception {
        restHandler.fail(Response.RESPONSE_NOT_FOUND);
    }
}
