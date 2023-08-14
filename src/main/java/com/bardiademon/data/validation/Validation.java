package com.bardiademon.data.validation;

import com.bardiademon.controller.RestController;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.excp.ResponseException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Validation<DTO> {

    protected final Logger logger;

    public Validation() {
        logger = LogManager.getLogger(this);
    }

    abstract public void validation(final DTO dto) throws ResponseException;

    protected void initial(final DTO dto) throws ResponseException {
        if (dto == null) throw new ResponseException(Response.BAD_REQUEST);
    }
}
