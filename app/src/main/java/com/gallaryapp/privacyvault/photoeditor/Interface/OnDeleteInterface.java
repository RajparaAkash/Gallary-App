package com.gallaryapp.privacyvault.photoeditor.Interface;

import com.gallaryapp.privacyvault.photoeditor.Model.Media;

public interface OnDeleteInterface {
    void onDeleteComplete();

    void onMediaDeleteSuccess(boolean z, Media media);
}
