package com.bardiademon.data.validation;

import com.bardiademon.data.excp.ResponseException;

public abstract class Validation<DTO> {
    public Validation() {
    }

    abstract public void validation(final DTO dto) throws ResponseException;
}
