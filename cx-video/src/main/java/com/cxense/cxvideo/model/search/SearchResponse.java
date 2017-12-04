package com.cxense.cxvideo.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-11-15).
 */

public class SearchResponse {
    @JsonProperty("expires")
    public Date expiresAt;
    @JsonProperty("ResultSet")
    public SearchResultData data;
    @JsonProperty("timestamp")
    public Date requestedAt;
}
