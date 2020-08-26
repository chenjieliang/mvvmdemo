package com.jarvis.mvvm.utils;

import android.support.v4.util.LruCache;

/**
 * @author chenjieliang
 */
public class ObjectCacheManager {

    private static ObjectCacheManager instance;
    private LruCache<String, Object> lruCache;

    private static final int MAX_SIZE = 100;

    public static ObjectCacheManager getInstance(){
        if(instance == null){
            synchronized (ObjectCacheManager.class){
                if(instance == null){
                    instance = new ObjectCacheManager();
                }
            }
        }
        return instance;
    }

    private ObjectCacheManager(){
        lruCache = new LruCache<String, Object>(MAX_SIZE);
    }

    /**
     * 写入索引key对应的缓存
     * @param key 索引
     * @param object 缓存内容
     * @return 写入结果
     */
    public Object putCache(String key,Object object){
        Object objectValue=getCache(key);
        if(objectValue==null){
            if(lruCache!=null && object!=null)
                objectValue= lruCache.put(key, object);
        }
        return objectValue;
    }

    /**
     * 获取缓存
     * @param key 索引key对应的缓存
     * @return  缓存
     */
    public Object getCache(String key){
        if(lruCache!=null){
            return lruCache.get(key);
        }
        return null;
    }

    public void deleteCache(){
        if(lruCache!=null)
            lruCache.evictAll();
    }

    public void removeCache(String key){
        if(lruCache!=null)
            lruCache.remove(key);
    }

    public int size(){
        int size=0;
        if(lruCache!=null)
            size+=lruCache.size();
        return size;
    }

}
