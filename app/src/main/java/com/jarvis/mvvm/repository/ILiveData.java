package com.jarvis.mvvm.repository;

/**
 * @author chenjieliang
 */
public interface ILiveData<T> {

    public void postValue(T value);

    public void setValue(T value);

    public void postError(Throwable throwable);

    public void setError(Throwable throwable);

}
