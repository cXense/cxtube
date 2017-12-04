package com.cxense.cxtube;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.cxense.cxensesdk.CxenseSdk;
import com.cxense.cxensesdk.Widget;
import com.cxense.cxtube.model.ContentDatabase;
import com.cxense.cxtube.model.Migrations;
import com.cxense.cxtube.model.VideoDao;
import com.cxense.cxtube.model.VideoTagDao;
import com.cxense.cxtube.repository.ContentRepository;
import com.cxense.cxtube.utils.CrashlyticsTree;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-13).
 */

@Module
public final class AppModule {
    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Timber.Tree provideTimberTree() {
        return BuildConfig.DEBUG ? new Timber.DebugTree() : new CrashlyticsTree();
    }

    @Provides
    @Singleton
    CrashlyticsCore provideCrashlyticsCore() {
        // Custom config for Crashlytics. We don't want use it in debug builds
        return new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
    }

    @Provides
    @Singleton
    Crashlytics provideCrashlytics(CrashlyticsCore core) {
        return new Crashlytics.Builder().core(core).build();
    }

    @Provides
    @Singleton
    CxenseSdk provideCxenseSdk() {
        // Get Cxense SDK instance
        return CxenseSdk.getInstance();
    }

    @Provides
    @Singleton
    ContentDatabase provideContentDatabase() {
        return Room.databaseBuilder(context, ContentDatabase.class, "data.db")
                .addMigrations(Migrations.ALL).build();
    }

    @Provides
    @Singleton
    VideoDao provideVideoDao(ContentDatabase contentDatabase) {
        return contentDatabase.videoDao();
    }

    @Provides
    @Singleton
    VideoTagDao provideContentDAO(ContentDatabase contentDatabase) {
        return contentDatabase.videoTagDao();
    }

    @Provides
    @Singleton
    Widget provideWidget() {
        // Create Cxense Content Widget
        return CxenseSdk.createWidget(BuildConfig.WIDGET_ID);
    }

    @Provides
    @Singleton
    ContentRepository provideContentRepository(ContentDatabase contentDatabase, VideoDao videoDao,
                                               VideoTagDao videoTagDao, Widget widget) {
        return new ContentRepository(contentDatabase, videoDao, videoTagDao, widget);
    }
}
