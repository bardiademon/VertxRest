package com.bardiademon.data.validation;

import com.bardiademon.data.dto.TestDto;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.excp.ResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class TestValidation extends Validation<TestDto> {
    public TestValidation() {
    }

    @Override
    public void validation(final TestDto testDto) throws ResponseException {

        if (testDto == null) return;

        if (testDto.name == null || testDto.name.isEmpty() || testDto.name.length() > 10) {
            throw new ResponseException(Response.INVALID_NAME);
        }

    }
}
