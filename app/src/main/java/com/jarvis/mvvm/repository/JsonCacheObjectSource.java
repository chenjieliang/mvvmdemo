package com.jarvis.mvvm.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jarvis.mvvm.utils.ObjectCacheManager;
import com.jarvis.mvvm.utils.PreferenceUtil;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author chenjieliang
 */
public abstract class JsonCacheObjectSource<ResponeEntity, RequestEntity> extends DataSource<ResponeEntity, RequestEntity> {


    private ObjectCacheManager objectCacheManager;

    protected JsonCacheObjectSource() {
        objectCacheManager = ObjectCacheManager.getInstance();
    }

    @NonNull
    protected abstract String getCacheKey(RequestEntity entity);

    @NonNull
    protected abstract Observable<ResponeEntity> requestNetwork(@Nullable RequestEntity entity);

    @Override
    protected void saveToCache(@Nullable RequestEntity entity,@NonNull ResponeEntity data) {
        if (data!=null) {
            String cacheKey = getCacheKey(entity);
            objectCacheManager.putCache(cacheKey,data);
            Gson gson = new Gson();
            String json = gson.toJson(data);
            PreferenceUtil.setPreferenceToCache(cacheKey,json);
        }
    }

    @Override
    protected boolean shouldFetchFromNetwork(@Nullable ResponeEntity cacheData) {
        if (cacheData == null) {
            return true;
        }
        if (cacheData instanceof List) {
            List list = (List) cacheData;
            return list.size() == 0;
        }
        if (cacheData instanceof Map) {
            Map map = (Map) cacheData;
            return map.size() == 0;
        }
        return false;
    }

    @NonNull
    @Override
    protected UtLiveData<ResponeEntity> fetchFromCache(@Nullable RequestEntity entity) {
        final UtLiveData<ResponeEntity> liveData = new UtLiveData<>();
        final String cacheKey = getCacheKey(entity);
        ResponeEntity cacheObject = (ResponeEntity) objectCacheManager.getCache(cacheKey);
        if (cacheObject!=null) {
            liveData.setValue(cacheObject);
        } else {
            Observable.create(new ObservableOnSubscribe<ResponeEntity>() {
                @Override
                public void subscribe(ObservableEmitter<ResponeEntity> e) throws Exception {
                    String json = PreferenceUtil.getPreferenceFromCache(cacheKey,"");
                    ResponeEntity entity = cacheToObject(json);
                    e.onNext(entity);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponeEntity>() {
                        @Override
                        public void accept(ResponeEntity entity) throws Exception {
                            liveData.setValue(entity);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            liveData.setError(throwable);
                        }
                    });
        }
        return liveData;
    }

    @NonNull
    @Override
    protected UtLiveData<ResponeEntity> fetchFromNetwork(@Nullable RequestEntity entity) {
        final UtLiveData<ResponeEntity> liveData = new UtLiveData<>();
        requestNetwork(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponeEntity>() {
                    @Override
                    public void accept(ResponeEntity entity) throws Exception {
                        liveData.setValue(entity);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.fillInStackTrace();
                        liveData.setError(throwable);
                    }
                });
        return liveData;
    }

    protected ResponeEntity cacheToObject(String json){
        Gson gson = new Gson();
        ResponeEntity entity = gson.fromJson(json, new TypeToken<ResponeEntity>() {}.getType());
        return entity;
    }
}
