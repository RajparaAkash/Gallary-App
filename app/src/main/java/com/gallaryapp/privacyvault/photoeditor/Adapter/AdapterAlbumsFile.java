package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceActions;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.SelectHelperMedia;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

public class AdapterAlbumsFile extends RecyclerView.Adapter<AdapterAlbumsFile.ViewHolder> {

    private final InterfaceActions actionsListener;
    private int selectedCount = 0;
    private boolean isSelecting = false;
    private final ArrayList<Media> media = new ArrayList<>();

    public AdapterAlbumsFile(Context context, InterfaceActions actionsListener) {
        this.actionsListener = actionsListener;
    }

    @Override
    public long getItemId(int position) {
        return this.media.get(position).getUri().hashCode() ^ 1312;
    }

    public ArrayList<Media> getSelected() {
        if (Build.VERSION.SDK_INT >= 24) {
            return new ArrayList<>((Collection) this.media.stream().filter(SelectHelperMedia.INSTANCE).collect(Collectors.toList()));
        }
        ArrayList<Media> arrayList = new ArrayList<>(this.selectedCount);
        Iterator<Media> it = this.media.iterator();
        while (it.hasNext()) {
            Media m = it.next();
            if (m.isSelected()) {
                arrayList.add(m);
            }
        }
        return arrayList;
    }

    public Media getFirstSelected() {
        if (this.selectedCount > 0) {
            if (Build.VERSION.SDK_INT >= 24) {
                return (Media) this.media.stream().filter(SelectHelperMedia.INSTANCE).findFirst().orElse(null);
            }
            Iterator<Media> it = this.media.iterator();
            while (it.hasNext()) {
                Media m = it.next();
                if (m.isSelected()) {
                    return m;
                }
            }
        }
        return null;
    }

    public ArrayList<Media> getMedia() {
        return this.media;
    }

    public int getSelectedCount() {
        return this.selectedCount;
    }

    public void selectAll() {
        for (int i = 0; i < this.media.size(); i++) {
            if (this.media.get(i).setSelected(true)) {
                notifyItemChanged(i);
            }
        }
        this.selectedCount = this.media.size();
        startSelection();
    }

    public boolean clearSelected() {
        boolean changed = true;
        for (int i = 0; i < this.media.size(); i++) {
            boolean b = this.media.get(i).setSelected(false);
            if (b) {
                notifyItemChanged(i);
            }
            changed &= b;
        }
        this.selectedCount = 0;
        stopSelection();
        return changed;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_media, parent, false));
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

    private void startSelection() {
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

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Media f = this.media.get(position);
        if (f != null && getPathFromURI(f.getPath())) {
            holder.selectIcon.setVisibility(View.GONE);

            RequestOptions options = new RequestOptions().signature(f.getSignature()).format(DecodeFormat.PREFER_RGB_565)
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            Glide.with(holder.imageView.getContext())
                    .load(f.getUri())
                    .apply((BaseRequestOptions<?>) options)
                    .thumbnail(0.5f)
                    .into(holder.imageView);

            if (FileUtils.isVideo(f.getPath())) {
                holder.videoIcon.setVisibility(View.VISIBLE);
                holder.videoIcon.animate().alpha(1.0f).setDuration(250L);
            } else {
                holder.videoIcon.setVisibility(View.GONE);
                holder.videoIcon.animate().alpha(0.0f).setDuration(250L);
            }
            if (f.isSelected()) {
                holder.selectIcon.setImageResource(R.drawable.checkbox_round_selected);
                holder.selectIcon.setVisibility(View.VISIBLE);
                holder.imageView.setColorFilter(-2013265920, PorterDuff.Mode.SRC_ATOP);
                holder.selectIcon.animate().alpha(1.0f).setDuration(250L);

            } else {
                holder.imageView.clearColorFilter();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBindViewHolder0(f, holder);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return onBindViewHolder2(f, holder);
                }
            });
        }
    }


    public void onBindViewHolder0(Media f, ViewHolder holder) {
        if (selecting()) {
            notifySelected(f.toggleSelected());
            notifyItemChanged(holder.getAdapterPosition());
            return;
        }
        this.actionsListener.onItemSelected(holder.getAdapterPosition(), holder.imageView);
    }

    public boolean onBindViewHolder2(Media f, ViewHolder holder) {
        if (!selecting()) {
            notifySelected(f.toggleSelected());
            notifyItemChanged(holder.getAdapterPosition());
            return true;
        }
        selectAllUpTo(f);
        return true;
    }

    public boolean getPathFromURI(String path) {
        File yourFile = new File(path);
        if (yourFile.exists()) {
            return true;
        }
        return false;
    }

    public void removeSelectedMedia(Media media) {
        try {
            int i = this.media.indexOf(media);
            this.media.remove(i);
            notifyItemRemoved(i);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public void invalidateSelectedCount() {
        int c = 0;
        Iterator<Media> it = this.media.iterator();
        while (it.hasNext()) {
            Media m = it.next();
            c += m.isSelected() ? 1 : 0;
        }
        this.selectedCount = c;
        if (c != 0) {
            this.actionsListener.onSelectionCountChanged(c, this.media.size());
        } else {
            stopSelection();
        }
    }

    public void selectAllUpTo(Media m) {
        int targetIndex = this.media.indexOf(m);
        int indexRightBeforeOrAfter = -1;
        Iterator<Media> it = getSelected().iterator();
        while (it.hasNext()) {
            Media sm = it.next();
            int indexNow = this.media.indexOf(sm);
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
                if (this.media.get(index) != null && this.media.get(index).setSelected(true)) {
                    notifySelected(true);
                    notifyItemChanged(index);
                }
            }
        }
    }

    public void setupFor() {
        this.media.clear();
        notifyDataSetChanged();
    }

    public int add(Media album) {
        this.media.add(album);
        notifyItemInserted(this.media.size() - 1);
        return this.media.size() - 1;
    }

    @Override
    public int getItemCount() {
        return this.media.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView videoIcon;
        ImageView selectIcon;

        ViewHolder(View view) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.imageView);
            videoIcon = (ImageView) view.findViewById(R.id.videoIcon);
            selectIcon = (ImageView) view.findViewById(R.id.selectIcon);
        }
    }
}
