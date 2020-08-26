package com.jarvis.mvvm.repository;

import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author chenjieliang
 */
public class UtMediatorLiveData<T> extends UtLiveData<T>{


    @MainThread
    public <S> void addSource(@NonNull UtLiveData<S> source, @NonNull final DataObserver<S> observer) {
        mDataLiveData.addSource(source.getDataLiveData(), new Observer<S>() {
            @Override
            public void onChanged(@Nullable S s) {
                observer.onChanged(s);
            }
        });
        mThrowableLiveData.addSource(source.getThrowableLiveData(), new Observer<Throwable>() {
            @Override
            public void onChanged(@Nullable Throwable throwable) {
                observer.onError(throwable);
            }
        });
    }

    @MainThread
    public <S> void removeSource(@NonNull UtLiveData<S> toRemote) {
        mDataLiveData.removeSource(toRemote.getDataLiveData());
        mThrowableLiveData.removeSource(toRemote.getThrowableLiveData());
    }
}
