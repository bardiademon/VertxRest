package com.bardiademon.rest;

import com.bardiademon.controller.Handler;
import com.bardiademon.controller.Rest;
import com.bardiademon.controller.RestController;
import com.bardiademon.data.dto.NothingDto;
import com.bardiademon.data.enums.RequestMethod;

@Rest(method = RequestMethod.POST, path = "/authentication", authentication = true)
public class TestAuthentication extends RestController<NothingDto, String> {
    @Override
    public void handler(final Handler<NothingDto, String> handler) throws Exception {
        handler.success(String.format("Hi, %s %s" , handler.user().getFirstName() , handler.user().getLastName()));
    }
}
