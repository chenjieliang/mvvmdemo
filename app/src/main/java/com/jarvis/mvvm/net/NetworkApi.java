package com.jarvis.mvvm.net;

import com.google.gson.Gson;
import com.jarvis.mvvm.net.download.FileCallBack;
import com.jarvis.mvvm.net.download.FileSubscriber;
import com.jarvis.mvvm.net.download.ProgressInterceptor;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NetworkApi {

    private static String TAG = "NetworkApi";

    private static String mBaseUrl = "";

    private static NetworkService mNetworkService;

    private static NetworkService mDownloadService;


    private static final byte[] networkServiceLock = new byte[0];

    private static final int NETWORK_SERVICE_FLAG = 1;
    private static final int NETWORK_DOWNLOAD_SERVICE_FLAG = 2;

    private NetworkApi() {
    }

    private static Retrofit createRetrofit(String baseUrl,int serviceFlag){

        mBaseUrl = baseUrl;
        Retrofit retrofit = null;

        if(serviceFlag==NETWORK_SERVICE_FLAG){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())//请求结果转换为基本类型
                    .addConverterFactory(GsonConverterFactory.create())//请求的结果转为实体类
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  //适配RxJava2.0, RxJava1.x则为RxJavaCallAdapterFactory.create()
                    .build();
        }else if(serviceFlag==NETWORK_DOWNLOAD_SERVICE_FLAG){
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new ProgressInterceptor())
                    .build();
            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())//请求结果转换为基本类型
                    .addConverterFactory(GsonConverterFactory.create())//请求的结果转为实体类
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  //适配RxJava2.0, RxJava1.x则为RxJavaCallAdapterFactory.create()
                    .build();
        }

        return retrofit;
    }

    private static NetworkService networkService(){
        synchronized (networkServiceLock) {
            String nowBaseUrl = getBaseUrl();
            if (mNetworkService == null || !mBaseUrl.equals(nowBaseUrl)) {
                mNetworkService = createRetrofit(nowBaseUrl,NETWORK_SERVICE_FLAG).create(NetworkService.class);
            }
        }
        return mNetworkService;
    }

    private static NetworkService downloadService(){
        synchronized (networkServiceLock) {
            String nowBaseUrl = getBaseUrl();
            if (mDownloadService == null || !mBaseUrl.equals(nowBaseUrl)) {
                mDownloadService = createRetrofit(nowBaseUrl,NETWORK_DOWNLOAD_SERVICE_FLAG).create(NetworkService.class);
            }
        }
        return mDownloadService;
    }


    private static String getBaseUrl() {
        return "http://flash.weather.com.cn/";
    }


    public static Observable<String> getWeather(String areaName) {
        return networkService()
                .getWeather(areaName);
    }

    /**
     *  下载文件
     */
    public static void download(String url, final FileCallBack<ResponseBody> callBack, LifecycleProvider<ActivityEvent> provider){

        downloadService()
                .download(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        callBack.saveFile(responseBody);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<ResponseBody>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new FileSubscriber<ResponseBody>(callBack,url));
    }
}
