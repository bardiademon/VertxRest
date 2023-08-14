package com.bardiademon.rest;

import com.bardiademon.controller.handler.RestHandler;
import com.bardiademon.controller.annotation.Rest;
import com.bardiademon.controller.RestController;
import com.bardiademon.data.dto.NothingDto;
import com.bardiademon.data.enums.RequestMethod;
import com.bardiademon.data.enums.UserRole;

@Rest(method = RequestMethod.POST, path = "/authentication", authentication = true, roles = UserRole.ADMIN)
public class TestAuthentication extends RestController<NothingDto, String> {
    @Override
    public void handler(final RestHandler<NothingDto, String> restHandler) throws Exception {
        restHandler.success(String.format("Hi, %s %s" , restHandler.user().getFirstName() , restHandler.user().getLastName()));
    }
}
