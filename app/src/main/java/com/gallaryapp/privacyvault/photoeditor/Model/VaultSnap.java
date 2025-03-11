package com.gallaryapp.privacyvault.photoeditor.Model;

import java.io.Serializable;

public class VaultSnap implements Serializable {

    private String mImgName = "";
    private String mImgPath = "";
    private String mCapMillis = "";

    public String getmImgName() {
        return mImgName;
    }

    public String getImgPath() {
        return this.mImgPath;
    }

    public void setImgPath(String mImgPath) {
        this.mImgPath = mImgPath;
    }

    public void setImgName(String mImgName) {
        this.mImgName = mImgName;
    }

    public String getCapMillis() {
        return this.mCapMillis;
    }

    public void setCapMillis(String mCapMillis) {
        this.mCapMillis = mCapMillis;
    }
}
