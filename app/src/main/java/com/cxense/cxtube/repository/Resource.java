package com.cxense.cxtube.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-13).
 */

public final class Resource<T> {
    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable
    public final Throwable throwable;

    public Resource(@NonNull Status status, @Nullable T data, @Nullable Throwable throwable) {
        this.status = status;
        this.data = data;
        this.throwable = throwable;
    }

    @NonNull
    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    @NonNull
    public static <T> Resource<T> error(@Nullable T data, Throwable throwable) {
        return new Resource<>(Status.ERROR, data, throwable);
    }

    @NonNull
    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    public enum Status {
        LOADING,
        SUCCESS,
        ERROR
    }
}
