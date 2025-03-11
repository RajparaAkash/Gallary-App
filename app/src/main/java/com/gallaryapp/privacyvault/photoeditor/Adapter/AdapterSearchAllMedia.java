package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.annotations.NonNull;

public class AdapterSearchAllMedia extends RecyclerView.Adapter<AdapterSearchAllMedia.ViewHolder> {

    private ArrayList<Media> searchList;
    private Context context;
    private Click click;

    public AdapterSearchAllMedia(Context context, ArrayList<Media> searchList, Click click) {
        this.context = context;
        this.searchList = searchList;
        this.click = click;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Media media = searchList.get(position);
        holder.mediaNameTxt.setText(media.getFileName());

        if (FileUtils.isImage(media.getPath())) {
            holder.videoImg.setVisibility(View.GONE);
            Glide.with(context)
                    .load(media.getPath())
                    .error(R.drawable.place_holder_img)
                    .into(holder.imageView);
        } else if (FileUtils.isVideo(media.getPath())) {
            holder.videoImg.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .asBitmap()
                    .load(Uri.fromFile(new File(media.getPath())))
                    .error(R.drawable.place_holder_img)
                    .into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(position, searchList, holder.imageView);
            }
        });
    }

    public interface Click {
        void onClick(int pos, ArrayList<Media> searchList, ImageView imageView);
    }

    public void filterList(ArrayList<Media> filteredList) {
        searchList = filteredList;
        notifyDataSetChanged();
    }

    public void removeSelectedMedia(Media media) {
        try {
            int i = this.searchList.indexOf(media);
            this.searchList.remove(i);
            notifyItemRemoved(i);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageView videoImg;
        public TextView mediaNameTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            videoImg = itemView.findViewById(R.id.videoImg);
            mediaNameTxt = itemView.findViewById(R.id.mediaNameTxt);
        }
    }
}
