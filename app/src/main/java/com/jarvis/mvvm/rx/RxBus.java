package com.jarvis.mvvm.rx;

import java.util.HashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;


public class RxBus {

    private HashMap<String, CompositeDisposable> mSubscriptionMap;
    private static volatile RxBus mRxBus;
    //一对多的发布/订阅
    private final Subject<Object> mSubject;
    //一对一的发布/订阅
    private HashMap<String,Subject<Object>> mSubjectMap = new HashMap<>(); ;

    //单列模式
    public static RxBus getInstance(){
        if (mRxBus==null){
            synchronized (RxBus.class){
                if(mRxBus==null){
                    mRxBus = new RxBus();
                }
            }
        }
        return mRxBus;
    }
    public RxBus(){
        mSubject = PublishSubject.create().toSerialized();
    }

    public void post(Object o){
        mSubject.onNext(o);
    }

    public void singlePost(String key, Object o){
        Subject<Object> singleSubject = null ;
        if (mSubjectMap.containsKey(key)) {
            singleSubject = mSubjectMap.get(key);
            singleSubject.onNext(o);
        }
    }
    /**
     * 返回指定类型的带背压的Flowable实例
     *
     * @param <T>
     * @param type
     * @return
     */
    public <T>Flowable<T> getObservable(Class<T> type){
        return mSubject.toFlowable(BackpressureStrategy.BUFFER)
                .ofType(type);
    }

    /**
     * 返回指定类型的带背压的Flowable实例
     *  一对一的订阅
     * @param subscribeKey
     * @param <T>
     * @param type
     * @return
     */
    public <T>Flowable<T> getSingleObservable(String subscribeKey, Class<T> type){
        Subject<Object> singleSubject = null ;
        if (mSubjectMap.containsKey(subscribeKey)) {
            singleSubject = mSubjectMap.get(subscribeKey);
        } else {
            singleSubject = PublishSubject.create().toSerialized();
            mSubjectMap.put(subscribeKey,singleSubject);
        }
        return singleSubject.toFlowable(BackpressureStrategy.BUFFER)
                .ofType(type);
    }

    /**
     * 一个默认的订阅方法
     *
     * @param <T>
     * @param type
     * @param next
     * @param error
     * @return
     */
    public <T> Disposable doSubscribe(Class<T> type, Consumer<T> next, Consumer<Throwable> error){
        return getObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next,error);
    }

    /**
     * 一个一对一的订阅方法
     *
     * @param subscribeKey
     * @param <T>
     * @param type
     * @param next
     * @param error
     * @return
     */
    public <T> Disposable doSingleSubscribe(String subscribeKey, Class<T> type, Consumer<T> next, Consumer<Throwable> error){
        return getSingleObservable(subscribeKey,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next,error);
    }
    /**
     * 是否已有观察者订阅
     *
     * @return
     */
    public boolean hasObservers() {
        return mSubject.hasObservers();
    }

    /**
     * 保存订阅后的disposable
     * @param  subscribeKey
     * @param disposable
     */
    public void addSubscription(String subscribeKey, Disposable disposable) {
        if (mSubscriptionMap == null) {
            mSubscriptionMap = new HashMap<>();
        }
        String key = subscribeKey;//o.getClass().getName();
        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).add(disposable);
        } else {
            //一次性容器,可以持有多个并提供 添加和移除。
            CompositeDisposable disposables = new CompositeDisposable();
            disposables.add(disposable);
            mSubscriptionMap.put(key, disposables);
        }
    }

    /**
     * 取消订阅
     * @param subscribeKey
     */
    public void unSubscribe(String subscribeKey) {

        if (mSubjectMap.containsKey(subscribeKey)) {
            mSubjectMap.remove(subscribeKey);
        }

        if (mSubscriptionMap == null) {
            return;
        }

        String key = subscribeKey;//o.getClass().getName();
        if (!mSubscriptionMap.containsKey(key)){
            return;
        }
        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).dispose();
        }

        mSubscriptionMap.remove(key);
    }

}