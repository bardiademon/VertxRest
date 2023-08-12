package com.bardiademon.conf;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record DbConfig
        (
                String host ,
                int port ,
                String username ,
                String password ,
                @SerializedName("driver_class") String driverClass ,
                String url
        )  implements Serializable {
}
