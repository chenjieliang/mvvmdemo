package com.jarvis.mvvm.repository;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author chenjieliang
 */
public class UtLiveData<T> implements ILiveData<T>{

    protected MediatorLiveData<T> mDataLiveData = new MediatorLiveData<>();
    protected MediatorLiveData<Throwable> mThrowableLiveData = new MediatorLiveData<>();

    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull final DataObserver<T> observer) {
        mDataLiveData.observe(owner, new android.arch.lifecycle.Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                observer.onChanged(t);
            }
        });
        mThrowableLiveData.observe(owner, new android.arch.lifecycle.Observer<Throwable>() {
            @Override
            public void onChanged(@Nullable Throwable throwable) {
                observer.onError(throwable);
            }
        });
    }

    @Override
    public void postValue(T value) {
        mDataLiveData.postValue(value);
    }

    @Override
    public void setValue(T value) {
        mDataLiveData.setValue(value);
    }

    @Override
    public void postError(Throwable throwable) {
        mThrowableLiveData.postValue(throwable);
    }

    @Override
    public void setError(Throwable throwable) {
        mThrowableLiveData.setValue(throwable);
    }

    public MutableLiveData<T> getDataLiveData(){
        return mDataLiveData;
    }

    public MutableLiveData<Throwable> getThrowableLiveData(){
        return mThrowableLiveData;
    }

}
