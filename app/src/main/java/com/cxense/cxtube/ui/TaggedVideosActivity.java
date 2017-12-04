package com.cxense.cxtube.ui;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.cxense.cxtube.BuildConfig;
import com.cxense.cxtube.model.VideoItem;
import com.cxense.cxtube.model.VideoTag;
import com.cxense.cxtube.repository.Resource;

import java.util.List;

public final class TaggedVideosActivity extends VideoListActivity {
    private static final String TAG_DISPLAY_TITLE = "display_title";

    public static void openForItem(@NonNull AppCompatActivity activity, @NonNull VideoTag tag,
                                   @NonNull String referrer) {
        // We generate url as site for using one mechanism for "opening tag site url" and "opening tag in app"
        String url = BuildConfig.HOST_URL + "tag/" + tag.value;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url), activity, TaggedVideosActivity.class);
        intent.putExtra(VIDEO_REFERRER_URL, referrer);
        intent.putExtra(TAG_DISPLAY_TITLE, tag.displayName);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getStringExtra(TAG_DISPLAY_TITLE);
        setTitle(title);
    }

    @Override
    protected LiveData<Resource<List<VideoItem>>> getVideos() {
        return viewModel.getVideoByTagData();
    }
}
