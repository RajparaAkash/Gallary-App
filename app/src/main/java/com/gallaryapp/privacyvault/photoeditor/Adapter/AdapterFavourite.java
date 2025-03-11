package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.DataHelper.DBFavourite;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FavoriteHelper;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdapterFavourite extends RecyclerView.Adapter<AdapterFavourite.RecyclerViewHolder> {

    Context context;
    ArrayList<Media> favArrayList;
    private ClickFavImg clickFavImg;
    private Click click;

    public AdapterFavourite(Context context, ArrayList<Media> favArrayList, ClickFavImg clickFavImg, Click click) {
        this.context = context;
        this.favArrayList = favArrayList;
        this.clickFavImg = clickFavImg;
        this.click = click;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_files, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Media dat = favArrayList.get(position);

        if (FileUtils.isVideo(dat.getPath())) {
            holder.playVideoImg.setVisibility(View.VISIBLE);
        } else {
            holder.playVideoImg.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(dat.getPath())
                .into(holder.imageView);

        holder.favoriteImg.setOnClickListener(view -> {
            addFavoriteItem(dat);
            favArrayList.remove(favArrayList.get(position));
            notifyDataSetChanged();
            clickFavImg.onClickFavImg(favArrayList.size());
        });

        holder.itemView.setOnClickListener(v -> {
            click.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return favArrayList.size();
    }

    public void addFavoriteItem(Media item) {
        String likeListData = FavoriteHelper.getPreferenceString(context, "likeList", "");
        List<String> likeList = new ArrayList<String>();
        if (!likeListData.isEmpty()) {
            likeList.addAll(Arrays.asList(likeListData.split(",")));
        }
        if (likeList.contains(item.getPath())) {
            likeList.remove(item.getPath());
            DBFavourite dbFavourite = new DBFavourite(context);
            dbFavourite.deleteData(item.getPath());

        } else {
            likeList.add(item.getPath());
        }
        String str = String.join(",", likeList);
        FavoriteHelper.setPreferenceString(context, "likeList", str);
        notifyDataSetChanged();
    }


    public interface ClickFavImg {
        void onClickFavImg(int pos);
    }

    public interface Click {
        void onItemClick(int pos);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView favoriteImg;
        ImageView playVideoImg;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            favoriteImg = itemView.findViewById(R.id.favoriteImg);
            playVideoImg = itemView.findViewById(R.id.playVideoImg);
        }
    }
}
