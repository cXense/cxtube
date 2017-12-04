package com.cxense.cxvideo.model.video;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-22).
 */

public class TagInfo {
    @JsonProperty("DisplayName")
    public String displayName;
    @JsonProperty("Value")
    public String value;
    @JsonProperty("Score")
    public double score;
    @JsonProperty("IsKeyword")
    public boolean isKeyword;
    @JsonProperty("InDocument")
    public boolean isInDocument;
    @JsonProperty("InMetadata")
    public boolean isInMetadata;
    @JsonProperty("IsContentCue")
    public boolean isContentCue;
    @JsonProperty("IsIgnored")
    public boolean isIgnored;

    @JsonIgnore
    public boolean isVisible() {
        return !TextUtils.isEmpty(displayName);
    }
}
