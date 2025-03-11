package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.MyViewCustom.SquareImagesView;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.io.File;
import java.util.List;

public class AdapterStickersFile extends RecyclerView.Adapter<AdapterStickersFile.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private final List<String> arrayList;
    private ItemClickListener mClickListener;

    public AdapterStickersFile(Context mContext, List<String> arrayList) {
        this.layoutInflater = LayoutInflater.from(mContext);
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_sticker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int pos) {
        viewHolder.ivSticker.setImageBitmap(BitmapFactory.decodeFile(new File(this.arrayList.get(pos)).getAbsolutePath()));
        viewHolder.itemView.setTag(this.arrayList.get(pos));
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(String path, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public SquareImagesView ivSticker;

        public ViewHolder(View itemView) {
            super(itemView);
            ivSticker = itemView.findViewById(R.id.ivSticker);

            itemView.setOnClickListener(view -> {
                if (mClickListener != null) {
                    mClickListener.onItemClick(arrayList.get(getLayoutPosition()), getLayoutPosition());
                }
            });
        }
    }
}
