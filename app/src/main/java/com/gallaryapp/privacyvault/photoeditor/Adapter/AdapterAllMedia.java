package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.Model.DateGroup;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.io.File;
import java.util.ArrayList;

public class AdapterAllMedia extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_DATE = 0;
    public static final int VIEW_TYPE_MEDIA = 1;

    Context context;
    private ArrayList<Object> items;
    private ClickInterface clickInterface;

    public AdapterAllMedia(Context context, ClickInterface clickInterface) {
        this.items = new ArrayList<>();
        this.context = context;
        this.clickInterface = clickInterface;
    }

    public void setMediaItems(ArrayList<DateGroup> dateGroups) {
        items.clear();
        for (DateGroup dateGroup : dateGroups) {
            items.add(dateGroup);
            items.addAll(dateGroup.getMediaItems());
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_DATE) {
            View itemView = inflater.inflate(R.layout.item_all_media_date, parent, false);
            return new DateViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.item_all_media, parent, false);
            return new MediaViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (getItemViewType(position) == VIEW_TYPE_DATE) {

            DateViewHolder dateViewHolder = (DateViewHolder) holder;
            DateGroup dateGroup = (DateGroup) items.get(position);
            dateViewHolder.dateText.setText(dateGroup.getDate());

        } else if (getItemViewType(position) == VIEW_TYPE_MEDIA) {

            int mediaPosition = getMediaPosition(position);

            MediaViewHolder mediaViewHolder = (MediaViewHolder) holder;
            Media mediaItem = (Media) items.get(position);

            /*mediaViewHolder.mediaNameTxt.setText(FileUtils.getFileSize(mediaItem.getPath()));*/

            if (FileUtils.isVideo(mediaItem.getPath())) {
                mediaViewHolder.videoIcon.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .asBitmap()
                        .load(Uri.fromFile(new File(mediaItem.getPath())))
                        .into(mediaViewHolder.imageView);
            } else {
                mediaViewHolder.videoIcon.setVisibility(View.GONE);
                Glide.with(context)
                        .load(mediaItem.getPath())
                        .into(mediaViewHolder.imageView);
            }

            mediaViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!UtilApp.isLoading) {
                        clickInterface.ClickToActivity(mediaPosition, mediaViewHolder.imageView);
                    }
                }
            });
        }
    }

    public interface ClickInterface {
        void ClickToActivity(int pos, ImageView imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof DateGroup ? VIEW_TYPE_DATE : VIEW_TYPE_MEDIA;
    }

    private int getMediaPosition(int position) {
        int mediaPosition = 0;
        for (int i = 0; i < position; i++) {
            if (getItemViewType(i) == VIEW_TYPE_MEDIA) {
                mediaPosition++;
            }
        }
        return mediaPosition;
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;

        DateViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateTextView);
        }
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView videoIcon;

        MediaViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            videoIcon = (ImageView) itemView.findViewById(R.id.videoIcon);
        }
    }
}
