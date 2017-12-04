package com.cxense.cxvideo.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-11-15).
 */

public class SearchResultItem {
    @JsonProperty("pinned")
    public boolean isPinned;
    @JsonProperty("score")
    public double score;
    @JsonProperty("EpisodeMetaData")
    public ItemMetadata itemData;
}
