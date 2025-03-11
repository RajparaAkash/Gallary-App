package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.R;

public class AdapterFonts extends RecyclerView.Adapter<AdapterFonts.ViewHolder> {
    private ItemClickListener mClickListener;
    private final LayoutInflater mInflater;
    private final Typeface[] typeface;


    public interface ItemClickListener {
        void onItemClick(View view, int i);
    }

    public AdapterFonts(Context mContext, Typeface[] typeface) {
        this.mInflater = LayoutInflater.from(mContext);
        this.typeface = typeface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.mInflater.inflate(R.layout.item_fonts, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.myTextView.setTypeface(this.typeface[position]);
    }

    @Override
    public int getItemCount() {
        return this.typeface.length;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView myTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.myTextView = (TextView) itemView.findViewById(R.id.tvFontPreview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (AdapterFonts.this.mClickListener != null) {
                AdapterFonts.this.mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
}
