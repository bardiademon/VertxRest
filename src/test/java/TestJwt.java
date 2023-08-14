import com.bardiademon.controller.ServerController;
import com.bardiademon.controller.handler.JwtHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.impl.jose.JWS;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

import java.net.URL;

public final class TestJwt extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(TestJwt.class.getName());
    }

    @Override
    public void start(final Promise<Void> promise) throws Exception {

        final URL resourcePublicKey = ServerController.getResource("pem/private_key.pem");
        final URL resourcePrivateKey = ServerController.getResource("pem/public.pem");

        if (resourcePublicKey != null && resourcePrivateKey != null) {

            final Buffer publicKey = vertx.fileSystem().readFileBlocking(resourcePublicKey.getFile());
            final Buffer privateKey = vertx.fileSystem().readFileBlocking(resourcePrivateKey.getFile());

            final JWTAuth jwt = JWTAuth.create(vertx , new JWTAuthOptions()
                    .addPubSecKey(new PubSecKeyOptions()
                            .setAlgorithm(JWS.HS256)
                            .setBuffer(publicKey))
                    .addPubSecKey(new PubSecKeyOptions()
                            .setAlgorithm(JWS.HS256)
                            .setBuffer(privateKey)));

            final String token = testGenerateToken(jwt);

            System.out.println("testGenerateToken(jwt) = " + token);

            jwt.authenticate(new TokenCredentials(token)).onSuccess(user -> {

                System.out.println("user.principal() = " + user.principal());

                promise.complete();

                vertx.close();

            }).onFailure(fail -> {
                fail.printStackTrace();
            });


        } else {
            new Exception("Jwt config error").printStackTrace();
            promise.fail(new Exception("Jwt config error"));
        }

    }

    private String testGenerateToken(final JWTAuth jwt) {
        try {
            return jwt.generateToken(new JsonObject().put("id" , 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
