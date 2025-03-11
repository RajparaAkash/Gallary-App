package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceItemClick;
import com.gallaryapp.privacyvault.photoeditor.Model.OptionDatasModel;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;

public class AdapterEditSubOptions extends RecyclerView.Adapter<AdapterEditSubOptions.ViewHolder> {

    Activity context;
    InterfaceItemClick listener;
    ArrayList<OptionDatasModel> optionDataModelArrayList;

    public AdapterEditSubOptions(Activity activity, ArrayList<OptionDatasModel> optionDataModelArrayList, InterfaceItemClick listener) {
        this.context = activity;
        this.optionDataModelArrayList = optionDataModelArrayList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_edit_sub_options, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.tvSpecies.setText(this.optionDataModelArrayList.get(i).getOptionName());
        viewHolder.imgThumbnail.setImageResource(this.optionDataModelArrayList.get(i).getOptionIcon());
        if (this.optionDataModelArrayList.get(i).isSelected()) {
            viewHolder.imgThumbnail.setColorFilter(ContextCompat.getColor(this.context, R.color.colorPrimary));
            viewHolder.tvSpecies.setTextColor(ContextCompat.getColor(this.context, R.color.colorPrimary));
        } else {
            viewHolder.imgThumbnail.setColorFilter(ContextCompat.getColor(this.context, R.color.icon_black));
            viewHolder.tvSpecies.setTextColor(ContextCompat.getColor(this.context, R.color.text_color));
        }
        viewHolder.tvDevider.setVisibility(this.optionDataModelArrayList.get(i).withDevider() ? View.GONE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return this.optionDataModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgThumbnail;
        public TextView tvDevider;
        public TextView tvSpecies;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            this.tvSpecies = (TextView) itemView.findViewById(R.id.tv_species);
            this.tvDevider = (TextView) itemView.findViewById(R.id.tvDevider);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder viewHolder = ViewHolder.this;
                    if (AdapterEditSubOptions.this.optionDataModelArrayList.get(viewHolder.getAdapterPosition()).isOptionSelectable()) {
                        int i = 0;
                        while (i < AdapterEditSubOptions.this.optionDataModelArrayList.size()) {
                            AdapterEditSubOptions.this.optionDataModelArrayList.get(i).setSelected(i == ViewHolder.this.getAdapterPosition());
                            i++;
                        }
                        AdapterEditSubOptions.this.notifyDataSetChanged();
                    }
                    ViewHolder viewHolder2 = ViewHolder.this;
                    AdapterEditSubOptions.this.listener.onItemClick(itemView, viewHolder2.getLayoutPosition());
                }
            });
        }
    }
}
