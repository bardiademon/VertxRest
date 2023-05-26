package com.bardiademon.rest;

import com.bardiademon.controller.Handler;
import com.bardiademon.controller.Rest;
import com.bardiademon.controller.RestController;
import com.bardiademon.data.dto.NothingDto;
import com.bardiademon.data.enums.Response;

@Rest(db = false)
public final class ResponseNotFound extends RestController<NothingDto, NothingDto> {
    @Override
    public void handler(final Handler<NothingDto, NothingDto> handler) throws Exception {
        handler.fail(Response.RESPONSE_NOT_FOUND);
    }
}
