package com.siim.depchecker.util;

import rx.Observable;
import rx.Subscriber;

public class RxUtil {

    /**
     * <p>Creates observable with handled error catching.</p><br/>
     * If exception occurrs onError will be called if subscriber is not unsubscribed.<br/>
     * Also, before the first call to callback subscriber subscription is checked.
     */
    public static <T> Observable<T> createErrorHandledObservable(final OnErrorHandledSubscribe<T> f) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        f.call(subscriber);
                    }
                } catch (Throwable e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }

    public static interface OnErrorHandledSubscribe<T> {
        public void call(Subscriber<? super T> subscriber) throws Throwable;
    }
}
