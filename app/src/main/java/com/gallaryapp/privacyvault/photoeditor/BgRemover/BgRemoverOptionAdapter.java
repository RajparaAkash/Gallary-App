package com.gallaryapp.privacyvault.photoeditor.BgRemover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.List;

public class BgRemoverOptionAdapter extends RecyclerView.Adapter<BgRemoverOptionAdapter.ViewHolder> {

    private final Context ctx;
    private final List<OptionTool> items;
    private final oClick oclick;
    private int po = 0;

    public BgRemoverOptionAdapter(Context context, List<OptionTool> list, oClick oclick) {
        this.ctx = context;
        this.items = list;
        this.oclick = oclick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.ctx).inflate(R.layout.item_bg_remover_option, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int color = ContextCompat.getColor(this.ctx, R.color.colorPrimary);
        int color2 = ContextCompat.getColor(this.ctx, R.color.icon_black);
        if (this.po != i) {
            color = color2;
        }
        viewHolder.name.setTextColor(color);
        viewHolder.image.setColorFilter(color);
        viewHolder.name.setText(this.items.get(i).name);
        viewHolder.image.setImageResource(this.items.get(i).image);
    }

    public interface oClick {
        void onOption(int i);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        private final TextView name;

        ViewHolder(View view) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.ivIcon);
            this.name = (TextView) view.findViewById(R.id.tvName);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    BgRemoverOptionAdapter.this.po = getAdapterPosition();
                    BgRemoverOptionAdapter.this.oclick.onOption(BgRemoverOptionAdapter.this.po);
                    BgRemoverOptionAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }
}
