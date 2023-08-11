package com.bardiademon.data.mapper;

import com.bardiademon.data.entity.BaseEntity;
import com.bardiademon.data.entity.UserEntity;
import com.bardiademon.data.enums.UserRole;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

public final class UserMapper {

    private final static Logger logger = LogManager.getLogger(UserMapper.class);

    private UserMapper() {
    }

    public static UserEntity toUserEntity(final JsonObject row) {
        try {

            logger.trace("Starting mapping Row to user entity, Row: {}" , row);

            final BaseEntity baseEntity = BaseMapper.toBaseEntity(row);

            if (baseEntity == null) throw new NullPointerException("Base entity is null");

            return UserEntity.builder()
                    .id(baseEntity.getId())
                    .firstName(row.getString("first_name"))
                    .lastName(row.getString("last_name"))
                    .email(row.getString("email"))
                    .createdAt(baseEntity.getCreatedAt())
                    .updatedAt(baseEntity.getUpdatedAt())
                    .deleted(baseEntity.isDeleted())
                    .deletedAt(baseEntity.getDeletedAt())
                    .description(baseEntity.getDescription())
                    .userRoles(row.getValue("roles") == null ? null : new HashSet<>(new JsonArray(row.getString("roles")).stream().map(item -> UserRole.valueOf(item.toString())).toList()))
                    .build();

        } catch (Exception e) {
            logger.error("Fail to map row to user entity: {}" , row , e);
        }
        return null;
    }
}
