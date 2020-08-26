package com.jarvis.mvvm.repository;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author chenjieliang
 */
public class UniversalLiveData<T> {

    private MutableLiveData<T> mDataLiveData = new MutableLiveData<>();
    private MutableLiveData<Throwable> mThrowableLiveData = new MutableLiveData<>();

    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<T> observer) {
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

    public void postValue(T value) {
        mDataLiveData.postValue(value);
    }

    public void setValue(T value) {
        mDataLiveData.setValue(value);
    }

    public void postError(Throwable throwable) {
        mThrowableLiveData.postValue(throwable);
    }

    public void setError(Throwable throwable) {
        mThrowableLiveData.setValue(throwable);
    }

    public interface Observer<T> {
        /**
         * Called when the data is changed.
         * @param t  The new data
         */
        void onChanged(@Nullable T t);

        void onError(Throwable throwable);
    }
}
