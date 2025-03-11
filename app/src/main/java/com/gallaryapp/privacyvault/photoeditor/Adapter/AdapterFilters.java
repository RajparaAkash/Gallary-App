//package com.gallaryapp.privacyvault.photoeditor.Adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceFilterItemClick;
//import com.gallaryapp.privacyvault.photoeditor.R;
//import com.zomato.photofilters.utils.ThumbnailItem;
//
//import java.util.List;
//
//public class AdapterFilters extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private final InterfaceFilterItemClick filterItemClickListener;
//    private final List<ThumbnailItem> filterModelList;
//    private final int rotateImage;
//    public int selectedPos = 0;
//
//    public AdapterFilters(List<ThumbnailItem> filterModelList, int rotateImage, InterfaceFilterItemClick filterItemClickListener) {
//        this.filterModelList = filterModelList;
//        this.filterItemClickListener = filterItemClickListener;
//        this.rotateImage = rotateImage;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
//        return new CommonHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
//        CommonHolder holder = (CommonHolder) viewHolder;
//        ThumbnailItem thumbnailItem = this.filterModelList.get(position);
//        holder.selectedBorder.setVisibility(position == this.selectedPos ? View.VISIBLE : View.GONE);
//        holder.ivImage.setRotation(this.rotateImage);
//        holder.ivImage.setImageBitmap(thumbnailItem.image);
//        holder.tvFilterName.setText(thumbnailItem.filterName);
//    }
//
//    public void setSelectedFirstPos() {
//        int p = this.selectedPos;
//        this.selectedPos = 0;
//        notifyItemChanged(p);
//        notifyItemChanged(this.selectedPos);
//    }
//
//    @Override
//    public int getItemCount() {
//        return this.filterModelList.size();
//    }
//
//
//    class CommonHolder extends RecyclerView.ViewHolder {
//        ImageView ivImage;
//        RelativeLayout selectedBorder;
//        TextView tvFilterName;
//
//        CommonHolder(View view) {
//            super(view);
//            this.ivImage = (ImageView) view.findViewById(R.id.img_filter);
//            this.tvFilterName = (TextView) view.findViewById(R.id.tv_filter);
//            this.selectedBorder = (RelativeLayout) view.findViewById(R.id.selectedBorder);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    CommonHolder commonHolder = CommonHolder.this;
//                    AdapterFilters adapterFilters = AdapterFilters.this;
//                    int p = adapterFilters.selectedPos;
//                    adapterFilters.selectedPos = commonHolder.getAdapterPosition();
//                    AdapterFilters.this.notifyItemChanged(p);
//                    AdapterFilters adapterFilters2 = AdapterFilters.this;
//                    adapterFilters2.notifyItemChanged(adapterFilters2.selectedPos);
//                    AdapterFilters.this.filterItemClickListener.onFilterClicked(((ThumbnailItem) AdapterFilters.this.filterModelList.get(CommonHolder.this.getAdapterPosition())).filter);
//                }
//            });
//        }
//    }
//}
