package com.bardiademon.conf;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public record StaticConfig
        (
                boolean enable ,
                @SerializedName("directory_listing") boolean directoryListing ,
                String path ,
                String router
        ) implements Serializable {
}
