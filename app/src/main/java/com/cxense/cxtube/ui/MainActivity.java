package com.cxense.cxtube.ui;

import android.arch.lifecycle.LiveData;

import com.cxense.cxtube.model.VideoItem;
import com.cxense.cxtube.repository.Resource;

import java.util.List;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-28).
 */

public final class MainActivity extends VideoListActivity {
    @Override
    protected LiveData<Resource<List<VideoItem>>> getVideos() {
        return viewModel.getRecommendedVideos();
    }
}
