package com.gallaryapp.privacyvault.photoeditor.Model;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.bumptech.glide.signature.ObjectKey;
import com.gallaryapp.privacyvault.photoeditor.Interface.CursorHandlers;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilMimeType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Media implements CursorHandlers, Parcelable {

    public static final Creator<Media> CREATOR = new Creator<Media>() {

        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }


        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    private static final int CURSOR_POS_DATA;
    private static final int CURSOR_POS_DATE_ADDED;
    private static final int CURSOR_POS_DATE_MODIFIED;
    private static final int CURSOR_POS_DATE_TAKEN;
    private static final int CURSOR_POS_MIME_TYPE;
    private static final int CURSOR_POS_ORIENTATION;
    private static final int CURSOR_POS_SIZE;
    private static final int CURSOR_POS__ID;
    private static final String[] sProjection;
    private long dateModified;
    private long id;
    private String mimeType;
    private int orientation;
    private String path;
    private boolean selected;
    private long size;
    private String uriString;
    private String fileName;

    static {
        String[] strArr = {"_id", "_data", "datetaken", "mime_type", "_size", "orientation", "date_modified", "date_added"};
        sProjection = strArr;
        CURSOR_POS__ID = getIndex(strArr, "_id");
        CURSOR_POS_DATA = getIndex(strArr, "_data");
        CURSOR_POS_DATE_TAKEN = getIndex(strArr, "datetaken");
        CURSOR_POS_MIME_TYPE = getIndex(strArr, "mime_type");
        CURSOR_POS_SIZE = getIndex(strArr, "_size");
        CURSOR_POS_ORIENTATION = getIndex(strArr, "orientation");
        CURSOR_POS_DATE_MODIFIED = getIndex(strArr, "date_modified");
        CURSOR_POS_DATE_ADDED = getIndex(strArr, "date_added");
    }

    public Media() {
        this.path = null;
        this.id = 0L;
        this.dateModified = -1L;
        this.mimeType = "unknown/unknown";
        this.orientation = 0;
        this.uriString = null;
        this.size = -1L;
        this.selected = false;
    }

    public Media(long id, String filePath, String fileName, long size, String mimeType) {
        this.id = id;
        this.path = filePath;
        this.fileName = fileName;
        this.size = size;
        this.mimeType = mimeType;
        this.selected = false;
    }

    public Media(long id, String path, String fileName) {
        this.id = id;
        this.path = path;
        this.fileName = fileName;
    }

    public Media(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
        this.selected = false;
    }

    public String getFileName() {
        return fileName;
    }

    public Media(String path, long dateModified) {
        this.path = null;
        this.id = 0L;
        this.dateModified = -1L;
        this.mimeType = "unknown/unknown";
        this.orientation = 0;
        this.uriString = null;
        this.size = -1L;
        this.selected = false;
        this.path = path;
        this.dateModified = dateModified;
        initDate(path);
        this.mimeType = UtilMimeType.getMimeType(path);
    }

    private void initDate(String path) {
        if (this.dateModified <= 0) {
            File file = new File(path);
            if (file.exists()) {
                if (Build.VERSION.SDK_INT >= 26) {
                    try {
                        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class, new LinkOption[0]);
                        long createdAt = attr.creationTime().toMillis();
                        this.dateModified = createdAt;
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        this.dateModified = file.lastModified();
                        return;
                    }
                }
                return;
            }
        }
    }

    public Media(String path, long dateModified, long id) {
        this.path = null;
        this.id = 0L;
        this.dateModified = -1L;
        this.mimeType = "unknown/unknown";
        this.orientation = 0;
        this.uriString = null;
        this.size = -1L;
        this.selected = false;
        this.path = path;
        this.dateModified = dateModified;
        initDate(path);
        this.id = id;
        this.mimeType = UtilMimeType.getMimeType(path);
    }

    public Media(File file) {
        this(file.getPath(), file.lastModified());
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class, new LinkOption[0]);
                long createdAt = attr.creationTime().toMillis();
                this.dateModified = createdAt;
            } catch (IOException e) {
                e.printStackTrace();
                this.dateModified = file.lastModified();
            }
        }
        this.size = file.length();
        this.mimeType = UtilMimeType.getMimeType(this.path);
    }

    public Media(String path) {
        this(path, -1L);
    }

    public Media(Uri mediaUri) {
        this.path = null;
        this.id = 0L;
        this.dateModified = -1L;
        this.mimeType = "unknown/unknown";
        this.orientation = 0;
        this.uriString = null;
        this.size = -1L;
        this.selected = false;
        String uri = mediaUri.toString();
        this.uriString = uri;
        this.path = null;
        this.mimeType = UtilMimeType.getMimeType(uri);
    }

    public static String parseDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy HH:mm:ss");
        return formatter.format(new Date(milliSeconds));
    }

    public Media(Cursor cur) {
        this.path = null;
        this.id = 0L;
        this.dateModified = -1L;
        this.mimeType = "unknown/unknown";
        this.orientation = 0;
        this.uriString = null;
        this.size = -1L;
        this.selected = false;
        this.id = cur.getLong(CURSOR_POS__ID);
        this.path = cur.getString(CURSOR_POS_DATA);
        int i = CURSOR_POS_DATE_MODIFIED;
        long dateModify = cur.getLong(i) * 1000;
        parseDate(cur.getLong(i) * 1000);
        int i2 = CURSOR_POS_DATE_TAKEN;
        long dateCreated = cur.getLong(i2);
        parseDate(cur.getLong(i2));
        int i3 = CURSOR_POS_DATE_ADDED;
        long dateAdded = cur.getLong(i3) * 1000;
        parseDate(cur.getLong(i3) * 1000);
        long finalDateMS = dateAdded > dateCreated ? dateAdded : dateCreated;
        parseDate(finalDateMS);
        long finalDateMS2 = finalDateMS > dateModify ? finalDateMS : dateModify;
        parseDate(finalDateMS2);
        this.dateModified = finalDateMS2;
        this.mimeType = cur.getString(CURSOR_POS_MIME_TYPE);
        this.size = cur.getLong(CURSOR_POS_SIZE);
        this.orientation = cur.getInt(CURSOR_POS_ORIENTATION);
        Uri contentUri = Uri.fromFile(new File(this.path));
        if (contentUri == null) {
            contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, this.id);
        }
        this.uriString = contentUri.toString();
    }

    protected Media(Parcel in2) {
        this.path = null;
        this.id = 0L;
        this.dateModified = -1L;
        this.mimeType = "unknown/unknown";
        this.orientation = 0;
        this.uriString = null;
        this.size = -1L;
        this.selected = false;
        this.id = in2.readLong();
        this.path = in2.readString();
        this.fileName = in2.readString();
        this.dateModified = in2.readLong();
        this.mimeType = in2.readString();
        this.orientation = in2.readInt();
        this.uriString = in2.readString();
        this.size = in2.readLong();
        this.selected = in2.readByte() != 0;
        Uri contentUri = Uri.fromFile(new File(this.path));
        this.uriString = (contentUri == null ? ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, this.id) : contentUri).toString();
    }

    public static <T> int getIndex(T[] array, T element) {
        for (int pos = 0; pos < array.length; pos++) {
            if (array[pos].equals(element)) {
                return pos;
            }
        }
        return -1;
    }

    public static String[] getProjection() {
        return sProjection;
    }

    @Override
    public Media handle(Cursor cu) {
        return new Media(cu);
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public boolean setSelected(boolean selected) {
        if (this.selected == selected) {
            return false;
        }
        this.selected = selected;
        return true;
    }

    public boolean toggleSelected() {
        boolean z = !this.selected;
        this.selected = z;
        return z;
    }

    public boolean isGif() {
        if (TextUtils.isEmpty(this.mimeType)) {
            return false;
        }
        return this.mimeType.endsWith("gif");
    }

    public boolean isImage() {
        if (TextUtils.isEmpty(this.mimeType)) {
            return false;
        }
        return this.mimeType.startsWith("image");
    }

    public boolean isVideo() {
        if (TextUtils.isEmpty(this.mimeType)) {
            return false;
        }
        return this.mimeType.startsWith("video");
    }

    public Uri getUri() {
        String str = this.uriString;
        return str != null ? Uri.parse(str) : Uri.fromFile(new File(this.path));
    }

    public void setUri(String uriString) {
        this.uriString = uriString;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayPath() {
        String str = this.path;
        return str != null ? str : getUri().getEncodedPath();
    }

    public long getSize() {
        return this.size;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getDateModified() {
        return Long.valueOf(this.dateModified);
    }

    public ObjectKey getSignature() {
        return new ObjectKey(getDateModified() + getPath() + getOrientation());
    }

    public int getOrientation() {
        return this.orientation;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Media) {
            return getPath().equals(((Media) obj).getPath());
        }
        return super.equals(obj);
    }

    public File getFile() {
        if (this.path != null) {
            File file = new File(this.path);
            if (file.exists()) {
                return file;
            }
            return null;
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.path);
        dest.writeString(this.fileName);
        dest.writeLong(this.dateModified);
        dest.writeString(this.mimeType);
        dest.writeInt(this.orientation);
        dest.writeString(this.uriString);
        dest.writeLong(this.size);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }
}
