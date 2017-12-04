package com.cxense.cxvideo.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-11-16).
 */

public class KeywordsList {
    // Bad API response
    @JsonProperty("KeyWord")
    public List<Keyword> items;
}
