package com.bardiademon.data.validation;

import com.bardiademon.data.dto.LoginDto;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.excp.ResponseException;

public final class LoginValidation extends Validation<LoginDto> {
    @Override
    public void validation(final LoginDto loginDto) throws ResponseException {
        initial(loginDto);

        if (loginDto.email() == null || loginDto.email().isEmpty() || !loginDto.email().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$") || loginDto.email().length() > 100) {
            throw new ResponseException(Response.INVALID_EMAIL);
        }

        if (loginDto.password() == null || loginDto.password().isEmpty() || loginDto.password().length() > 100) {
            throw new ResponseException(Response.INVALID_PASSWORD);
        }

    }
}
