package com.jarvis.mvvm.net.download;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * @author chenjieliang
 */
public class FileSubscriber<T> implements Observer<T> {

    private FileCallBack fileCallBack;
    private String subscriberKey;

    public FileSubscriber(FileCallBack fileCallBack,String subscriberKey) {

        this.fileCallBack = fileCallBack;
        this.subscriberKey = subscriberKey;
        if (fileCallBack != null) {
            fileCallBack.subscribeLoadProgress(subscriberKey);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (fileCallBack != null)
            fileCallBack.onError(e);
    }

    @Override
    public void onComplete() {
        if (fileCallBack != null)
            fileCallBack.unsubscribe();
            fileCallBack.onCompleted();
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (fileCallBack != null) {
            fileCallBack.onStart();
        }
    }

    @Override
    public void onNext(T t) {
        if (fileCallBack != null)
            fileCallBack.onSuccess(t);
    }

}
