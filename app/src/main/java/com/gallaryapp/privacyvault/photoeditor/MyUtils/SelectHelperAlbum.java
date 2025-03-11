package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import com.gallaryapp.privacyvault.photoeditor.Model.Album;

import java.util.function.Predicate;

public final class SelectHelperAlbum implements Predicate {

    public static final SelectHelperAlbum INSTANCE = new SelectHelperAlbum();

    private SelectHelperAlbum() {
    }

    @Override
    public boolean test(Object obj) {
        return ((Album) obj).isSelected();
    }
}
