package com.bardiademon.rest;

import com.bardiademon.Application;
import com.bardiademon.controller.Handler;
import com.bardiademon.controller.Rest;
import com.bardiademon.controller.RestController;
import com.bardiademon.data.dto.LoginDto;
import com.bardiademon.data.enums.RequestMethod;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.repository.UserRepository;
import com.bardiademon.data.validation.LoginValidation;
import com.bardiademon.service.UserService;
import io.vertx.core.json.JsonObject;

@Rest(method = RequestMethod.POST, path = "/users/login", dto = LoginDto.class, validator = LoginValidation.class)
public final class LoginRest extends RestController<LoginDto, String> {

    private final UserRepository userRepository = new UserService();

    @Override
    public void handler(final Handler<LoginDto, String> handler) throws Exception {
        userRepository.fetchUserByEmailAndPassword(handler.sqlConnection() , handler.request().email() , handler.request().password()).onSuccess(user -> {

            logger.info("Successfully fetch user by email and password: {}" , user);

            final String token;
            try {
                token = Application.jwt.generateToken(new JsonObject().put("user_id" , user.getId()));
            } catch (Exception e) {
                logger.error("Fail to create token" , e);
                handler.fail(Response.SERVER_ERROR);
                return;
            }

            logger.info("Successfully create token: {} , User: {}" , token , user);

            handler.success(token);

        }).onFailure(fail -> {
            logger.error("Fail to fetch user by email and password: {}" , handler , fail);
            handler.fail(fail);
        });

    }
}
