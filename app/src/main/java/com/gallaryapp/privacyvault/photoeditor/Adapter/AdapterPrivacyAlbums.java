package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Model.Album;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceActions;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.SelectHelperAlbum;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Collectors;

public class AdapterPrivacyAlbums extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ALBUM = 0;
    private static final int VIEW_TYPE_CREATE_NEW_ALBUM = 1;

    private ArrayList<Album> privacyVaultAlbums;
    private Context context;
    private Click click;
    private InterfaceActions actionsListener;
    private LongClickInter longClickInter;
    private NormalClickInter normalClickInter;
    private boolean isSelecting = false;
    private int selectedCount = 0;

    public AdapterPrivacyAlbums(Context context, ArrayList<Album> privacyVaultAlbums, Click click,
                                InterfaceActions actionsListener,
                                LongClickInter longClickInter,
                                NormalClickInter normalClickInter) {
        this.context = context;
        this.privacyVaultAlbums = privacyVaultAlbums;
        this.click = click;
        this.actionsListener = actionsListener;
        this.longClickInter = longClickInter;
        this.normalClickInter = normalClickInter;
        sortAlbums();
    }

    private void sortAlbums() {
        Collections.sort(privacyVaultAlbums, (album1, album2) -> {
            if (album1.isPinned() && !album2.isPinned()) {
                return -1; // album1 comes before album2
            } else if (!album1.isPinned() && album2.isPinned()) {
                return 1; // album2 comes before album1
            } else {
                return 0; // no change in order
            }
        });
    }

    public void pinSelectedAlbums() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AlbumPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (Album album : privacyVaultAlbums) {
            if (album.isSelected()) {
                album.setPinned(true);
                editor.putBoolean(album.getName(), true);
            }
        }
        editor.apply();
        notifyDataSetChanged();
    }

    public void unPinSelectedAlbums() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AlbumPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (Album album : privacyVaultAlbums) {
            if (album.isSelected()) {
                album.setPinned(false);
                editor.putBoolean(album.getName(), false);
            }
        }
        editor.apply();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ALBUM) {
            View view = inflater.inflate(R.layout.item_albums_grid, parent, false);
            return new AlbumViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_new_albums, parent, false);
            return new CreateNewAlbumViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof AlbumViewHolder) {
            AlbumViewHolder albumHolder = (AlbumViewHolder) holder;
            Album album = privacyVaultAlbums.get(position);

            albumHolder.albumName.setText(album.getName());
            albumHolder.totalFiles.setText(album.getCount() + " items");

            if (album.getThumbnail() != null) {
                Glide.with(context)
                        .load(album.getThumbnail())
                        .centerCrop()
                        .error(R.drawable.place_holder_img)
                        .into(albumHolder.thumbnailImageView);
            } else {
                albumHolder.thumbnailImageView.setImageResource(R.drawable.place_holder_img);
            }

            if (album.isSelected()) {
                albumHolder.albumSelect.setImageResource(R.drawable.checkbox_round_selected);
                albumHolder.albumSelect.setVisibility(View.VISIBLE);
                albumHolder.thumbnailImageView.setColorFilter(-2013265920, PorterDuff.Mode.SRC_ATOP);
                albumHolder.albumSelect.animate().alpha(1.0f).setDuration(250L);
            } else {
                albumHolder.thumbnailImageView.clearColorFilter();
                albumHolder.albumSelect.setVisibility(View.GONE);
            }

            if (album.isPinned()) {
                albumHolder.pinAlbumImg.setVisibility(View.VISIBLE);
            } else {
                albumHolder.pinAlbumImg.setVisibility(View.GONE);
            }

            albumHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    normalClickInter.onClick(position, album.getPath());
                    onBindViewHolder0(album, albumHolder, view);
                }
            });

            albumHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    longClickInter.onClick(position);
                    return onBindViewHolder2(album, albumHolder, view);
                }
            });

        } else if (holder instanceof CreateNewAlbumViewHolder) {
            CreateNewAlbumViewHolder createNewAlbumHolder = (CreateNewAlbumViewHolder) holder;
            createNewAlbumHolder.itemView.setOnClickListener(v -> {
                click.ClickToActivity();
            });
        }
    }

    public void onBindViewHolder0(Album f, AlbumViewHolder holder, View v) {
        if (selecting()) {
            notifySelected(f.toggleSelected());
            notifyItemChanged(holder.getAdapterPosition());
            return;
        }
        this.actionsListener.onItemSelected(holder.getAdapterPosition(), holder.thumbnailImageView);
    }

    public boolean onBindViewHolder2(Album f, AlbumViewHolder holder, View v) {
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

    public void selectAllUpTo(Album m) {
        int targetIndex = this.privacyVaultAlbums.indexOf(m);
        int indexRightBeforeOrAfter = -1;
        Iterator<Album> it = getSelected().iterator();
        while (it.hasNext()) {
            Album sm = it.next();
            int indexNow = this.privacyVaultAlbums.indexOf(sm);
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
                if (this.privacyVaultAlbums.get(index) != null && this.privacyVaultAlbums.get(index).setSelected(true)) {
                    notifySelected(true);
                    notifyItemChanged(index);
                }
            }
        }
    }

    public ArrayList<Album> getSelected() {
        if (Build.VERSION.SDK_INT >= 24) {
            return new ArrayList<>((Collection) this.privacyVaultAlbums.stream().filter(SelectHelperAlbum.INSTANCE).collect(Collectors.toList()));
        }
        ArrayList<Album> arrayList = new ArrayList<>(this.selectedCount);
        Iterator<Album> it = this.privacyVaultAlbums.iterator();
        while (it.hasNext()) {
            Album m = it.next();
            if (m.isSelected()) {
                arrayList.add(m);
            }
        }
        return arrayList;
    }

    public void selectAll() {
        for (int i = 0; i < this.privacyVaultAlbums.size(); i++) {
            if (this.privacyVaultAlbums.get(i).setSelected(true)) {
                notifyItemChanged(i);
            }
        }
        this.selectedCount = this.privacyVaultAlbums.size();
        startSelection();
    }

    public boolean clearSelected() {
        boolean changed = true;
        for (int i = 0; i < this.privacyVaultAlbums.size(); i++) {
            boolean b = this.privacyVaultAlbums.get(i).setSelected(false);
            if (b) {
                notifyItemChanged(i);
            }
            changed &= b;
        }
        this.selectedCount = 0;
        stopSelection();
        return changed;
    }

    public void startSelection() {
        this.isSelecting = true;
        this.actionsListener.onSelectMode(true);
    }

    private void stopSelection() {
        this.isSelecting = false;
        this.actionsListener.onSelectMode(false);
    }

    public int getSelectedCount() {
        return this.selectedCount;
    }

    public boolean selecting() {
        return this.isSelecting;
    }

    public interface LongClickInter {
        void onClick(int pos);
    }

    public interface NormalClickInter {
        void onClick(int pos, String albumPath);
    }

    public interface Click {
        void ClickToActivity();
    }

    @Override
    public int getItemCount() {
        return privacyVaultAlbums.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position < privacyVaultAlbums.size() ? VIEW_TYPE_ALBUM : VIEW_TYPE_CREATE_NEW_ALBUM;
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        TextView albumName;
        ImageView thumbnailImageView;
        ImageView albumSelect;
        ImageView pinAlbumImg;
        TextView totalFiles;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumName = itemView.findViewById(R.id.album_name);
            thumbnailImageView = itemView.findViewById(R.id.album_preview);
            albumSelect = itemView.findViewById(R.id.album_select);
            pinAlbumImg = itemView.findViewById(R.id.pin_album_img);
            totalFiles = itemView.findViewById(R.id.album_media_count);
        }
    }

    public static class CreateNewAlbumViewHolder extends RecyclerView.ViewHolder {
        public CreateNewAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
