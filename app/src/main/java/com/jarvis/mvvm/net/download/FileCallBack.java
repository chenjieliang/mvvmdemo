package com.jarvis.mvvm.net.download;

import android.util.Log;

import com.jarvis.mvvm.rx.RxBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;

/**
 *
 * @author chenjieliang
 *
 */
public abstract class FileCallBack<T> {

    private String destFileDir;
    private String destFileName;
    private String subscribeKey;

    public FileCallBack(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    public abstract void onSuccess(T t);

    public abstract void progress(long progress, long total);

    public abstract void onStart();

    public abstract void onCompleted();

    public abstract void onError(Throwable e);

    public void saveFile(ResponseBody body) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            is = body.byteStream();
            File dir = new File(destFileDir);
            //File dir = JoyoafApplication.getContext().getDir("libs", Context.MODE_PRIVATE);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
           // fos = JoyoafApplication.getContext().openFileOutput("/libs/"+destFileName,Context.MODE_PRIVATE);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //unsubscribe();
            Log.i("jieliang","save");
            try {
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                Log.e("saveFile", e.getMessage());
            }
        }
    }

    /**
     * 订阅加载的进度条
     */
    public void subscribeLoadProgress(String subscribeKey) {

        this.subscribeKey = subscribeKey;

        Disposable disposable = RxBus.getInstance().doSingleSubscribe(subscribeKey,FileLoadEvent.class, new Consumer<FileLoadEvent>() {
            @Override
            public void accept(FileLoadEvent fileLoadEvent) throws Exception {
                Log.i("jieliang","destFileName : " + destFileName + " ; getTotal : " + fileLoadEvent.getTotal() + " ; getBytesLoaded : " + fileLoadEvent.getBytesLoaded());
                progress(fileLoadEvent.getBytesLoaded(),fileLoadEvent.getTotal());
            }

        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                //TODO 对异常的处理
            }

        });
        RxBus.getInstance().addSubscription(subscribeKey, disposable);
    }

    /**
     * 取消订阅，防止内存泄漏
     */
    public void unsubscribe() {
        if (subscribeKey!=null) {
            RxBus.getInstance().unSubscribe(subscribeKey);
        }
    }

}
