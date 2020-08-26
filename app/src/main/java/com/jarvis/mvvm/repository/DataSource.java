package com.jarvis.mvvm.repository;

import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

/**
 * @author chenjieliang
 */
public abstract class DataSource<ResponeEntity, RequestEntity> implements ISource<RequestEntity>{

    private final UtMediatorLiveData<ResponeEntity> result = new UtMediatorLiveData<>();

    public final UtMediatorLiveData<ResponeEntity> getLiveData() {
        return result;
    }

    @WorkerThread
    protected abstract void saveToCache(@Nullable RequestEntity entity,@NonNull ResponeEntity data);

    @MainThread
    protected abstract boolean shouldFetchFromNetwork(@Nullable ResponeEntity cacheData);

    // Called to get the cached getDate from Cache
    @NonNull
    @MainThread
    protected abstract UtLiveData<ResponeEntity> fetchFromCache(@Nullable RequestEntity entity);

    @NonNull
    @MainThread
    protected abstract UtLiveData<ResponeEntity> fetchFromNetwork(@Nullable RequestEntity entity);

    @MainThread
    @Override
    public final UtMediatorLiveData fetchData(@Nullable final RequestEntity entity){
        final UtLiveData<ResponeEntity> cacheData = fetchFromCache(entity);
        result.addSource(cacheData, new DataObserver<ResponeEntity>() {
            @Override
            public void onChanged(@Nullable ResponeEntity responeEntity) {
                result.removeSource(cacheData);
                if (shouldFetchFromNetwork(responeEntity)) {
                    requestNetwork(entity);
                } else {
                    result.setValue(responeEntity);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                result.removeSource(cacheData);
                requestNetwork(entity);
            }
        });
        return result;
    }

    @MainThread
    private void requestNetwork(@Nullable final RequestEntity entity){
        final UtLiveData<ResponeEntity> remoteData = fetchFromNetwork(entity);
        result.addSource(remoteData, new DataObserver<ResponeEntity>() {
            @Override
            public void onChanged(@Nullable ResponeEntity responeEntity) {
                result.removeSource(remoteData);
                saveResultAndReInit(entity,responeEntity);
            }

            @Override
            public void onError(Throwable throwable) {
                result.removeSource(remoteData);
                result.setError(throwable);
            }
        });
    }

    @MainThread
    private void saveResultAndReInit(final RequestEntity requestEntity,final ResponeEntity data) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                saveToCache(requestEntity,data);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                result.setValue(data);
            }
        }.execute();
    }

}
