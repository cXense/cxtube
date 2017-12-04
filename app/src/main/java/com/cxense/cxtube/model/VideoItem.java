package com.cxense.cxtube.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.text.TextUtils;

import com.cxense.cxensesdk.model.WidgetItem;
import com.cxense.cxtube.utils.Utils;
import com.cxense.cxvideo.model.search.ItemMetadata;
import com.cxense.cxvideo.model.video.VideoItemInfo;

import java.text.ParseException;
import java.util.Map;

import timber.log.Timber;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-13).
 */

@Entity(tableName = "videos",
        indices = {@Index("contextKey")}
)
public final class VideoItem {
    @PrimaryKey
    public long articleId;
    public String title;
    public String url;
    public String clickUrl;
    public long duration;
    public String thumbnail;
    public long publishedAt;
    public String contentUrl;
    public String description;
    public String transcription;

    public long updatedAt;
    public int contextKey;

    public VideoItem() {
        this.updatedAt = System.currentTimeMillis();
    }

    public VideoItem(WidgetItem item, int contextKey) {
        this();
        this.contextKey = contextKey;

        title = item.title;
        url = item.url;
        clickUrl = item.clickUrl;
        Map<String, Object> properties = item.getProperties();
        articleId = Long.parseLong(properties.get("recs-articleid").toString());
        duration = Math.round(Float.parseFloat(properties.get("cxv-duration").toString()));
        thumbnail = properties.get("dominantthumbnail").toString();
        try {
            publishedAt = Utils.parseDate(properties.get("publishtime").toString()).getTime();
        } catch (ParseException e) {
            Timber.e(e);
        }
    }

    private VideoItem(long articleId, String title, long duration, String thumbnail, long publishedAt,
                      String contentUrl) {
        this.articleId = articleId;
        this.title = title;
        this.duration = duration;
        this.thumbnail = thumbnail;
        this.publishedAt = publishedAt;
        this.contentUrl = contentUrl;
    }

    public VideoItem(VideoItemInfo item) {
        this(item.id, item.title, Math.round(item.duration), item.thumbnail, item.publishedAt.getTime(),
                item.contentUrl);
    }

    public VideoItem(ItemMetadata item) {
        this(item.id, item.title, Math.round(item.duration), item.thumbnail, item.publishedAt.getTime(), item.mediaUrl);
    }

    public boolean isNotFullLoaded() {
        return TextUtils.isEmpty(contentUrl);
    }
}
