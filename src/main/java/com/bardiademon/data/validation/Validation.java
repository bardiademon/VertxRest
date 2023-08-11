package com.bardiademon.data.validation;

import com.bardiademon.controller.RestController;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.excp.ResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Validation<DTO> {

    protected final Logger logger;

    public Validation() {
        final Class<?> subclass = RestController.class.getDeclaringClass();
        logger = LogManager.getLogger(subclass == null ? this : subclass);
    }

    abstract public void validation(final DTO dto) throws ResponseException;

    protected void initial(final DTO dto) throws ResponseException {
        if (dto == null) throw new ResponseException(Response.BAD_REQUEST);
    }
}
