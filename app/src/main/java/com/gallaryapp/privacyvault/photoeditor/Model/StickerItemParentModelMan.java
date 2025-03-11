package com.gallaryapp.privacyvault.photoeditor.Model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class StickerItemParentModelMan implements Serializable {

    private String mParentText;
    Drawable parentIcon;

    public String getParentText() {
        return this.mParentText;
    }

    public void setParentText(String parentText) {
        this.mParentText = parentText;
    }

    public Drawable getParentIcon() {
        return this.parentIcon;
    }

    public void setParentIcon(Drawable parentIcon) {
        this.parentIcon = parentIcon;
    }
}
