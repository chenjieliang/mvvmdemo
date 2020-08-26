package com.jarvis.mvvm.repository;

import android.support.annotation.Nullable;

/**
 * @author chenjieliang
 */
public interface DataObserver<T> {
    /**
     * Called when the data is changed.
     * @param t  The new data
     */
    void onChanged(@Nullable T t);

    void onError(Throwable throwable);
}