package com.bardiademon.conf;

import com.google.gson.annotations.SerializedName;

public record ServerConfig
        (
                String host ,
                int port ,
                @SerializedName("rest_package") String restPackage ,
                @SerializedName("static") StaticConfig statik
        ) {
}
