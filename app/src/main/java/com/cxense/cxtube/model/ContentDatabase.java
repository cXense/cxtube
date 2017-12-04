package com.cxense.cxtube.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-13).
 */

@Database(entities = {VideoItem.class, VideoTag.class}, version = 3)
public abstract class ContentDatabase extends RoomDatabase {
    public abstract VideoDao videoDao();

    public abstract VideoTagDao videoTagDao();
}
