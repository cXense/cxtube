package com.cxense.cxvideo.model.video;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-26).
 */

public class ApiResponse<T> {
    @JsonProperty("Response")
    public T response;
}
