package com.bardiademon.utils;

import com.bardiademon.controller.handler.ResponseHandler;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

public final class RemoveUploadedFile {

    private final static Logger logger = LogManager.getLogger(ResponseHandler.class);


    private RemoveUploadedFile() {
    }

    public synchronized static void remove(final RoutingContext routingContext) {
        new Thread(() -> {

            logger.info("remove uploaded file is starting... ");

            try {
                final List<FileUpload> fileUploads = routingContext.fileUploads();

                if (!fileUploads.isEmpty()) {
                    for (final FileUpload fileUpload : fileUploads) {
                        final File file = new File(fileUpload.uploadedFileName());
                        if (file.exists()) {
                            final boolean remove = file.delete();
                            logger.info("Deleted {}: {}" , file.getAbsolutePath() , remove);
                        } else logger.warn("File not exists: {}" , file.getAbsolutePath());
                    }

                    logger.info("Successfully remove files");

                }
            } catch (Exception e) {
                logger.error("Fail to get file uploads" , e);
            }

        }).start();
    }
}
