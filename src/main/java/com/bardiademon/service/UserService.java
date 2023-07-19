package com.bardiademon.service;

import com.bardiademon.data.entity.UserEntity;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.excp.ResponseException;
import com.bardiademon.data.mapper.UserMapper;
import com.bardiademon.data.repository.UserRepository;
import com.bardiademon.utils.RepositoryUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

public final class UserService extends Service implements UserRepository {

    @Override
    public Future<UserEntity> fetchUserById(final SQLConnection sqlConnection , final long userId) {
        final Promise<UserEntity> promise = Promise.promise();

        final String query = """
                select "id","first_name","last_name","email","created_at","deleted_at","updated_at","deleted","description" from "users" where "deleted" = false and "id" = ?
                """;

        final JsonArray params = new JsonArray()
                .add(userId);

        logger.trace("Executing -> Query: {} , Params: {} , ParamsSize: {}" , query , params , params.size());
        sqlConnection.queryWithParams(query , params , resultHandler -> {

            if (resultHandler.failed()) {
                logger.error("Fail to fetch user by id: {}" , userId , resultHandler.cause());
                promise.fail(new ResponseException(Response.SERVER_ERROR));
                return;
            }

            if (RepositoryUtil.isEmptySelect(resultHandler)) {
                logger.error("Not found user, UserId: {}" , userId);
                promise.fail(new ResponseException(Response.USER_NOT_FOUND));
                return;
            }

            final JsonObject row = resultHandler.result().getRows().get(0);

            logger.info("Successfully fetch user by id: {}" , row);

            final UserEntity userEntity = UserMapper.toUserEntity(row);
            if (userEntity == null) {
                logger.error("UserEntity is null, UserId: {}" , userId);
                promise.fail(new ResponseException(Response.USER_NOT_FOUND));
                return;
            }

            promise.complete(userEntity);

        });

        return promise.future();
    }

    @Override
    public Future<UserEntity> fetchUserByEmailAndPassword(final SQLConnection sqlConnection , final String email , final String password) {
        final Promise<UserEntity> promise = Promise.promise();

        final String query = """
                select "id","first_name","last_name","email","created_at","deleted_at","updated_at","deleted","description" from "users" where "deleted" = false and "email" = ? and "password" = ?
                """;

        final JsonArray params = new JsonArray()
                .add(email)
                .add(password);

        logger.trace("Executing -> Query: {} , Params: {} , ParamsSize: {}" , query , params , params.size());
        sqlConnection.queryWithParams(query , params , resultHandler -> {

            if (resultHandler.failed()) {
                logger.error("Fail to fetch user by email[{}] and password[{}]" , email , password , resultHandler.cause());
                promise.fail(new ResponseException(Response.SERVER_ERROR));
                return;
            }

            if (RepositoryUtil.isEmptySelect(resultHandler)) {
                logger.error("Not found user, email[{}] and password[{}]" , email , password);
                promise.fail(new ResponseException(Response.USER_NOT_FOUND));
                return;
            }

            final JsonObject row = resultHandler.result().getRows().get(0);

            logger.info("Successfully fetch user by email[{}] , Result: {}" , email , row);

            final UserEntity userEntity = UserMapper.toUserEntity(row);
            if (userEntity == null) {
                logger.error("UserEntity is null, email[{}] and password[{}]" , email , password);
                promise.fail(new ResponseException(Response.USER_NOT_FOUND));
                return;
            }

            promise.complete(userEntity);

        });

        return promise.future();
    }
}
