package com.bardiademon.rest;

import com.bardiademon.controller.handler.RestHandler;
import com.bardiademon.controller.handler.JwtHandler;
import com.bardiademon.controller.annotation.Rest;
import com.bardiademon.controller.RestController;
import com.bardiademon.data.dto.SignInDto;
import com.bardiademon.data.enums.RequestMethod;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.repository.UserRepository;
import com.bardiademon.data.validation.SignInValidation;
import com.bardiademon.service.UserService;

@Rest(method = RequestMethod.POST, path = "/users/sign-in", dto = SignInDto.class, validator = SignInValidation.class)
public final class SignInRest extends RestController<SignInDto, String> {

    private final UserRepository userRepository = new UserService();

    @Override
    public void handler(final RestHandler<SignInDto, String> restHandler) throws Exception {
        userRepository.fetchUserByEmailAndPassword(restHandler.sqlConnection() , restHandler.request().email() , restHandler.request().password()).onSuccess(user -> {

            logger.info("Successfully fetch user by email and password: {}" , user);

            final String token = JwtHandler.getJwtHandler().generate(user.getId());

            if (token == null || token.isEmpty()) {
                logger.error("Fail to create token");
                restHandler.fail(Response.SERVER_ERROR);
                return;
            }

            logger.info("Successfully create token: {} , User: {}" , token , user);

            restHandler.success(token);

        }).onFailure(fail -> {
            logger.error("Fail to fetch user by email and password: {}" , restHandler , fail);
            restHandler.fail(fail);
        });

    }
}
