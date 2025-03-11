package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.webkit.MimeTypeMap;

public class UtilMimeType {

    public static String getMimeType(String path) {
        int index;
        String mime;
        return (path == null || (index = path.lastIndexOf(46)) == -1 || (mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(path.substring(index + 1).toLowerCase())) == null) ? "unknown/unknown" : mime;
    }
}
