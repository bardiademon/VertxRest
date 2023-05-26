package com.bardiademon.data.Model;

import com.bardiademon.data.enums.Response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ServerResponse<T> {
    private T info;
    private Response response;

    public ServerResponse(final Response response) {
        this(null , response);
    }

    public ServerResponse(final T info , final Response response) {
        this.info = info;
        this.response = response;
    }
}
