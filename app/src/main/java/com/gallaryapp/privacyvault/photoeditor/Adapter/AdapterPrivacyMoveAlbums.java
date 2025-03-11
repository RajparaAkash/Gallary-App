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

import java.util.ArrayList;

public class AdapterPrivacyMoveAlbums extends RecyclerView.Adapter<AdapterPrivacyMoveAlbums.ViewHolder> {

    private ArrayList<Album> privacyVaultAlbumsList;
    private Context context;
    private Click click;

    public AdapterPrivacyMoveAlbums(Context context, ArrayList<Album> privacyVaultAlbumsList, Click click) {
        this.context = context;
        this.privacyVaultAlbumsList = privacyVaultAlbumsList;
        this.click = click;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_select_albums, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Album album = privacyVaultAlbumsList.get(position);

        holder.folderNameTxt.setText(album.getName());
        holder.folderImgCountTxt.setText(album.getCount() + " items");

        if (album.getThumbnail() != null) {
            Glide.with(context)
                    .load(album.getThumbnail())
                    .centerCrop()
                    .error(R.drawable.place_holder_img) // Placeholder image while loading
                    .into(holder.folderThumbImg);
        } else {
            holder.folderThumbImg.setImageResource(R.drawable.place_holder_img);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Album albumModel : privacyVaultAlbumsList) {
                    albumModel.setSelected(false);
                }
                album.setSelected(true);
                click.ClickToActivity(album.getPath());
                notifyDataSetChanged();
            }
        });

        if (album.isSelected()) {
            holder.folderSelectedImg.setVisibility(View.VISIBLE);
        } else {
            holder.folderSelectedImg.setVisibility(View.GONE);
        }
    }

    public interface Click {
        void ClickToActivity(String path);
    }

    @Override
    public int getItemCount() {
        return privacyVaultAlbumsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView folderThumbImg;
        public TextView folderNameTxt;
        public TextView folderImgCountTxt;
        public ImageView folderSelectedImg;

        public ViewHolder(View itemView) {
            super(itemView);
            folderThumbImg = (ImageView) itemView.findViewById(R.id.folderThumbImg);
            folderNameTxt = (TextView) itemView.findViewById(R.id.folderNameTxt);
            folderImgCountTxt = (TextView) itemView.findViewById(R.id.folderImgCountTxt);
            folderSelectedImg = (ImageView) itemView.findViewById(R.id.folderSelectedImg);
        }
    }
}
