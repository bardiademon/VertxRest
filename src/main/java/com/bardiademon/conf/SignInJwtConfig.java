package com.bardiademon.conf;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public record SignInJwtConfig
        (
                String subject ,
                String issuer ,
                @SerializedName("ignore_expiration") boolean ignoreExpiration ,
                @SerializedName("expires_in_minutes") int expiresInMinutes ,
                String algorithm ,
                @SerializedName("private_key") String privateKey ,
                @SerializedName("header_name") String headerName
        ) implements Serializable {
}
