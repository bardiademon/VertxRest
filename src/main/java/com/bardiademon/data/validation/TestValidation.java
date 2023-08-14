package com.bardiademon.data.validation;

import com.bardiademon.data.dto.TestDto;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.excp.ResponseException;

public final class TestValidation extends Validation<TestDto> {

    @Override
    public void validation(final TestDto testDto) throws ResponseException {

        if (testDto == null) return;

        if (testDto.name == null || testDto.name.isEmpty() || testDto.name.length() > 10) {
            throw new ResponseException(Response.INVALID_NAME);
        }
    }
}
