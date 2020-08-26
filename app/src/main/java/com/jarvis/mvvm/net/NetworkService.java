package com.jarvis.mvvm.net;


import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface NetworkService {

    @GET("/wmaps/xml/{area}.xml")
    Observable<String> getWeather(@Path("area") String area);

    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);//直接使用网址下载

}
