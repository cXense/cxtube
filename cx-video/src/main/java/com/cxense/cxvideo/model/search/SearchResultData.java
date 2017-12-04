package com.cxense.cxvideo.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-11-15).
 */

public class SearchResultData {
    @JsonProperty("has-previous")
    public boolean hasPrevious;
    @JsonProperty("Results")
    public SearchResultsList wrappedList;
    @JsonProperty("results-returned")
    public int returnedSize;
    @JsonProperty("total-results")
    public int totalSize;
    @JsonProperty("has-next")
    public boolean hasNext;
}
