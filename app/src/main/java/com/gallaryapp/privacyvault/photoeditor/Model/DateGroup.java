package com.gallaryapp.privacyvault.photoeditor.Model;

import java.util.ArrayList;

public class DateGroup {

    private String date;
    private ArrayList<Media> mediaItems;

    public DateGroup(String date, ArrayList<Media> mediaItems) {
        this.date = date;
        this.mediaItems = mediaItems;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<Media> getMediaItems() {
        return mediaItems;
    }

}