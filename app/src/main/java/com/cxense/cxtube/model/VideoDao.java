package com.cxense.cxtube.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-26).
 */

@Dao
public interface VideoDao {
    @Query("SELECT * FROM videos WHERE contextKey = :contextKey ORDER BY updatedAt DESC LIMIT :limit")
    LiveData<List<VideoItem>> getVideos(int contextKey, int limit);

    @Query("SELECT * FROM videos ORDER BY publishedAt DESC LIMIT :limit")
    LiveData<List<VideoItem>> getRecentVideos(int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveVideos(List<VideoItem> videos);

    @Update
    void updateVideos(VideoItem... videos);

    @Query("SELECT transcription FROM videos WHERE articleId = :id")
    LiveData<String> getVideoTranscription(long id);

    @Query("UPDATE videos SET transcription = :value WHERE articleId = :id")
    void updateVideoTranscription(long id, String value);

    @Transaction
    @Query("SELECT * FROM videos WHERE articleId = :id")
    LiveData<FullVideoItem> getVideoWithTags(long id);

    @Query("SELECT v.* FROM tags t LEFT JOIN videos v ON v.articleId = t.videoId WHERE t.value = :tag " +
            "ORDER BY updatedAt DESC LIMIT :limit")
    LiveData<List<VideoItem>> getVideosByTag(String tag, int limit);
}
