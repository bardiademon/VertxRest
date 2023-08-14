package com.bardiademon.utils;

import com.bardiademon.conf.DbConfig;
import com.bardiademon.controller.ServerController;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.excp.ResponseException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DbConnection {
    private static final Logger logger = LogManager.getLogger(DbConnection.class);

    public static Future<SQLConnection> connect(final Vertx vertx) {
        final Promise<SQLConnection> promise = Promise.promise();

        final DbConfig config = ServerController.getConfig().dbConfig();

        logger.info("Connecting to database...");
        logger.info("Set config info: {}" , config);

        final JsonObject jsonConfig = new JsonObject()
                .put("url" , config.url())
                .put("driver_class" , config.driverClass())
                .put("user" , config.username())
                .put("password" , config.password());

        logger.info("Connecting to database...");

        final JDBCClient dbHandle = JDBCClient.createShared(vertx , jsonConfig);

        dbHandle.getConnection(e ->
        {
            if (e.succeeded()) {
                final SQLConnection sqlConnection = e.result();
                sqlConnection.setAutoCommit(false , voidAsyncResult -> {

                    if (voidAsyncResult.failed()) {
                        logger.info("Fail to set auto commit" , voidAsyncResult.cause());
                        promise.fail(new ResponseException(Response.SERVER_ERROR));
                        return;
                    }

                    logger.info("Database connection successfully");
                    promise.complete(sqlConnection);

                });

            } else {
                logger.error("Error connect to database" , e.cause());
                promise.fail(e.cause());
            }
        });

        return promise.future();
    }

    public synchronized static void close(final SQLConnection sqlConnection) {
        if (notNullSql(sqlConnection)) {
            try {
                logger.info("Closing sql connection");
                sqlConnection.close();

                logger.info("Successfully closed sql connection");

            } catch (Exception e) {
                logger.error("Fail to close sql connection" , e);
            }
        }
    }

    public synchronized static void commitClose(final SQLConnection sqlConnection) {
        if (notNullSql(sqlConnection)) {
            try {
                logger.info("Commit sql connection");

                sqlConnection.commit(voidAsyncResult -> {
                    if (voidAsyncResult.failed()) {
                        logger.error("Fail to commit sql connection" , voidAsyncResult.cause());
                        close(sqlConnection);
                        return;
                    }

                    logger.info("Successfully commit sql connection");
                    close(sqlConnection);

                });

            } catch (Exception e) {
                logger.error("Fail to commit sql connection" , e);
            }
        }
    }

    public synchronized static void rollback(final SQLConnection sqlConnection) {
        if (notNullSql(sqlConnection)) {
            sqlConnection.rollback(voidAsyncResult -> {
                if (voidAsyncResult.failed()) {
                    logger.error("Fail to rollback" , voidAsyncResult.cause());
                    close(sqlConnection);
                    return;
                }

                logger.info("Successfully rollback sql connection");
                close(sqlConnection);
            });
        }
    }

    private static boolean notNullSql(final SQLConnection sqlConnection) {
        if (sqlConnection == null) {
            logger.info("Sql connection is null");
            return false;
        }

        return true;
    }

}
