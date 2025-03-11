package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.gallaryapp.privacyvault.photoeditor.Model.Album;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceAllAlbum;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterSearchAlbums extends RecyclerView.Adapter<AdapterSearchAlbums.ViewHolder> {

    private final InterfaceAllAlbum actionsListener;
    private final Activity mActivity;
    private List<Album> allAlbumsList = new ArrayList();

    public AdapterSearchAlbums(Activity mActivity, InterfaceAllAlbum actionsListener) {
        this.mActivity = mActivity;
        this.actionsListener = actionsListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (MyPreference.get_AlbumsAS_IsGrid()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_albums_grid, parent, false);
            return new ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_albums_list, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        Album albumData = allAlbumsList.get(position);

        Media media = albumData.getCover();
        holder.album_name.setText(albumData.getName());

        RequestOptions options = new RequestOptions().signature(media.getSignature()).format(DecodeFormat.PREFER_ARGB_8888)
                .centerCrop()
                .error(R.drawable.place_holder_img)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        Glide.with(holder.album_preview.getContext()).load(media.getPath())
                .apply((BaseRequestOptions<?>) options)
                .error(R.drawable.place_holder_img)
                .into(holder.album_preview);

        holder.album_media_count.setText(albumData.getCount() + " items");

        holder.itemView.setOnClickListener(view -> {
            actionsListener.onItemSelected(position, holder.album_preview);
        });
    }

    public void clear() {
        allAlbumsList.clear();
        notifyDataSetChanged();
    }

    public int add(Album album) {
        allAlbumsList.add(album);
        notifyItemInserted(allAlbumsList.size() - 1);
        return allAlbumsList.size() - 1;
    }

    public void filterList(ArrayList<Album> filteredList) {
        allAlbumsList = filteredList;
        notifyDataSetChanged();
    }

    public Album get(int pos) {
        return allAlbumsList.get(pos);
    }

    @Override
    public int getItemCount() {
        return allAlbumsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView album_media_count;
        TextView album_name;
        ImageView album_preview;

        ViewHolder(View itemView) {
            super(itemView);

            album_preview = (ImageView) itemView.findViewById(R.id.album_preview);
            album_name = (TextView) itemView.findViewById(R.id.album_name);
            album_media_count = (TextView) itemView.findViewById(R.id.album_media_count);

        }
    }
}
