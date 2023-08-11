package com.bardiademon.conf;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;

@Builder
public record Config
        (
                @SerializedName("database") DbConfig dbConfig ,
                @SerializedName("server") ServerConfig serverConfig ,
                @SerializedName("response_header") JsonObject responseHeader
        ) {
}
