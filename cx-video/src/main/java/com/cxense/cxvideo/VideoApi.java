package com.cxense.cxvideo;

import com.cxense.cxvideo.model.search.SearchResponse;
import com.cxense.cxvideo.model.video.ApiResponse;
import com.cxense.cxvideo.model.video.VideoItemInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-22).
 */

interface VideoApi {
    @GET("search?media=video&processingStatus=COMPLETED&order=desc&orderBy=last_acquired_time&format=json&codec=jackson-jaxb")
    @MixedConverterFactory.Json
    Call<ApiResponse<SearchResponse>> getVideos(@Query("num") int count, @Query("q") String query);

    @GET("search?media=video&processingStatus=COMPLETED&order=desc&orderBy=last_acquired_time&format=json&codec=jackson-jaxb&fieldName=content")
    @MixedConverterFactory.Json
    Call<ApiResponse<SearchResponse>> getVideosByKeyword(@Query("num") int count, @Query("q") String query,
                                                         @Query("fieldValue") String keyword);

    @GET("item/{id}?format=json&codec=jackson-jaxb")
    @MixedConverterFactory.Json
    Call<VideoItemInfo> getVideo(@Path("id") long videoId);

    @GET("item/{id}/transcript?format=txt")
    @MixedConverterFactory.Scalar
    Call<String> getSubtitles(@Path("id") long videoId);
}
