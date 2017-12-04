package com.cxense.cxvideo.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-11-15).
 */

public class SearchResultsList {
    // Bad API response
    @JsonProperty("CompleteResult")
    public List<SearchResultItem> items;
}
