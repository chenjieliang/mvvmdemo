package com.jarvis.mvvm.net.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author chenjieliang
 */
public class ProgressInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //request.header("")
        String url = request.url().toString();
        Response originalResponse = chain.proceed(request);
        ResponseBody body = originalResponse.body();
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(url,originalResponse.body()))
                .build();
    }
}
