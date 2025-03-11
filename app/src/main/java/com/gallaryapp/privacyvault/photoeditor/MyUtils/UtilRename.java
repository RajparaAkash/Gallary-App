package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.net.URLConnection;

public class UtilRename {

    private static final String TAG = "FileActionUtils";

    public static Uri getImageUriFromFile(String str, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor query = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_data = ?", new String[]{str}, "date_modified desc");
        query.moveToFirst();
        if (query.isAfterLast()) {
            query.close();
            ContentValues contentValues = new ContentValues();
            contentValues.put("_data", str);
            return contentResolver.insert(MediaStore.Images.Media.getContentUri("external"), contentValues);
        }
        Uri build = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(Integer.toString(query.getInt(query.getColumnIndexOrThrow("_id")))).build();
        query.close();
        return build;
    }


    public static Uri getVideoUriFromFile(String str, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor query = contentResolver.query(MediaStore.Video.Media.getContentUri("external"), new String[]{"_id"}, "_data = ?", new String[]{str}, "date_added desc");
        query.moveToFirst();
        if (query.isAfterLast()) {
            query.close();
            ContentValues contentValues = new ContentValues();
            contentValues.put("_data", str);
            return contentResolver.insert(MediaStore.Video.Media.getContentUri("external"), contentValues);
        }
        Uri build = MediaStore.Video.Media.getContentUri("external").buildUpon().appendPath(Integer.toString(query.getInt(query.getColumnIndexOrThrow("_id")))).build();
        query.close();
        return build;
    }

    public static boolean isVideo(String str) {
        String guessContentTypeFromName = URLConnection.guessContentTypeFromName(str);
        return guessContentTypeFromName != null && guessContentTypeFromName.startsWith("video");
    }

    public static boolean renameFileAboveQ(Activity activity, String str, String str2) {
        ContentResolver contentResolver = activity.getContentResolver();
        try {
            if (isVideo(new File(str).getName())) {
                Uri videoUriFromFile = getVideoUriFromFile(str, activity);
                ContentValues contentValues = new ContentValues();
                contentValues.put("is_pending", (Integer) 1);
                contentResolver.update(videoUriFromFile, contentValues, null, null);
                contentValues.clear();
                contentValues.put("_display_name", str2);
                contentValues.put("is_pending", (Integer) 0);
                contentResolver.update(videoUriFromFile, contentValues, null, null);
                return true;
            }
            Uri imageUriFromFile = getImageUriFromFile(str, activity);
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("is_pending", (Integer) 1);
            contentResolver.update(imageUriFromFile, contentValues2, null, null);
            contentValues2.clear();
            contentValues2.put("_display_name", str2);
            contentValues2.put("is_pending", (Integer) 0);
            contentResolver.update(imageUriFromFile, contentValues2, null, null);
            return true;
        } catch (SecurityException e) {
            RecoverableSecurityException recoverableSecurityException = (RecoverableSecurityException) e;
            if (Build.VERSION.SDK_INT >= 26) {
                try {
                    activity.startIntentSenderForResult(recoverableSecurityException.getUserAction().getActionIntent().getIntentSender(), 2, null, 0, 0, 0, null);
                } catch (IntentSender.SendIntentException e2) {
                    e2.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        }
    }

    public static String getPath(Uri uri, Context context) {
        Cursor query = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
        if (query == null) {
            return null;
        }
        int columnIndexOrThrow = query.getColumnIndexOrThrow("_data");
        query.moveToFirst();
        String string = query.getString(columnIndexOrThrow);
        query.close();
        return string;
    }
}
