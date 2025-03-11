package com.gallaryapp.privacyvault.photoeditor.Model;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.gallaryapp.privacyvault.photoeditor.Interface.CursorHandlers;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;

import java.util.ArrayList;

public class Album implements CursorHandlers, Parcelable {

    public static final Creator<Album> CREATOR = new Creator<Album>() {

        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    private int count;
    private long dateModified;
    private long id;
    private Media lastMedia;
    private ArrayList<Media> mediaArrayList;
    private String name;
    private String path;

    private String thumbnail;
    private ArrayList<String> mediaPaths;
    private boolean isSelected;
    private boolean isPinned;

    public Album() {
        this.id = -1L;
        this.count = -1;
        this.lastMedia = null;
        this.mediaArrayList = null;
    }

    public Album(String path, String name) {
        this.id = -1L;
        this.count = -1;
        this.lastMedia = null;
        this.mediaArrayList = null;
        this.name = name;
        this.path = path;
    }

    public Album(String name, long id) {
        this.id = -1L;
        this.count = -1;
        this.lastMedia = null;
        this.mediaArrayList = null;
        this.name = name;
        this.id = id;
    }

    public Album(String path, String name, long id, int count, long dateModified) {
        this(path, name);
        this.count = count;
        this.id = id;
        this.dateModified = dateModified;
    }

    public Album(String path, String name, int count, long dateModified) {
        this(path, name, -1L, count, dateModified);
    }


    public Album(String name, String path, String thumbnail, int count) {
        this.name = name;
        this.path = path;
        this.thumbnail = thumbnail;
        this.count = count;
    }

    public Album(String name, String path, String thumbnail, int count, ArrayList<String> mediaPaths) {
        this.name = name;
        this.path = path;
        this.thumbnail = thumbnail;
        this.count = count;
        this.mediaPaths = mediaPaths;
        this.isSelected = false;
        this.isPinned = false;
    }

    @SuppressLint({"Range"})
    public Album(Cursor cur) {
        this(cur.getColumnIndex("_data") != -1 ? UtilApp.getBucketPathByImagePath(cur.getString(cur.getColumnIndex("_data"))) : "",
                cur.getColumnIndex("bucket_display_name") != -1 ? cur.getString(cur.getColumnIndex("bucket_display_name")) : "",
                cur.getColumnIndex("parent") != -1 ? cur.getLong(cur.getColumnIndex("parent")) : 0L, 0, 0L);
        setLastMedia(new Media(cur.getString(cur.getColumnIndex("_data"))));
    }

    protected Album(Parcel in2) {
        this.id = -1L;
        this.count = -1;
        this.lastMedia = null;
        this.mediaArrayList = null;
        this.name = in2.readString();
        this.path = in2.readString();
        this.thumbnail = in2.readString();
        this.id = in2.readLong();
        this.dateModified = in2.readLong();
        this.count = in2.readInt();
        this.lastMedia = (Media) in2.readParcelable(Media.class.getClassLoader());
        this.mediaArrayList = in2.readArrayList(Media.class.getClassLoader());
    }

    @Override
    public Album handle(Cursor cur) {
        return new Album(cur);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public ArrayList<String> getMediaPaths() {
        return mediaPaths;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean setSelected(boolean selected) {
        if (this.isSelected == selected) {
            return false;
        }
        this.isSelected = selected;
        return true;
    }

    public boolean toggleSelected() {
        boolean z = !this.isSelected;
        this.isSelected = z;
        return z;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public Media getCover() {
        Media media = this.lastMedia;
        if (media != null) {
            return media;
        }
        return new Media();
    }

    public void setLastMedia(Media lastMedia) {
        this.lastMedia = lastMedia;
    }

    public long getId() {
        return this.id;
    }

    public String toString() {
        return "Album{name='" + this.name + "', path='" + this.path + "', id=" + this.id + ", count=" + this.count + '}';
    }

    public boolean equals(Object obj) {
        if (obj instanceof Album) {
            return this.path.equals(((Album) obj).getPath());
        }
        return super.equals(obj);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.thumbnail);
        dest.writeLong(this.id);
        dest.writeLong(this.dateModified);
        dest.writeInt(this.count);
        dest.writeParcelable(this.lastMedia, flags);
        dest.writeList(this.mediaArrayList);
    }

    public ArrayList<Media> getMediaArrayList() {
        return this.mediaArrayList;
    }
}
