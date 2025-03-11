package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Model.IconModel;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;

public class AdapterVideoPlayerIcons extends RecyclerView.Adapter<AdapterVideoPlayerIcons.ViewHolder> {

    private final Context context;
    private final ArrayList<IconModel> iconsList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    public AdapterVideoPlayerIcons(ArrayList<IconModel> iconsList, Context context) {
        this.iconsList = iconsList;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_video_player_icons, viewGroup, false), this.mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.optionIconImg.setImageResource(iconsList.get(i).getImageView());
        viewHolder.optionNameTxt.setText(iconsList.get(i).getIconTitle());
    }

    @Override
    public int getItemCount() {
        if (iconsList == null) {
            return 0;
        }
        return iconsList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView optionIconImg;
        private final TextView optionNameTxt;

        public ViewHolder(View view, final OnItemClickListener onItemClickListener) {
            super(view);

            optionIconImg = view.findViewById(R.id.optionIconImg);
            optionNameTxt = view.findViewById(R.id.optionNameTxt);

            view.setOnClickListener(view2 -> {
                int adapterPosition;
                if (onItemClickListener == null || (adapterPosition = ViewHolder.this.getAdapterPosition()) == -1) {
                    return;
                }
                onItemClickListener.onItemClick(adapterPosition);
            });
        }
    }
}
