package com.cxense.cxtube;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.cxense.ad.CxenseAd;
import com.cxense.ad.policy.Environment;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-14).
 */

public class App extends Application {
    private static AppComponent appComponent;
    @Inject
    Timber.Tree logTree;
    @Inject
    Crashlytics crashlytics;

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.inject(this);
        Fabric.with(this, crashlytics);
        Timber.plant(logTree);
        CxenseAd cxenseAd = CxenseAd.getInstance();
        cxenseAd.setEnvironment(Environment.LIVE);
    }
}
