package com.cxense.cxvideo.model.video;

import com.cxense.cxvideo.CxenseVideo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-11-16).
 */

public class VideoItemInfo {
    @JsonProperty("Id")
    public long id;
    @JsonProperty("ContentUrl")
    public String contentUrl;
    @JsonProperty("Title")
    public String title;
    @JsonProperty("Description")
    public String description;
    @JsonProperty("LargeThumbnailUrl")
    public String thumbnail;
    @JsonProperty("PublishingDate")
    public Date publishedAt;
    @JsonProperty("DurationSeconds")
    public double duration;
    @JsonProperty("TranscriptionConfidence")
    public double confidence;
    @JsonProperty("Tags")
    public List<TagInfo> tags;


    @JsonIgnore
    public boolean isTranscriptionCorrect() {
        return confidence >= CxenseVideo.TRANSCRIPTION_MIN_CONFIDENCE;
    }
}
