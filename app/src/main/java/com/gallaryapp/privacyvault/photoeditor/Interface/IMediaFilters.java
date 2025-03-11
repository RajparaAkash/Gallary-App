package com.gallaryapp.privacyvault.photoeditor.Interface;

import com.gallaryapp.privacyvault.photoeditor.Model.Media;

public interface IMediaFilters {
    boolean accept(Media media);
}
