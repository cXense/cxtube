package com.cxense.cxtube.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

import com.cxense.cxensesdk.CxenseSdk;
import com.cxense.cxensesdk.PageViewEvent;
import com.cxense.cxensesdk.model.WidgetContext;
import com.cxense.cxtube.App;
import com.cxense.cxtube.BuildConfig;
import com.cxense.cxtube.model.VideoItem;
import com.cxense.cxtube.repository.ContentRepository;
import com.cxense.cxtube.repository.Resource;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-14).
 */

public class BaseViewModel extends ViewModel {
    private static final String FAKE_USER_ID = "DO_NOT_TRACK_USER";
    private static final int TAG_SEGMENT_INDEX = 2;
    private static final int VIDEOS_LIMIT = 100;
    final MutableLiveData<Pair<String, String>> urlWithReferrerLiveData = new MutableLiveData<>();
    private final LiveData<String> currentTagLiveData = Transformations.map(urlWithReferrerLiveData, pair -> {
        Uri uri = Uri.parse(pair.first);
        List<String> segments = uri.getPathSegments();
        if (segments.size() > TAG_SEGMENT_INDEX)
            return segments.get(TAG_SEGMENT_INDEX);
        return null;
    });
    @Inject
    CxenseSdk cxenseSdk;
    @Inject
    ContentRepository contentRepository;
    private final LiveData<Resource<List<VideoItem>>> recommendedVideosData = Transformations.switchMap(
            urlWithReferrerLiveData, pair -> contentRepository.getRecommendedVideos(
                    // Building simple widget context
                    new WidgetContext.Builder(pair.first)
                            .setReferrer(pair.second)
                            .build()
            )
    );
    private final LiveData<Resource<List<VideoItem>>> videoByTagData = Transformations.switchMap(
            currentTagLiveData, tag -> contentRepository.getVideosByKeyword(tag, VIDEOS_LIMIT)
    );
    private EventTimeData event;
    private final LiveData<String> trackerData = Transformations.map(urlWithReferrerLiveData, pair -> {
        checkTracking();
        String eventId = UUID.randomUUID().toString();
        // Building PageView event
        PageViewEvent.Builder builder = new PageViewEvent.Builder(BuildConfig.SITE_ID)
                .setLocation(pair.first)
                .setEventId(eventId);
        if (!TextUtils.isEmpty(pair.second))
            builder.setReferrer(pair.second);
        // Pushing event to sending queue
        cxenseSdk.pushEvents(builder.build());
        trackCurrentEventTime();
        event = new EventTimeData();
        return eventId;
    });

    @Inject
    public BaseViewModel() {
        App.getAppComponent().inject(this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        trackCurrentEventTime();
    }

    public void setUrl(String url) {
        setUrlWithReferrer(url, "");
    }

    public void setUrlWithReferrer(String url, String referrer) {
        urlWithReferrerLiveData.setValue(new Pair<>(url, referrer));
    }

    public LiveData<Pair<String, String>> getUrlWithReferrerLiveData() {
        return urlWithReferrerLiveData;
    }

    public LiveData<Resource<List<VideoItem>>> getRecommendedVideos() {
        return recommendedVideosData;
    }

    public LiveData<Resource<List<VideoItem>>> getVideoByTagData() {
        return videoByTagData;
    }

    public void refreshRecommendedVideos() {
        urlWithReferrerLiveData.setValue(urlWithReferrerLiveData.getValue());
    }

    public LiveData<String> getTrackerEventId() {
        return trackerData;
    }

    public String getCurrentCachedUrl() {
        Pair<String, String> value = urlWithReferrerLiveData.getValue();
        return value != null ? value.first : "";
    }

    public void startTrackTime() {
        if (event != null)
            event.start();
    }

    public void stopTrackTime() {
        if (event != null)
            event.stop();
    }

    private void trackCurrentEventTime() {
        if (event == null)
            return;
        event.stop();
        cxenseSdk.trackActiveTime(trackerData.getValue(), event.elapsed);
        event = null;
    }

    private void checkTracking() {
        if (cxenseSdk.isLimitAdTrackingEnabled()) {
            Timber.e("User has limited tracking, we should not track user");
            // We want to track all users, we use ONE FAKE user id for disallowed users
            cxenseSdk.setUserId(FAKE_USER_ID);
        }
        if (TextUtils.isEmpty(cxenseSdk.getDefaultUserId())) {
            Timber.e("User advertising id can't be found. User has not Google account or " +
                    "limited tracking or something else");
        }
    }

    private static class EventTimeData {
        boolean isStarted = true;
        long switchTime = System.currentTimeMillis();
        long elapsed = 0;

        void start() {
            if (isStarted)
                return;
            isStarted = true;
            switchTime = System.currentTimeMillis();
        }

        void stop() {
            if (!isStarted)
                return;
            isStarted = false;
            long now = System.currentTimeMillis();
            elapsed += now - switchTime;
            switchTime = now;
        }
    }
}
