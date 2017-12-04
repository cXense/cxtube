package com.cxense.cxtube.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-13).
 */

@Dao
public interface VideoTagDao {
    @Query("SELECT * FROM tags WHERE videoId = :videoId ORDER BY score DESC")
    LiveData<List<VideoTag>> getVideoTags(long videoId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveVideoTags(List<VideoTag> tags);

    @Query("DELETE FROM tags WHERE videoId = :videoId")
    void deleteTagsByVideoId(long videoId);
}
