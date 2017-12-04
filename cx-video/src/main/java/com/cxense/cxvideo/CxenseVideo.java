package com.cxense.cxvideo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cxense.Cxense;
import com.cxense.LoadCallback;
import com.cxense.Preconditions;
import com.cxense.SdkInterceptor;
import com.cxense.UserAgentInterceptor;
import com.cxense.cxvideo.model.search.SearchResultData;
import com.cxense.cxvideo.model.video.VideoItemInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-22).
 */

public final class CxenseVideo extends Cxense {
    public static final double TRANSCRIPTION_MIN_CONFIDENCE = 0.7;
    private static CxenseVideo instance;
    private final VideoApi apiInstance;
    private String apiKey;

    protected CxenseVideo(@NonNull Context context) {
        super(context);
        apiInstance = retrofit.create(VideoApi.class);
    }

    static void init(Context context) {
        instance = new CxenseVideo(context);
    }

    /**
     * Gets singleton SDK instance.
     *
     * @return singleton SDK instance.
     */
    public static CxenseVideo getInstance() {
        throwIfUninitialized(instance);
        return instance;
    }

    @NonNull
    @Override
    protected String getBaseUrl() {
        return BuildConfig.SDK_ENDPOINT;
    }

    @NonNull
    @Override
    protected String getSdkName() {
        return BuildConfig.SDK_NAME;
    }

    @NonNull
    @Override
    protected String getSdkVersion() {
        return BuildConfig.VERSION_NAME;
    }

    @NonNull
    @Override
    public String getUserAgent() {
        return String.format("cx-video/%s %s", BuildConfig.VERSION_NAME, getDefaultUserAgent());
    }

    @Override
    protected ObjectMapper buildMapper() {
        ObjectMapper mapper = super.buildMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return mapper;
    }

    @NonNull
    @Override
    protected Converter.Factory getConverterFactory() {
        return new MixedConverterFactory(JacksonConverterFactory.create(this.mapper), ScalarsConverterFactory.create());
    }

    @Override
    protected OkHttpClient buildHttpClient() {
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(new File(appContext.getCacheDir(), "cxense"), cacheSize);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .cache(cache)
                .addInterceptor(new SdkInterceptor(getSdkName(), getSdkVersion()))
                .addInterceptor(new UserAgentInterceptor(getUserAgent()))
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    HttpUrl url = request.url().newBuilder()
                            .addQueryParameter("apikey", apiKey)
                            .build();
                    Request newRequest = request.newBuilder()
                            .url(url)
                            .build();
                    return chain.proceed(newRequest);
                })
                .addInterceptor(interceptor)
                .build();
    }

    @SuppressWarnings("SameParameterValue")
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void getRecentVideos(int count, String query, LoadCallback<SearchResultData> callback) {
        Preconditions.checkStringForNullOrEmpty(apiKey, "api key");
        apiInstance.getVideos(count, query).enqueue(transform(callback, v -> v.response.data));
    }

    public void getVideosByKeyword(int count, String keyword, String query, LoadCallback<SearchResultData> callback) {
        Preconditions.checkStringForNullOrEmpty(apiKey, "api key");
        apiInstance.getVideosByKeyword(count, query, keyword).enqueue(transform(callback, v -> v.response.data));
    }

    public void getVideoById(long videoId, LoadCallback<VideoItemInfo> callback) {
        Preconditions.checkStringForNullOrEmpty(apiKey, "api key");
        apiInstance.getVideo(videoId).enqueue(transform(callback));
    }

    public void getVideoSubtitles(long videoId, LoadCallback<String> callback) {
        Preconditions.checkStringForNullOrEmpty(apiKey, "api key");
        apiInstance.getSubtitles(videoId).enqueue(transform(callback));
    }
}
