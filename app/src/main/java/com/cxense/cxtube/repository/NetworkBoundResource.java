/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cxense.cxtube.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.cxense.LoadCallback;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

import timber.log.Timber;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();
    private final ExecutorService executorService;

    @MainThread
    NetworkBoundResource(ExecutorService executorService) {
        this.executorService = executorService;
        result.setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = loadFromDb();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource, data);
            } else {
                result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
            }
        });
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (!Objects.equals(result.getValue(), newValue))
            result.setValue(newValue);
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource, final ResultType dbData) {
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource, newData -> setValue(Resource.loading(newData)));
        // modified for Cxense SDK
        fetchNetworkData(new LoadCallback<RequestType>() {
            @Override
            public void onSuccess(RequestType data) {
                result.removeSource(dbSource);
                executorService.execute(() -> {
                    saveNetworkResult(data, dbData);
                    result.addSource(loadFromDb(), v -> setValue(Resource.success(v)));
                });
            }

            @Override
            public void onError(Throwable throwable) {
                onFetchFailed();
                Timber.e(throwable);
                result.removeSource(dbSource);
                result.addSource(dbSource, v -> setValue(Resource.error(v, throwable)));
            }
        });
    }

    @SuppressWarnings({"WeakerAccess", "EmptyMethod"})
    protected void onFetchFailed() {
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @WorkerThread
    protected abstract void saveNetworkResult(@NonNull RequestType item, @Nullable ResultType dbData);

    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    protected abstract void fetchNetworkData(LoadCallback<RequestType> callback);
}
