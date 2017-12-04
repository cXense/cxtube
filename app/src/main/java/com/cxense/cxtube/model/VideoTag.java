package com.cxense.cxtube.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.cxense.cxvideo.model.search.Keyword;
import com.cxense.cxvideo.model.video.TagInfo;

import java.util.Objects;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-22).
 */

@Entity(tableName = "tags",
        indices = {@Index("score"), @Index("videoId"), @Index("value")},
        foreignKeys = @ForeignKey(entity = VideoItem.class,
                parentColumns = "articleId",
                childColumns = "videoId",
                onDelete = ForeignKey.CASCADE)
)
public final class VideoTag {
    @PrimaryKey
    public long id;
    public long videoId;
    public String displayName;
    public String value;
    public double score;

    public VideoTag() {
    }

    private VideoTag(long id, long videoId, String displayName, String value, double score) {
        this.id = id;
        this.videoId = videoId;
        this.displayName = displayName;
        this.value = value;
        this.score = score;
    }

    public VideoTag(TagInfo tag, long videoId) {
        this(Objects.hash(videoId, tag.value), videoId,
                tag.displayName.substring(0, 1).toUpperCase() + tag.displayName.substring(1),
                tag.value, tag.score);
    }

    public VideoTag(Keyword tag, long videoId) {
        this(Objects.hash(videoId, tag.content), videoId, tag.content.substring(0, 1).toUpperCase(),
                tag.content, tag.score);
    }
}
