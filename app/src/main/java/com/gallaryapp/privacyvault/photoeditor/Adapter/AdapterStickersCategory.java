package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceItemClickGallery;
import com.gallaryapp.privacyvault.photoeditor.Model.StickerItemParentModelMan;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;


public class AdapterStickersCategory extends RecyclerView.Adapter<AdapterStickersCategory.ViewHolder> {

    Activity context;
    InterfaceItemClickGallery listener;
    ArrayList<StickerItemParentModelMan> optionDataModelArrayList;

    public AdapterStickersCategory(Activity activity, ArrayList<StickerItemParentModelMan> optionDataModelArrayList, InterfaceItemClickGallery listener) {
        this.context = activity;
        this.optionDataModelArrayList = optionDataModelArrayList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sticker_category_list, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.tvSpecies.setText(this.optionDataModelArrayList.get(i).getParentText());
        viewHolder.imgThumbnail.setImageDrawable(this.optionDataModelArrayList.get(i).getParentIcon());
    }

    @Override
    public int getItemCount() {
        return this.optionDataModelArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatImageView imgThumbnail;
        RelativeLayout rel_main;
        public TextView tvSpecies;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.imgThumbnail = (AppCompatImageView) itemView.findViewById(R.id.img_thumbnail);
            this.tvSpecies = (TextView) itemView.findViewById(R.id.tv_species);
            this.rel_main = (RelativeLayout) itemView.findViewById(R.id.rel_main);
            this.tvSpecies.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder viewHolder = ViewHolder.this;
                    AdapterStickersCategory.this.listener.onItemClick(itemView, viewHolder.getLayoutPosition());
                }
            });
        }
    }
}
