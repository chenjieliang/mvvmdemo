package com.jarvis.mvvm.net.download;


import android.util.Log;

import com.jarvis.mvvm.rx.RxBus;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 *
 * @author chenjieliang
 */
public class ProgressResponseBody extends ResponseBody {
    private ResponseBody responseBody;

    private BufferedSource bufferedSource;

    private String httpUrl;

    public ProgressResponseBody(String url, ResponseBody responseBody) {
        this.httpUrl = url;
        this.responseBody = responseBody;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long bytesReaded = 0;
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                bytesReaded += bytesRead == -1 ? 0 : bytesRead;
                Log.i("jieliang","httpUrl : " + httpUrl);
                Log.i("jieliang","read, total : " + contentLength() + " ; bytesReaded: " + bytesReaded);
               //实时发送当前已读取(上传/下载)的字节
                RxBus.getInstance().singlePost(httpUrl,new FileLoadEvent(contentLength(), bytesReaded));
                return bytesRead;
            }
        };
    }
}
