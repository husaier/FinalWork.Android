package com.example.finalwork;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    // https://beiyou.bytedance.com/api/invoke/video/invoke/video

    @GET("api/invoke/video/invoke/video")
    Call<VideoMessage[]> getVideoMessages();
}
