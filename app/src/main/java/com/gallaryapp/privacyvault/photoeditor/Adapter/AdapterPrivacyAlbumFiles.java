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

public class AdapterPrivacyAlbumFiles extends RecyclerView.Adapter<AdapterPrivacyAlbumFiles.ViewHolder> {

    private Context context;
    private List<Media> privacyVaultAlbumData;
    private InterfaceActions actionsListener;
    private LongClickInter longClickInter;
    private NormalClickInter normalClickInter;
    private int selectedCount = 0;
    private boolean isSelecting = false;

    public AdapterPrivacyAlbumFiles(Context context, List<Media> privacyVaultAlbumData, InterfaceActions actionsListener,
                                    LongClickInter longClickInter, NormalClickInter normalClickInter) {
        this.context = context;
        this.privacyVaultAlbumData = privacyVaultAlbumData;
        this.actionsListener = actionsListener;
        this.longClickInter = longClickInter;
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

        Media item = privacyVaultAlbumData.get(position);

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
            onNormalClick(item, holder, view);
        });

        holder.itemView.setOnLongClickListener(view -> {
            longClickInter.onClick(position);
            return onLongClick(item, holder, view);
        });
    }

    @Override
    public int getItemCount() {
        return privacyVaultAlbumData.size();
    }

    private Uri getThumbnailUri(String videoPath) {
        return Uri.fromFile(new File(videoPath));
    }

    public void onNormalClick(Media f, ViewHolder holder, View v) {
        if (selecting()) {
            notifySelected(f.toggleSelected());
            notifyItemChanged(holder.getAdapterPosition());
            return;
        }
        this.actionsListener.onItemSelected(holder.getAdapterPosition(), holder.imageView);
    }

    public boolean onLongClick(Media f, ViewHolder holder, View v) {
        if (!selecting()) {
            notifySelected(f.toggleSelected());
            notifyItemChanged(holder.getAdapterPosition());
            return true;
        }
        selectAllUpTo(f);
        return true;
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

    public void selectAllUpTo(Media m) {
        int targetIndex = this.privacyVaultAlbumData.indexOf(m);
        int indexRightBeforeOrAfter = -1;
        Iterator<Media> it = getSelected().iterator();
        while (it.hasNext()) {
            Media sm = it.next();
            int indexNow = this.privacyVaultAlbumData.indexOf(sm);
            if (indexRightBeforeOrAfter == -1) {
                indexRightBeforeOrAfter = indexNow;
            }
            if (indexNow > targetIndex) {
                break;
            }
            indexRightBeforeOrAfter = indexNow;
        }
        if (indexRightBeforeOrAfter != -1) {
            for (int index = Math.min(targetIndex, indexRightBeforeOrAfter); index <= Math.max(targetIndex, indexRightBeforeOrAfter); index++) {
                if (this.privacyVaultAlbumData.get(index) != null && this.privacyVaultAlbumData.get(index).setSelected(true)) {
                    notifySelected(true);
                    notifyItemChanged(index);
                }
            }
        }
    }

    public void startSelection() {
        this.isSelecting = true;
        this.actionsListener.onSelectMode(true);
    }

    private void stopSelection() {
        this.isSelecting = false;
        this.actionsListener.onSelectMode(false);
    }

    public boolean selecting() {
        return this.isSelecting;
    }

    public ArrayList<Media> getSelected() {
        if (Build.VERSION.SDK_INT >= 24) {
            return new ArrayList<>((Collection) this.privacyVaultAlbumData.stream().filter(SelectHelperMedia.INSTANCE).collect(Collectors.toList()));
        }
        ArrayList<Media> arrayList = new ArrayList<>(this.selectedCount);
        Iterator<Media> it = this.privacyVaultAlbumData.iterator();
        while (it.hasNext()) {
            Media m = it.next();
            if (m.isSelected()) {
                arrayList.add(m);
            }
        }
        return arrayList;
    }

    public int getSelectedCount() {
        return this.selectedCount;
    }

    public void selectAll() {
        for (int i = 0; i < this.privacyVaultAlbumData.size(); i++) {
            if (this.privacyVaultAlbumData.get(i).setSelected(true)) {
                notifyItemChanged(i);
            }
        }
        this.selectedCount = this.privacyVaultAlbumData.size();
        startSelection();
    }

    public boolean clearSelected() {
        boolean changed = true;
        for (int i = 0; i < this.privacyVaultAlbumData.size(); i++) {
            boolean b = this.privacyVaultAlbumData.get(i).setSelected(false);
            if (b) {
                notifyItemChanged(i);
            }
            changed &= b;
        }
        this.selectedCount = 0;
        stopSelection();
        return changed;
    }

    public interface LongClickInter {
        void onClick(int pos);
    }

    public interface NormalClickInter {
        void onClick(int pos);
    }

    public void filterList(ArrayList<Media> filteredList) {
        privacyVaultAlbumData = filteredList;
        notifyDataSetChanged();
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