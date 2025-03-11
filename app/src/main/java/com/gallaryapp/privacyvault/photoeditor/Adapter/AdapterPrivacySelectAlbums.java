package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Model.Album;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.io.File;
import java.util.ArrayList;

public class AdapterPrivacySelectAlbums extends RecyclerView.Adapter<AdapterPrivacySelectAlbums.ViewHolder> {

    private ArrayList<Album> privacyVaultAlbums;
    private Context context;
    private Click click;

    public AdapterPrivacySelectAlbums(Context context, ArrayList<Album> privacyVaultAlbums, Click click) {
        this.context = context;
        this.privacyVaultAlbums = privacyVaultAlbums;
        this.click = click;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_albums_grid, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Album album = privacyVaultAlbums.get(position);

        holder.album_name.setText(album.getName());
        holder.album_media_count.setText(album.getCount() + " items");

        if (album.getThumbnail() != null) {
            Glide.with(context)
                    .load(album.getThumbnail())
                    .centerCrop()
                    .error(R.drawable.place_holder_img)
                    .into(holder.album_preview);
        } else {
            holder.album_preview.setImageResource(R.drawable.place_holder_img);
        }

        File file = new File(album.getPath());
        String albumPath = file.getParent();

        holder.itemView.setOnClickListener(view -> {
            click.ClickToActivity(albumPath, album.getName());
        });
    }

    public interface Click {
        void ClickToActivity(String path, String albumName);
    }

    @Override
    public int getItemCount() {
        return privacyVaultAlbums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView album_preview;
        TextView album_name;
        TextView album_media_count;

        public ViewHolder(View itemView) {
            super(itemView);
            album_preview = (ImageView) itemView.findViewById(R.id.album_preview);
            album_name = (TextView) itemView.findViewById(R.id.album_name);
            album_media_count = (TextView) itemView.findViewById(R.id.album_media_count);
        }
    }
}
