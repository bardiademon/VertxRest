package com.bardiademon.data.validation;

import com.bardiademon.data.enums.Response;
import com.bardiademon.data.excp.ResponseException;

public abstract class Validation<DTO> {
    public Validation() {
    }

    abstract public void validation(final DTO dto) throws ResponseException;

    protected void initial(final DTO dto) throws ResponseException {
        if (dto == null) throw new ResponseException(Response.BAD_REQUEST);
    }
}
