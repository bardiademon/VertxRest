package com.bardiademon.controller.handler;

import com.bardiademon.controller.ServerController;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;


@Getter
public final class JwtHandler {

    private final static Logger logger = LogManager.getLogger(JwtHandler.class);

    private final JWTOptions options;

    @Getter
    private static final JwtHandler jwtHandler;

    static {
        jwtHandler = new JwtHandler();
    }

    private JwtHandler() {
        options = new JWTOptions()
                .setSubject(ServerController.getConfig().signInJwtConfig().subject())
                .setIssuer(ServerController.getConfig().signInJwtConfig().issuer())
                .setIgnoreExpiration(ServerController.getConfig().signInJwtConfig().ignoreExpiration())
                .setExpiresInMinutes(ServerController.getConfig().signInJwtConfig().expiresInMinutes())
                .setAlgorithm(ServerController.getConfig().signInJwtConfig().algorithm());
    }

    public Future<User> authenticate(final String token) {
        logger.trace("Starting authenticate, Token: {}" , token);

        final Promise<User> promise = Promise.promise();

        ServerController.getJwt().authenticate(new TokenCredentials(token)).onSuccess(user -> {
            logger.trace("Successfully authenticate, Options: {} , Token: {} , User: {}" , options.toJson() , token , user);
            promise.complete(user);
        }).onFailure(fail -> {
            logger.error("Fail to authenticate: Options: {} , Token: {}" , options.toJson() , token , fail);
            promise.fail(fail);
        });

        return promise.future();
    }

    public String generate(final long userId) {
        logger.trace("Starting generate, UserId: {}" , userId);
        try {
            return ServerController.getJwt().generateToken(new JsonObject().put(ServerController.getConfig().signInJwtConfig().claimsUserIdKey() , userId) , options);
        } catch (Exception e) {
            logger.error("Fail to generateToken, UserId: {}" , userId , e);
        }
        return null;
    }

    public Future<JWTAuth> jwtConfig(final Vertx vertx) {
        final Promise<JWTAuth> promise = Promise.promise();

        final URL resourcePublicKey = ServerController.getResource(ServerController.getConfig().signInJwtConfig().publicKey());
        final URL resourcePrivateKey = ServerController.getResource(ServerController.getConfig().signInJwtConfig().privateKey());
        if (resourcePublicKey != null && resourcePrivateKey != null) {

            final Buffer publicKey = vertx.fileSystem().readFileBlocking(resourcePublicKey.getFile());
            final Buffer privateKey = vertx.fileSystem().readFileBlocking(resourcePrivateKey.getFile());

            final JWTAuth jwt = JWTAuth.create(vertx , new JWTAuthOptions()
                    .addPubSecKey(new PubSecKeyOptions()
                            .setAlgorithm(ServerController.getConfig().signInJwtConfig().algorithm())
                            .setBuffer(publicKey))
                    .addPubSecKey(new PubSecKeyOptions()
                            .setAlgorithm(ServerController.getConfig().signInJwtConfig().algorithm())
                            .setBuffer(privateKey)));

            promise.complete(jwt);
        } else promise.fail(new Exception("Jwt config error"));

        return promise.future();
    }
}
