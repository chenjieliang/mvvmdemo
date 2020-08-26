package com.jarvis.mvvm.repository;

/**
 * @author chenjieliang
 */
public interface ISource<RequestEntity> {

    public UtMediatorLiveData fetchData(RequestEntity request);

}
