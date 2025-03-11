package com.gallaryapp.privacyvault.photoeditor.Model;

public class IconModel {

    private String iconTitle;
    private int imageView;

    public IconModel(int i, String str) {
        this.imageView = i;
        this.iconTitle = str;
    }

    public int getImageView() {
        return this.imageView;
    }

    public void setImageView(int i) {
        this.imageView = i;
    }

    public String getIconTitle() {
        return this.iconTitle;
    }

    public void setIconTitle(String str) {
        this.iconTitle = str;
    }
}
