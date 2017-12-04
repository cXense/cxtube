package com.cxense.cxtube.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.cxense.ad.AdView;
import com.cxense.cxensesdk.CxenseSdk;
import com.cxense.cxtube.BuildConfig;
import com.cxense.cxtube.R;
import com.cxense.cxtube.model.VideoItem;
import com.cxense.cxtube.model.VideoTag;
import com.cxense.cxtube.repository.Resource;
import com.cxense.cxtube.utils.Utils;
import com.cxense.cxtube.viewmodel.VideoViewModel;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class VideoActivity extends AppCompatActivity {
    private static final String TRANSITION_ELEMENT = "video_preview";
    private static final String VIDEO_REFERRER_URL = "referrer_url";
    private static final String VIDEO_PREVIEW_URL = "preview_url";
    @BindView(R.id.toolbar)
    @Nullable
    Toolbar toolbar;
    @BindView(R.id.video_view)
    FrameLayout videoContainer;
    @BindView(R.id.video_player_view)
    VideoView videoView;
    @BindView(R.id.video_player_preview)
    ImageView videoPreview;
    @BindView(R.id.video_player_progressbar)
    ProgressBar videoLoadingProgressBar;
    @BindView(R.id.adView)
    @Nullable
    AdView adView;
    @BindView(R.id.video_description)
    @Nullable
    TextView descriptionTextView;
    @BindView(R.id.video_flexbox)
    @Nullable
    FlexboxLayout flexboxLayout;
    @BindView(R.id.video_transcription)
    @Nullable
    TextView transcriptionTextView;
    @BindView(R.id.video_transcription_progressbar)
    @Nullable
    ProgressBar transcriptionProgressBar;
    @BindView(R.id.videos_list)
    @Nullable
    RecyclerView recyclerView;
    TrackingLifecycleObserver lifecycleObserver;
    private boolean isPortrait = true;
    private MediaController mediaController;
    private VideoViewModel viewModel;
    private VideoAdapter adapter;


    public static void openForItem(@NonNull AppCompatActivity activity, @NonNull View transitionView,
                                   @NonNull VideoItem item, @NonNull String referrer) {
        if (item.clickUrl != null)
            CxenseSdk.trackClick(item.clickUrl);
        // We don't know url for videos loaded not from content widget
        String url = item.url != null ? item.url : BuildConfig.HOST_URL + "video/NAME/" + item.articleId;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url), activity, VideoActivity.class);
        intent.putExtra(VideoActivity.VIDEO_REFERRER_URL, referrer);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, transitionView, VideoActivity.TRANSITION_ELEMENT);
        intent.putExtra(VideoActivity.VIDEO_PREVIEW_URL, item.thumbnail);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        // We use fullscreen video in landscape
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        ViewCompat.setTransitionName(videoPreview, TRANSITION_ELEMENT);
        viewModel = ViewModelProviders.of(this).get(VideoViewModel.class);
        lifecycleObserver = new TrackingLifecycleObserver(getLifecycle(), viewModel);

        if (isPortrait) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            fillRecommendations();
            // fill video transcription
            viewModel.getVideoTranscriptionData().observe(this, info -> {
                if (info == null) {
                    return;
                }
                transcriptionProgressBar.setVisibility(TextUtils.isEmpty(info.data)
                        && info.status == Resource.Status.LOADING ? View.VISIBLE : View.GONE);
                if (!TextUtils.isEmpty(info.data))
                    transcriptionTextView.setText(info.data);
            });
        }

        prepareVideo();

        viewModel.getTrackerEventId().observe(this, id -> Timber.d("Tracked view event with id %s", id));

        // first open
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String action = intent.getAction();
            Uri pageUri = intent.getData();
            if (Intent.ACTION_VIEW.equals(action) && pageUri != null) {
                String referrer = intent.getStringExtra(VIDEO_REFERRER_URL);
                String previewUrl = intent.getStringExtra(VIDEO_PREVIEW_URL);
                viewModel.setVideoPreviewUrl(previewUrl);
                viewModel.setUrlWithReferrer(pageUri.toString(), referrer);
            }
        }
    }

    @Override
    protected void onPause() {
        // We need this for transition animation
        if (recyclerView != null)
            recyclerView.setLayoutFrozen(true);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restoring
        if (recyclerView != null)
            recyclerView.setLayoutFrozen(false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // immersive mode
        if (!isPortrait && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int position = 0;
        try {
            position = videoView.getCurrentPosition();
        } catch (IllegalStateException e) {
            Timber.e(e);
        }
        viewModel.position = position;
        viewModel.isPlaying = videoView.isPlaying();
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video, menu);
        MenuItem item = menu.findItem(R.id.menu_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_video_text, getTitle(),
                viewModel.getCurrentCachedUrl()));
        shareActionProvider.setShareIntent(shareIntent);
        return true;
    }

    @Override
    public void finishAfterTransition() {
        // We need this for transition
        videoPreview.setVisibility(View.VISIBLE);
        super.finishAfterTransition();
    }


    @Override
    protected void onStop() {
        mediaController.hide();
        try {
            videoView.stopPlayback();
        } catch (IllegalStateException e) {
            Timber.e(e);
        }
        super.onStop();
    }

    private void prepareVideo() {
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener(mp -> {
            mp.setOnVideoSizeChangedListener((mp1, width, height) -> {
                if (isPortrait)
                    adjustAspectRatio(width, height);
                // Re-Set the videoView that acts as the anchor for the MediaController
                mediaController.setAnchorView(videoView);
            });

            mediaController.setAnchorView(videoView);
            mp.seekTo(viewModel.position);
            videoLoadingProgressBar.setVisibility(View.GONE);
            videoPreview.setVisibility(View.INVISIBLE);
            if (viewModel.isPlaying)
                mp.start();
            else mediaController.show();
        });
        viewModel.getVideoPreviewUrlData().observe(this, url -> Utils.loadImage(videoPreview, url));
        viewModel.getVideoData().observe(this, info -> {
            if (info == null) {
                showMessage(getString(R.string.error_loading_video));
                return;
            }
            if (info.status == Resource.Status.ERROR) {
                Timber.e(info.throwable);
                showMessage(info.throwable.getMessage());
            }

            if (info.status == Resource.Status.LOADING)
                videoLoadingProgressBar.setVisibility(View.VISIBLE);

            if (info.data == null) {
                return;
            }
            VideoItem item = info.data.videoItem;
            if (isPortrait) {
                setTitle(item.title);
                descriptionTextView.setText(item.description);
                fillTags(info.data.tags);
            }
            if (info.status == Resource.Status.SUCCESS) {
                if (!TextUtils.isEmpty(item.contentUrl))
                    videoView.setVideoURI(Uri.parse(item.contentUrl));
            }

        });
    }

    private void fillRecommendations() {
        adapter = new VideoAdapter(R.layout.linear_video_item);
        adapter.setOnItemClickListener((view, item) -> VideoActivity.openForItem(this, view, item,
                viewModel.getCurrentCachedUrl()));

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        viewModel.getRecommendedVideos().observe(this, info -> {
            if (info == null) {
                showMessage(getString(R.string.error_loading_videos_failed));
                return;
            }

            if (info.status == Resource.Status.ERROR) {
                Timber.e(info.throwable);
                showMessage(info.throwable.getMessage());
            }
            List<VideoItem> data = info.data != null ? info.data : new ArrayList<>();
            adapter.updateData(data);
        });
    }

    private void fillTags(List<VideoTag> tags) {
        flexboxLayout.removeAllViews();
        createFlexboxTextView(R.layout.tags_start, null, null);
        if (tags == null || tags.isEmpty()) {
            flexboxLayout.setVisibility(View.GONE);
        } else {
            flexboxLayout.setVisibility(View.VISIBLE);
            for (VideoTag tag : tags) {
                createFlexboxTextView(R.layout.tag_item, tag.displayName,
                        v -> TaggedVideosActivity.openForItem(this, tag, viewModel.getCurrentCachedUrl()));
            }
        }
    }

    private void createFlexboxTextView(@LayoutRes int layout, @Nullable String customText,
                                       @Nullable View.OnClickListener clickListener) {
        LayoutInflater inflater = LayoutInflater.from(this);
        TextView item = (TextView) inflater.inflate(layout, flexboxLayout, false);
        if (!TextUtils.isEmpty(customText))
            item.setText(customText);
        if (clickListener != null)
            item.setOnClickListener(clickListener);
        flexboxLayout.addView(item);
    }

    private void showMessage(String message) {
        Snackbar.make(videoView, message, Snackbar.LENGTH_LONG).show();
    }

    private void adjustAspectRatio(int videoWidth, int videoHeight) {
        int viewWidth = videoContainer.getWidth();
        double videoAspectRatio = (double) videoHeight / videoWidth;

        ViewGroup.LayoutParams lp = videoContainer.getLayoutParams();
        // limited by narrow width; restrict height
        lp.height = (int) (viewWidth * videoAspectRatio);
        videoContainer.setLayoutParams(lp);
    }
}
