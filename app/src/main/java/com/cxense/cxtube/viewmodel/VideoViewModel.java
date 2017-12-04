package com.cxense.cxtube.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.net.Uri;

import com.cxense.cxtube.model.FullVideoItem;
import com.cxense.cxtube.repository.Resource;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-23).
 */

public class VideoViewModel extends BaseViewModel {
    private static final int VIDEO_ID_SEGMENT_INDEX = 3;
    private final LiveData<Long> currentVideoIdData = Transformations.map(urlWithReferrerLiveData, pair -> {
        Uri uri = Uri.parse(pair.first);
        List<String> segments = uri.getPathSegments();
        try {
            return Long.parseLong(segments.get(VIDEO_ID_SEGMENT_INDEX));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            Timber.e(e);
        }
        return 0L;
    });
    private final LiveData<Resource<FullVideoItem>> videoData = Transformations.switchMap(currentVideoIdData,
            id -> contentRepository.getFullVideoData(id));
    private final LiveData<Resource<String>> videoTranscriptionData = Transformations.switchMap(currentVideoIdData,
            id -> contentRepository.getVideoTranscription(id));
    private final MutableLiveData<String> videoPreviewUrlData = new MutableLiveData<>();

    public int position = 0;
    public boolean isPlaying = true;

    @Inject
    public VideoViewModel() {
        super();
    }

    public void setVideoPreviewUrl(String url) {
        videoPreviewUrlData.setValue(url);
    }

    public LiveData<String> getVideoPreviewUrlData() {
        return videoPreviewUrlData;
    }

    public LiveData<Resource<FullVideoItem>> getVideoData() {
        return videoData;
    }

    public LiveData<Resource<String>> getVideoTranscriptionData() {
        return videoTranscriptionData;
    }
}
