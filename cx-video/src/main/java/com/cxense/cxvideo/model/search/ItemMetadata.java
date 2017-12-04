package com.cxense.cxvideo.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-11-15).
 */

public class ItemMetadata {
    @JsonProperty("id")
    public long id;
    @JsonProperty("media-url")
    public String mediaUrl;
    @JsonProperty("duration")
    public double duration;
    @JsonProperty("confidence")
    public double confidence;
    @JsonProperty("Title")
    public String title;
    @JsonProperty("large-thumbnail-url")
    public String thumbnail;
    @JsonProperty("time-stamp")
    public Date publishedAt;
    @JsonProperty("KeyWords")
    public KeywordsList keywords;
}
