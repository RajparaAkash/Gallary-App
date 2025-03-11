package com.gallaryapp.privacyvault.photoeditor.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.Objects;

public class WaStatus implements Parcelable {

    private long id;
    private String filePath;
    private String fileName;

    public WaStatus(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WaStatus model = (WaStatus) obj;
        return Objects.equals(id, model.id) &&
                Objects.equals(filePath, model.filePath) &&
                Objects.equals(fileName, model.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filePath, fileName);
    }

    protected WaStatus(Parcel in) {
        id = in.readLong();
        filePath = in.readString();
        fileName = in.readString();
    }

    public static final Creator<WaStatus> CREATOR = new Creator<WaStatus>() {
        @Override
        public WaStatus createFromParcel(Parcel in) {
            return new WaStatus(in);
        }

        @Override
        public WaStatus[] newArray(int size) {
            return new WaStatus[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public File getFile() {
        if (this.filePath != null) {
            File file = new File(this.filePath);
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
        dest.writeLong(id);
        dest.writeString(filePath);
        dest.writeString(fileName);
    }
}
