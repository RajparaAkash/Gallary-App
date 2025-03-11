package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceActions;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.SelectHelperMedia;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class AdapterPrivacySelectAlbumsData extends RecyclerView.Adapter<AdapterPrivacySelectAlbumsData.ViewHolder> {

    private Context context;
    private List<Media> vaultAlbumData;
    private InterfaceActions actionsListener;
    private NormalClickInter normalClickInter;
    private int selectedCount = 0;
    private boolean isSelecting = false;

    public AdapterPrivacySelectAlbumsData(Context context, List<Media> vaultAlbumData, InterfaceActions actionsListener,
                                          NormalClickInter normalClickInter) {
        this.context = context;
        this.vaultAlbumData = vaultAlbumData;
        this.actionsListener = actionsListener;
        this.normalClickInter = normalClickInter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_media, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Media item = vaultAlbumData.get(position);

        String mediaFile = item.getPath();

        if (FileUtils.isImage(mediaFile)) {
            holder.videoIcon.setVisibility(View.GONE);
            Glide.with(context)
                    .load(mediaFile)
                    .centerCrop()
                    .error(R.drawable.place_holder_img)
                    .into(holder.imageView);
        } else if (FileUtils.isVideo(mediaFile)) {
            holder.videoIcon.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(getThumbnailUri(mediaFile))
                    .centerCrop()
                    .error(R.drawable.place_holder_img)
                    .into(holder.imageView);
        }

        if (item.isSelected()) {
            holder.selectIcon.setImageResource(R.drawable.checkbox_round_selected);
            holder.selectIcon.setVisibility(View.VISIBLE);
            holder.imageView.setColorFilter(-2013265920, PorterDuff.Mode.SRC_ATOP);
            holder.selectIcon.animate().alpha(1.0f).setDuration(250L);
        } else {
            holder.imageView.clearColorFilter();
            holder.selectIcon.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            normalClickInter.onClick(position);
            notifySelected(item.toggleSelected());
            notifyItemChanged(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return vaultAlbumData.size();
    }

    private Uri getThumbnailUri(String videoPath) {
        return Uri.fromFile(new File(videoPath));
    }

    private void notifySelected(boolean increase) {
        int i = this.selectedCount + (increase ? 1 : -1);
        this.selectedCount = i;
        this.actionsListener.onSelectionCountChanged(i, getItemCount());
        int i2 = this.selectedCount;
        if (i2 != 0 || !this.isSelecting) {
            if (i2 <= 0 || this.isSelecting) {
                return;
            }
            startSelection();
            return;
        }
        stopSelection();
    }

    public void startSelection() {
        this.isSelecting = true;
        this.actionsListener.onSelectMode(true);
    }

    private void stopSelection() {
        this.isSelecting = false;
        this.actionsListener.onSelectMode(false);
    }

    public ArrayList<Media> getSelected() {
        if (Build.VERSION.SDK_INT >= 24) {
            return new ArrayList<>((Collection) this.vaultAlbumData.stream().filter(SelectHelperMedia.INSTANCE).collect(Collectors.toList()));
        }
        ArrayList<Media> arrayList = new ArrayList<>(this.selectedCount);
        Iterator<Media> it = this.vaultAlbumData.iterator();
        while (it.hasNext()) {
            Media m = it.next();
            if (m.isSelected()) {
                arrayList.add(m);
            }
        }
        return arrayList;
    }

    public boolean clearSelected() {
        boolean changed = true;
        for (int i = 0; i < this.vaultAlbumData.size(); i++) {
            boolean b = this.vaultAlbumData.get(i).setSelected(false);
            if (b) {
                notifyItemChanged(i);
            }
            changed &= b;
        }
        this.selectedCount = 0;
        stopSelection();
        return changed;
    }

    public void invalidateSelectedCount() {
        int c = 0;
        Iterator<Media> it = this.vaultAlbumData.iterator();
        while (it.hasNext()) {
            Media m = it.next();
            c += m.isSelected() ? 1 : 0;
        }
        this.selectedCount = c;
        if (c == 0) {
            stopSelection();
        } else {
            this.actionsListener.onSelectionCountChanged(c, getOriginalItemCount());
        }
    }

    public int getOriginalItemCount() {
        return this.vaultAlbumData.size();
    }

    public void removeSelectedMedia(Media mediaRemove) {
        try {
            this.vaultAlbumData.remove(mediaRemove);
            int i = this.vaultAlbumData.indexOf(mediaRemove);
            boolean p3 = true;
            boolean p1 = i == this.vaultAlbumData.size() - 1;
            boolean p2 = this.vaultAlbumData.get(i + (-1)) == null;
            if (this.vaultAlbumData.size() - 1 == i || this.vaultAlbumData.get(i - 1) != null || this.vaultAlbumData.get(i + 1) != null) {
                p3 = false;
            }
            if ((p1 && p2) || p3) {
                this.vaultAlbumData.remove(i - 1);
                notifyItemRemoved(i - 1);
                this.vaultAlbumData.remove(i - 1);
                notifyItemRemoved(i - 1);
                return;
            }
            this.vaultAlbumData.remove(i);
            notifyItemRemoved(i);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public interface NormalClickInter {
        void onClick(int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView videoIcon;
        ImageView selectIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            videoIcon = itemView.findViewById(R.id.videoIcon);
            selectIcon = itemView.findViewById(R.id.selectIcon);
        }
    }
}