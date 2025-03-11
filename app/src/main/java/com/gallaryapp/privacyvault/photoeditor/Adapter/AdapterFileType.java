package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Activity.ActMainPager;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;

public class AdapterFileType extends RecyclerView.Adapter<AdapterFileType.ViewHolder> {

    private Activity activity;
    private ArrayList<Media> mediaTypesList;

    public AdapterFileType(Activity activity, ArrayList<Media> mediaTypesList) {
        this.activity = activity;
        this.mediaTypesList = mediaTypesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_media, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Media mediaItem = mediaTypesList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(mediaItem.getPath())
                .error(R.drawable.place_holder_img)
                .into(holder.imageView);

        if (FileUtils.isVideo(mediaItem.getPath()))
            holder.videoIcon.setVisibility(View.VISIBLE);
        else
            holder.videoIcon.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InterstitialAds.ShowInterstitial(activity, () -> {
                    Intent intent = new Intent(activity, ActMainPager.class);
                    intent.setAction("SEARCHED");
                    intent.putExtra("args_media", mediaTypesList);
                    intent.putExtra("args_position", holder.getAdapterPosition());
                    activity.startActivityForResult(intent, 140);
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mediaTypesList.size();
    }

    public void removeSelectedMedia(Media media) {
        try {
            int i = mediaTypesList.indexOf(media);
            mediaTypesList.remove(i);
            notifyItemRemoved(i);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView videoIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            videoIcon = itemView.findViewById(R.id.videoIcon);
        }
    }
}
