package com.cxense.cxtube.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cxense.cxensesdk.CxenseSdk;
import com.cxense.cxtube.App;
import com.cxense.cxtube.BuildConfig;
import com.cxense.cxtube.R;
import com.cxense.cxtube.model.VideoItem;
import com.cxense.cxtube.repository.Resource;
import com.cxense.cxtube.viewmodel.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public abstract class VideoListActivity extends AppCompatActivity {
    static final String VIDEO_REFERRER_URL = "referrer_url";
    private static final int UI_DELAY = 100;
    TrackingLifecycleObserver lifecycleObserver;
    BaseViewModel viewModel;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.videos_list)
    RecyclerView recyclerView;
    @Inject
    CxenseSdk cxenseSdk;

    private VideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_videos_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        viewModel = ViewModelProviders.of(this).get(BaseViewModel.class);
        lifecycleObserver = new TrackingLifecycleObserver(getLifecycle(), viewModel);
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refreshRecommendedVideos());
        fillVideosList();
        viewModel.getTrackerEventId().observe(this, id -> {
            Timber.d("Tracked view event with id %s", id);
            // Get user id from Cxense SDK, we want to show it
            Snackbar.make(swipeRefreshLayout, String.format("User id = %s", cxenseSdk.getUserId()),
                    Snackbar.LENGTH_LONG).show();
        });
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Uri pageUri = intent.getData();
            String url = pageUri != null ? pageUri.toString() : BuildConfig.HOST_URL;
            String referrer = intent.getStringExtra(VIDEO_REFERRER_URL);
            viewModel.setUrlWithReferrer(url, referrer);
        }
    }

    @Override
    protected void onPause() {
        // We need this for transition animation
        recyclerView.setLayoutFrozen(true);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restoring
        swipeRefreshLayout.postDelayed(() -> recyclerView.setLayoutFrozen(false), UI_DELAY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                if (!swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                viewModel.refreshRecommendedVideos();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillVideosList() {
        adapter = new VideoAdapter(R.layout.grid_video_item);
        adapter.setOnItemClickListener((view, item) -> VideoActivity.openForItem(this, view, item,
                viewModel.getCurrentCachedUrl()));

        recyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.columns_count)));
        recyclerView.setAdapter(adapter);

        getVideos().observe(this, info -> {
            if (info == null) {
                swipeRefreshLayout.setRefreshing(false);
                showMessage(getString(R.string.error_loading_videos_failed));
                return;
            }
            if (info.status == Resource.Status.LOADING) {
                swipeRefreshLayout.setRefreshing(true);
                return;
            }

            swipeRefreshLayout.setRefreshing(false);
            if (info.status == Resource.Status.ERROR) {
                Timber.e(info.throwable);
                showMessage(info.throwable.getMessage());
            }
            List<VideoItem> data = info.data != null ? info.data : new ArrayList<>();
            adapter.updateData(data);
        });
    }

    private void showMessage(String message) {
        Snackbar.make(swipeRefreshLayout, message, Snackbar.LENGTH_LONG).show();
    }

    protected abstract LiveData<Resource<List<VideoItem>>> getVideos();
}
