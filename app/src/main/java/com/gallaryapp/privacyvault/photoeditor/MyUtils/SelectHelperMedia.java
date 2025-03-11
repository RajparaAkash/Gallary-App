package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import com.gallaryapp.privacyvault.photoeditor.Model.Media;

import java.util.function.Predicate;

public final class SelectHelperMedia implements Predicate {

    public static final SelectHelperMedia INSTANCE = new SelectHelperMedia();

    private SelectHelperMedia() {
    }

    @Override
    public boolean test(Object obj) {
        return ((Media) obj).isSelected();
    }
}
