package com.bardiademon.data.validation;

import com.bardiademon.data.dto.SignInDto;
import com.bardiademon.data.enums.Response;
import com.bardiademon.data.excp.ResponseException;

public final class SignInValidation extends Validation<SignInDto> {

    @Override
    public void validation(final SignInDto signInDto) throws ResponseException {
        initial(signInDto);

        if (signInDto.email() == null || signInDto.email().isEmpty() || !signInDto.email().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$") || signInDto.email().length() > 100) {
            logger.trace("Invalid email: {}" , signInDto);
            throw new ResponseException(Response.INVALID_EMAIL);
        }

        if (signInDto.password() == null || signInDto.password().isEmpty() || signInDto.password().length() > 100) {
            logger.trace("Invalid password: {}" , signInDto);
            throw new ResponseException(Response.INVALID_PASSWORD);
        }

        logger.trace("Successfully validation sing in dto: {}" , signInDto);
    }
}
