package com.cxense.cxtube.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-23).
 */

public final class FullVideoItem {
    @Embedded
    public VideoItem videoItem;

    @Relation(parentColumn = "articleId", entityColumn = "videoId")
    public List<VideoTag> tags;
}
