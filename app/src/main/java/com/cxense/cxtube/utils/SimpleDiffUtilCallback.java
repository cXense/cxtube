package com.cxense.cxtube.utils;

import android.support.v7.util.DiffUtil;

import com.cxense.cxtube.model.VideoItem;

import java.util.List;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-28).
 */

public class SimpleDiffUtilCallback extends DiffUtil.Callback {
    private final List<VideoItem> oldItems;
    private final List<VideoItem> newItems;

    public SimpleDiffUtilCallback(List<VideoItem> oldItems, List<VideoItem> newItems) {
        this.oldItems = oldItems;
        this.newItems = newItems;
    }

    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).articleId == newItems.get(newItemPosition).articleId;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
    }
}
