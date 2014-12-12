package com.siim.depchecker.util;

import rx.Observer;

public class SimpleEndlessObserver<T> implements Observer<T> {

    private final OnNextCallback<T> onNextCallback;

    public SimpleEndlessObserver(OnNextCallback<T> onNextCallback) {
        this.onNextCallback = onNextCallback;
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onNext(T data) {
        onNextCallback.call(data);
    }


    public static interface OnNextCallback<T> {
        public void call(T data);
    }
}
