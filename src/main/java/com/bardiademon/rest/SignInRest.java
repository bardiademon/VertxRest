package com.bardiademon.rest;

import com.bardiademon.Application;
import com.bardiademon.controller.Handler;
import com.bardiademon.controller.Rest;
import com.bardiademon.controller.RestController;
import com.bardiademon.data.dto.SignInDto;
import com.bardiademon.data.enums.RequestMethod;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.repository.UserRepository;
import com.bardiademon.data.validation.SignInValidation;
import com.bardiademon.service.UserService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;

@Rest(method = RequestMethod.POST, path = "/users/sign-in", dto = SignInDto.class, validator = SignInValidation.class)
public final class SignInRest extends RestController<SignInDto, String> {

    private final UserRepository userRepository = new UserService();

    @Override
    public void handler(final Handler<SignInDto, String> handler) throws Exception {
        userRepository.fetchUserByEmailAndPassword(handler.sqlConnection() , handler.request().email() , handler.request().password()).onSuccess(user -> {

            logger.info("Successfully fetch user by email and password: {}" , user);

            final String token;
            try {

                final JWTOptions jwtOptions = new JWTOptions()
                        .setSubject(Application.getConfig().signInJwtConfig().subject())
                        .setIssuer(Application.getConfig().signInJwtConfig().issuer())
                        .setIgnoreExpiration(Application.getConfig().signInJwtConfig().ignoreExpiration())
                        .setExpiresInMinutes(Application.getConfig().signInJwtConfig().expiresInMinutes())
                        .setAlgorithm(Application.getConfig().signInJwtConfig().algorithm());

                logger.trace("Jwt options: {} , User: {}" , jwtOptions.toJson() , user);

                token = Application.jwt.generateToken(new JsonObject().put("user_id" , user.getId()) , jwtOptions);
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
