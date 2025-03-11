package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.net.URLConnection;
import java.util.ArrayList;

public class AdapterWaStatusDownalod extends RecyclerView.Adapter<AdapterWaStatusDownalod.ViewHolder> {

    private Context context;
    private ArrayList<Media> statusDownalodList;
    private Click click;

    public AdapterWaStatusDownalod(Context context, ArrayList<Media> statusDownalodList, Click click) {
        this.context = context;
        this.statusDownalodList = statusDownalodList;
        this.click = click;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wa_status_download, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Media statusModel = statusDownalodList.get(position);

        if (isVideoFile(statusModel.getPath())) {
            holder.dStatus_playImg.setVisibility(View.VISIBLE);
        } else {
            holder.dStatus_playImg.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(statusModel.getFile())
                .into(holder.dStatus_mainImg);

        holder.itemView.setOnClickListener(view -> {
            click.onClick(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return statusDownalodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView dStatus_mainImg;
        ImageView dStatus_playImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dStatus_mainImg = itemView.findViewById(R.id.dStatus_mainImg);
            dStatus_playImg = itemView.findViewById(R.id.dStatus_playImg);
        }
    }

    public interface Click {
        void onClick(int pos);
    }

    public ArrayList<Media> getMedia() {
        return this.statusDownalodList;
    }

    public void removeSelectedMedia(Media media) {
        try {
            int i = statusDownalodList.indexOf(media);
            statusDownalodList.remove(i);
            notifyItemRemoved(i);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public boolean isVideoFile(String path) {
        String guessContentTypeFromName = URLConnection.guessContentTypeFromName(path);
        return guessContentTypeFromName != null && guessContentTypeFromName.startsWith("video");
    }
}
