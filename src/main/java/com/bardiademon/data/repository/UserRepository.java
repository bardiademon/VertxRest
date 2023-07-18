package com.bardiademon.data.repository;

import com.bardiademon.data.entity.UserEntity;
import io.vertx.core.Future;
import io.vertx.ext.sql.SQLConnection;

public interface UserRepository {

    Future<UserEntity> fetchUserById(final SQLConnection sqlConnection , final long userId);
}
