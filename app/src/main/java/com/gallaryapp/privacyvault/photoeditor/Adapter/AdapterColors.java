package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.R;

public class AdapterColors extends RecyclerView.Adapter<AdapterColors.ViewHolder> {

    public ItemClickListener mClickListener;
    Context mContext;
    private final LayoutInflater mInflater;

    public interface ItemClickListener {
        void onItemClick(int i);
    }

    public AdapterColors(Context mContext2) {
        this.mContext = mContext2;
        this.mInflater = LayoutInflater.from(mContext2);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(this.mInflater.inflate(R.layout.item_colors, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        DrawableCompat.setTint(holder.ivColorPreview.getDrawable(), Color.parseColor(UtilApp.myColorList[position]));
    }

    public int getItemCount() {
        return UtilApp.myColorList.length;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivColorPreview;

        public ViewHolder(View itemView) {
            super(itemView);
            this.ivColorPreview = (ImageView) itemView.findViewById(R.id.ivColorPreview);
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            if (AdapterColors.this.mClickListener != null) {
                AdapterColors.this.mClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
