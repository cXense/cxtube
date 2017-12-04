package com.cxense.cxtube;

import com.cxense.cxtube.ui.VideoListActivity;
import com.cxense.cxtube.viewmodel.BaseViewModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-14).
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(BaseViewModel viewModel);

    void inject(VideoListActivity activity);

    void inject(App app);
}
