package com.gallaryapp.privacyvault.photoeditor.Model;

public class Intro {

    private int imageResId;
    private String textTital;
    private String textDes;

    public Intro(int imageResId, String textTital, String textDes) {
        this.imageResId = imageResId;
        this.textTital = textTital;
        this.textDes = textDes;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTextTital() {
        return textTital;
    }

    public String getTextDes() {
        return textDes;
    }
}
