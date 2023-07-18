package com.bardiademon.data.mapper;

import com.bardiademon.data.entity.BaseEntity;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BaseMapper {

    private final static Logger logger = LogManager.getLogger(BaseMapper.class);

    private BaseMapper() {
    }

    public static BaseEntity toBaseEntity(final JsonObject row) {
        try {
            if (row == null || row.isEmpty()) throw new NullPointerException("Row is empty");
            return BaseEntity.builder()
                    .id(row.getLong("id"))
                    .deleted(row.getBoolean("deleted" , false))
                    .deletedAt(Mapper.toLocalDateTime(row , "deleted_at"))
                    .createdAt(Mapper.toLocalDateTime(row , "created_at"))
                    .updatedAt(Mapper.toLocalDateTime(row , "updated_at"))
                    .description(row.getString("description" , null))
                    .build();

        } catch (Exception e) {
            logger.error("Fail to map row to base entity: {}" , row , e);
        }
        return null;
    }
}
