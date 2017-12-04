package com.cxense.cxtube.ui;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import com.cxense.cxtube.viewmodel.BaseViewModel;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-10).
 */

final class TrackingLifecycleObserver implements DefaultLifecycleObserver {
    BaseViewModel viewModel;

    public TrackingLifecycleObserver(@NonNull Lifecycle lifecycle, @NonNull BaseViewModel viewModel) {
        this.viewModel = viewModel;
        lifecycle.addObserver(this);
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        viewModel.startTrackTime();
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        viewModel.stopTrackTime();
    }


}
