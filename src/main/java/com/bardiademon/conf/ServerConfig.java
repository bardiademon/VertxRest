package com.bardiademon.conf;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public record ServerConfig
        (
                String host ,
                int port ,
                @SerializedName("rest_package") String restPackage ,
                @SerializedName("static") StaticConfig statik
        ) implements Serializable {
}
