package com.bardiademon.utils.DocumentsDirectory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author @bardiademon
 */
public class DocumentsDirectory {
    private static final Logger logger = LogManager.getLogger(DocumentsDirectory.class);

    private static final String DEFAULT_DOCS_DIR_NAME = "docs";

    protected DocumentsDirectory() {
    }

    public static File getDir(final DocsDirName docsDirName) throws IOException {

        final String docs = System.getProperty("user.dir") + File.separator + DEFAULT_DOCS_DIR_NAME;

        final File directory = new File(docs + File.separator + docsDirName.dirName);

        if (!directory.exists() && !directory.mkdirs()) {
            logger.error("Fail to create directory: {}" , directory.getPath());
            throw new IOException("Fail to create directory: " + directory.getPath());
        }

        return directory;
    }
}
