package com.cxense.cxvideo.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-11-16).
 */

public class Keyword {
    @JsonProperty("score")
    public double score;
    @JsonProperty("content")
    public String content;
}
