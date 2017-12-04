package com.cxense.cxtube.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.cxense.LoadCallback;
import com.cxense.cxensesdk.Widget;
import com.cxense.cxensesdk.model.WidgetContext;
import com.cxense.cxensesdk.model.WidgetItem;
import com.cxense.cxtube.BuildConfig;
import com.cxense.cxtube.model.ContentDatabase;
import com.cxense.cxtube.model.FullVideoItem;
import com.cxense.cxtube.model.VideoDao;
import com.cxense.cxtube.model.VideoItem;
import com.cxense.cxtube.model.VideoTag;
import com.cxense.cxtube.model.VideoTagDao;
import com.cxense.cxvideo.CxenseVideo;
import com.cxense.cxvideo.model.search.Keyword;
import com.cxense.cxvideo.model.search.SearchResultData;
import com.cxense.cxvideo.model.search.SearchResultItem;
import com.cxense.cxvideo.model.video.TagInfo;
import com.cxense.cxvideo.model.video.VideoItemInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-13).
 */

@Singleton
public final class ContentRepository {
    private static final int MAX_CACHED_COUNT = 10;
    private final ContentDatabase db;
    private final VideoDao videoDao;
    private final VideoTagDao videoTagDao;
    private final Widget widget;
    private final CxenseVideo cxenseVideo;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private int recommendedVideosCount;

    @Inject
    public ContentRepository(ContentDatabase contentDatabase, VideoDao videoDao, VideoTagDao videoTagDao,
                             Widget widget) {
        this.db = contentDatabase;
        this.widget = widget;
        this.videoDao = videoDao;
        this.videoTagDao = videoTagDao;
        cxenseVideo = CxenseVideo.getInstance();
        cxenseVideo.setApiKey(BuildConfig.VIDEO_API_KEY);
    }

    public LiveData<Resource<List<VideoItem>>> getRecommendedVideos(final WidgetContext widgetContext) {
        recommendedVideosCount = MAX_CACHED_COUNT;
        final int key = Objects.hash(widgetContext.getUrl(), widgetContext.getReferrer());
        return new NetworkBoundResource<List<VideoItem>, List<WidgetItem>>(executorService) {
            @Override
            protected void saveNetworkResult(@NonNull List<WidgetItem> widgetItems, @Nullable List<VideoItem> dbData) {
                List<VideoItem> videos = new ArrayList<>();
                for (WidgetItem item : widgetItems) {
                    videos.add(new VideoItem(item, key));
                }
                videoDao.saveVideos(videos);
                recommendedVideosCount = widgetItems.size();
            }

            @Override
            protected boolean shouldFetch(@Nullable List<VideoItem> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<VideoItem>> loadFromDb() {
                return videoDao.getVideos(key, recommendedVideosCount);
            }

            @Override
            protected void fetchNetworkData(LoadCallback<List<WidgetItem>> callback) {
                // Get content recommendations from widget
                widget.loadItemsAsync(widgetContext, callback);
            }
        }.asLiveData();
    }

    public LiveData<Resource<FullVideoItem>> getFullVideoData(final long videoId) {
        return new NetworkBoundResource<FullVideoItem, VideoItemInfo>(executorService) {
            @Override
            protected void saveNetworkResult(@NonNull VideoItemInfo item, @Nullable FullVideoItem dbData) {
                VideoItem videoItem;
                List<VideoTag> videoTags;
                if (dbData == null) {
                    videoItem = new VideoItem(item);
                    videoTags = new ArrayList<>();
                } else {
                    videoItem = dbData.videoItem;
                    videoTags = dbData.tags;
                }
                videoItem.contentUrl = item.contentUrl;
                videoItem.description = item.description;
                for (TagInfo tag : item.tags) {
                    if (tag.isVisible())
                        videoTags.add(new VideoTag(tag, videoId));
                }
                db.beginTransaction();
                try {
                    videoTagDao.deleteTagsByVideoId(videoId);
                    videoDao.updateVideos(videoItem);
                    videoTagDao.saveVideoTags(videoTags);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable FullVideoItem data) {
                return data == null || data.videoItem == null || data.videoItem.isNotFullLoaded();
            }

            @NonNull
            @Override
            protected LiveData<FullVideoItem> loadFromDb() {
                return videoDao.getVideoWithTags(videoId);
            }

            @Override
            protected void fetchNetworkData(LoadCallback<VideoItemInfo> callback) {
                // Get video data by id
                cxenseVideo.getVideoById(videoId, callback);
            }
        }.asLiveData();
    }

    public LiveData<Resource<String>> getVideoTranscription(final long videoId) {
        return new NetworkBoundResource<String, String>(executorService) {

            @Override
            protected void saveNetworkResult(@NonNull String item, @Nullable String dbData) {
                videoDao.updateVideoTranscription(videoId, item);
            }

            @Override
            protected boolean shouldFetch(@Nullable String data) {
                return TextUtils.isEmpty(data);
            }

            @NonNull
            @Override
            protected LiveData<String> loadFromDb() {
                return videoDao.getVideoTranscription(videoId);
            }

            @Override
            protected void fetchNetworkData(LoadCallback<String> callback) {
                // Get video subtitles by id
                cxenseVideo.getVideoSubtitles(videoId, callback);
            }
        }.asLiveData();
    }

    private void saveFoundVideos(@NonNull SearchResultData item) {
        if (item.returnedSize == 0)
            return;
        List<VideoItem> videoItems = new ArrayList<>();
        List<VideoTag> videoTags = new ArrayList<>();
        for (SearchResultItem info : item.wrappedList.items) {
            VideoItem videoItem = new VideoItem(info.itemData);
            videoItems.add(videoItem);
            for (Keyword tag : info.itemData.keywords.items) {
                videoTags.add(new VideoTag(tag, videoItem.articleId));
            }
        }
        db.beginTransaction();
        try {
            videoDao.saveVideos(videoItems);
            videoTagDao.saveVideoTags(videoTags);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public LiveData<Resource<List<VideoItem>>> getVideosByKeyword(@Nullable String keyword,
                                                                  @SuppressWarnings("SameParameterValue") int limit) {
        return new NetworkBoundResource<List<VideoItem>, SearchResultData>(executorService) {
            @Override
            protected void saveNetworkResult(@NonNull SearchResultData item, @Nullable List<VideoItem> dbData) {
                saveFoundVideos(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<VideoItem> data) {
                return data == null || data.size() < limit;
            }

            @NonNull
            @Override
            protected LiveData<List<VideoItem>> loadFromDb() {
                return videoDao.getVideosByTag(keyword, limit);
            }

            @Override
            protected void fetchNetworkData(LoadCallback<SearchResultData> callback) {
                // Get videos by tag/keyword
                cxenseVideo.getVideosByKeyword(limit, keyword, null, callback);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<VideoItem>>> getRecentVideos(@SuppressWarnings("SameParameterValue") int limit) {
        return new NetworkBoundResource<List<VideoItem>, SearchResultData>(executorService) {
            @Override
            protected void saveNetworkResult(@NonNull SearchResultData item, @Nullable List<VideoItem> dbData) {
                saveFoundVideos(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<VideoItem> data) {
                return data == null || data.size() < limit;
            }

            @NonNull
            @Override
            protected LiveData<List<VideoItem>> loadFromDb() {
                return videoDao.getRecentVideos(limit);
            }

            @Override
            protected void fetchNetworkData(LoadCallback<SearchResultData> callback) {
                // Get recent videos
                cxenseVideo.getRecentVideos(limit, null, callback);
            }
        }.asLiveData();
    }
}
