package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceOptionItemClick;
import com.gallaryapp.privacyvault.photoeditor.Model.OptionDatasModel;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;


public class AdapterEditOptions extends RecyclerView.Adapter<AdapterEditOptions.ViewHolder> {

    Activity context;
    int currentSelectedOption;
    InterfaceOptionItemClick listener;
    ArrayList<OptionDatasModel> optionDataModelArrayList;

    public AdapterEditOptions(Activity activity, ArrayList<OptionDatasModel> optionDataModelArrayList, InterfaceOptionItemClick listener) {
        this.context = activity;
        this.optionDataModelArrayList = optionDataModelArrayList;
        this.listener = listener;
    }

    public int getCurrentSelectedOption() {
        return this.currentSelectedOption;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_edit_options, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        OptionDatasModel dataModel = this.optionDataModelArrayList.get(position);

        viewHolder.textOption.setText(dataModel.getOptionName());
        if (dataModel.isSelected()) {
            this.currentSelectedOption = position;
            viewHolder.imgThumbnail.setImageResource(dataModel.getOptionIconSelected());
            viewHolder.imgThumbnail.setColorFilter(ContextCompat.getColor(this.context, R.color.colorPrimary));
            viewHolder.textOption.setTextColor(ContextCompat.getColor(this.context, R.color.colorPrimary));
            return;
        }
        viewHolder.imgThumbnail.setImageResource(dataModel.getOptionIcon());
        viewHolder.imgThumbnail.setColorFilter(ContextCompat.getColor(this.context, R.color.icon_black));
        viewHolder.textOption.setTextColor(ContextCompat.getColor(this.context, R.color.text_color));
    }

    @Override
    public int getItemCount() {
        return this.optionDataModelArrayList.size();
    }

    public void notifySelection(int newPosition) {
        int i = 0;
        while (i < this.optionDataModelArrayList.size()) {
            this.optionDataModelArrayList.get(i).setSelected(i == newPosition);
            i++;
        }
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatImageView imgThumbnail;
        public TextView textOption;

        public ViewHolder(View itemView) {
            super(itemView);

            this.imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            this.textOption = itemView.findViewById(R.id.textOption);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder viewHolder = ViewHolder.this;
                    AdapterEditOptions.this.listener.onItemClick(viewHolder.getLayoutPosition());
                }
            });
        }
    }
}
