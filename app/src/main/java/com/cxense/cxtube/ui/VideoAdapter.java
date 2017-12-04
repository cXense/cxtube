package com.cxense.cxtube.ui;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxense.cxtube.R;
import com.cxense.cxtube.model.VideoItem;
import com.cxense.cxtube.utils.SimpleDiffUtilCallback;
import com.cxense.cxtube.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-14).
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private final int itemLayout;
    private List<VideoItem> data = new ArrayList<>();
    private OnItemClickListener clickListener;

    public VideoAdapter(@LayoutRes int itemLayout) {
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        VideoItem item = data.get(position);
        holder.titleTextView.setText(item.title);
        holder.durationTextView.setText(DateUtils.formatElapsedTime(item.duration));
        holder.publishedTextView.setText(DateUtils.getRelativeDateTimeString(holder.publishedTextView.getContext(),
                item.publishedAt, DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0));
        Utils.loadImage(holder.thumbnailImageView, item.thumbnail);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(@NonNull List<VideoItem> items) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SimpleDiffUtilCallback(data, items));
        this.data = new ArrayList<>(items);
        diffResult.dispatchUpdatesTo(this);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    interface OnItemClickListener {
        void onItemClick(@NonNull View view, @NonNull VideoItem item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.video_title)
        public TextView titleTextView;
        @BindView(R.id.video_duration)
        public TextView durationTextView;
        @BindView(R.id.video_published)
        public TextView publishedTextView;
        @BindView(R.id.video_thumbnail)
        public ImageView thumbnailImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                if (clickListener != null)
                    clickListener.onItemClick(v, data.get(getAdapterPosition()));
            });
        }
    }
}
