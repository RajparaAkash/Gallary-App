package com.gallaryapp.privacyvault.photoeditor.Interface;

import com.gallaryapp.privacyvault.photoeditor.Model.DateGroup;

import java.util.ArrayList;

public interface MediaLoadListener {
    void onMediaLoaded(ArrayList<DateGroup> dateGroups);
}