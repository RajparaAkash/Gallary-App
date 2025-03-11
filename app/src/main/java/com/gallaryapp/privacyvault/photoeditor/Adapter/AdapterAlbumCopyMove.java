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
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;

public class AdapterAlbumCopyMove extends RecyclerView.Adapter<AdapterAlbumCopyMove.ViewHolder> {

    private Activity mActivity;
    private ArrayList<Album> allAlbumList = new ArrayList();
    private Click click;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public AdapterAlbumCopyMove(Activity mActivity, Click click) {
        this.mActivity = mActivity;
        this.click = click;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_albums, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Album currentData = allAlbumList.get(position);

        Media media = currentData.getCover();
        holder.folderNameTxt.setText(currentData.getName());

        RequestOptions options = new RequestOptions().signature(media.getSignature()).format(DecodeFormat.PREFER_ARGB_8888)
                .centerCrop().error(R.drawable.place_holder_img).diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        Glide.with(mActivity).load(media.getPath())
                .apply((BaseRequestOptions<?>) options)
                .error(R.drawable.place_holder_img)
                .into(holder.folderThumbImg);

        holder.folderImgCountTxt.setText(currentData.getCount() + " items");

        holder.folderSelectedImg.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(view -> {
            int previousSelectedPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelectedPosition);
            notifyItemChanged(selectedPosition);

            click.ClickToActivity(currentData.getPath());
        });
    }

    public interface Click {
        void ClickToActivity(String selectedPath);
    }

    public int add(Album album) {
        allAlbumList.add(album);
        notifyItemInserted(allAlbumList.size() - 1);
        return allAlbumList.size() - 1;
    }

    @Override
    public int getItemCount() {
        return allAlbumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView folderThumbImg;
        public TextView folderNameTxt;
        public TextView folderImgCountTxt;
        public ImageView folderSelectedImg;

        ViewHolder(View itemView) {
            super(itemView);

            folderThumbImg = (ImageView) itemView.findViewById(R.id.folderThumbImg);
            folderNameTxt = (TextView) itemView.findViewById(R.id.folderNameTxt);
            folderImgCountTxt = (TextView) itemView.findViewById(R.id.folderImgCountTxt);
            folderSelectedImg = (ImageView) itemView.findViewById(R.id.folderSelectedImg);
        }
    }
}
