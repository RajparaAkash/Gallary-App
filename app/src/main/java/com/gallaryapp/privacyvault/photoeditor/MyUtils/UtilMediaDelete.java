package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.gallaryapp.privacyvault.photoeditor.Interface.OnDeleteInterface;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import kotlin.jvm.internal.Intrinsics;

public final class UtilMediaDelete {

    private static ArrayList<Media> pendingMediaList = new ArrayList<>();

    public static void shareMedia(Context context, List<? extends Media> mediaList) {

        Uri fromFile;
        Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
        HashMap types = new HashMap();
        ArrayList files = new ArrayList();

        for (Media f : mediaList) {
            String mimeType = FileUtils.getMimeType(f.getPath());
            int count = 0;
            if (types.containsKey(mimeType)) {
                Object obj = types.get(mimeType);
                Intrinsics.checkNotNull(obj);
                count = ((Number) obj).intValue();
            }
            Integer valueOf = Integer.valueOf(count);

            types.put(mimeType, valueOf);
            if (Build.VERSION.SDK_INT >= 24) {
                fromFile = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", f.getFile());
            } else {
                fromFile = Uri.fromFile(f.getFile());
            }
            Uri uri = fromFile;
            files.add(uri);
        }

        Set<String> fileTypes = types.keySet();

        String type = null;
        for (String fileType : fileTypes) {
            Object obj2 = types.get(fileType);
            Intrinsics.checkNotNull(obj2);
            int count2 = ((Number) obj2).intValue();
            if (count2 > -1) {
                type = fileType;
            }
        }

        StringBuilder sb = new StringBuilder();
        Intrinsics.checkNotNull(type);
        sb.append(type);
        sb.append("/*");
        intent.setType(sb.toString());
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", files);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.str_56)));
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void deleteWaStatusFile(Activity mActivity, ArrayList<Media> creationList) {

        Uri uriLocal;
        ArrayList urisToModify = new ArrayList();
        Iterator<Media> it = creationList.iterator();
        while (it.hasNext()) {
            Media creation = it.next();
            Uri uri = null;
            if (creation.getId() > 0) {
                uri = ContentUris.withAppendedId(FileUtils.isImage(creation.getPath()) ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        : MediaStore.Video.Media.EXTERNAL_CONTENT_URI, creation.getId());
            } else {
                if (Build.VERSION.SDK_INT >= 24) {
                    uriLocal = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".provider", creation.getFile());
                } else {
                    uriLocal = Uri.fromFile(creation.getFile());
                }
                if (uriLocal != null) {
                    uri = uriLocal;
                }
            }
            if (uri != null) {
                urisToModify.add(uri);
            }
        }
        pendingMediaList = creationList;

        PendingIntent editPendingIntent = MediaStore.createWriteRequest(mActivity.getContentResolver(), urisToModify);
        try {
            mActivity.startIntentSenderForResult(editPendingIntent.getIntentSender(),
                    androidx.appcompat.R.styleable.AppCompatTheme_windowMinWidthMajor, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteNormalFile(Activity mActivity, ArrayList<Media> mediaList, OnDeleteInterface onSuccessListener) {
        boolean z;
        Uri uriLocal;
        int i = Build.VERSION.SDK_INT;
        if (i >= 30) {
            ArrayList urisToModify = new ArrayList();
            Iterator<Media> it = mediaList.iterator();
            while (it.hasNext()) {
                Media media = it.next();
                Uri uri = null;
                if (media.getId() > 0) {
                    uri = ContentUris.withAppendedId(FileUtils.isImage(media.getPath()) ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI : MediaStore.Video.Media.EXTERNAL_CONTENT_URI, media.getId());
                } else {
                    if (Build.VERSION.SDK_INT >= 24) {
                        uriLocal = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".provider", media.getFile());
                    } else {
                        uriLocal = Uri.fromFile(media.getFile());
                    }
                    if (uriLocal != null) {
                        uri = uriLocal;
                    }
                }
                if (uri != null) {
                    urisToModify.add(uri);
                }
            }
            pendingMediaList = mediaList;

            try {
                PendingIntent editPendingIntent = MediaStore.createWriteRequest(mActivity.getContentResolver(), urisToModify);
                mActivity.startIntentSenderForResult(editPendingIntent.getIntentSender(), androidx.appcompat.R.styleable.AppCompatTheme_windowMinWidthMajor, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                throw new RuntimeException(e);
            }

        } else {
            Iterator<Media> it2 = mediaList.iterator();
            while (it2.hasNext()) {
                Media media3 = it2.next();
                boolean success = false;
                File deleteFile = new File(media3.getPath());
                if (!TextUtils.isEmpty(media3.getPath()) && deleteFile.exists()) {
                    try {
                        success = deleteFile.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!success) {
                        ContentResolver resolver = mActivity.getContentResolver();

                        try {
                            Uri uri2 = UtilApp.getUriForFile(mActivity, deleteFile);
                            if (uri2 != null) {
                                resolver.delete(uri2, null, null);
                            }
                            z = !deleteFile.exists();
                        } catch (Exception e2) {
                            z = false;
                        }
                        success = z;
                    }
                }
                if (success) {
                    onSuccessListener.onMediaDeleteSuccess(true, media3);
                    UtilApp.scanFile(mActivity, new String[]{deleteFile.getPath()});
                }
            }
            onSuccessListener.onDeleteComplete();
        }
    }

    public static void deleteMoveToPrivacyFile(Activity mActivity, ArrayList<Media> mediaList, OnDeleteInterface onDeleteProcessListener) {
        Uri uriLocal;
        pendingMediaList = new ArrayList<>();
        int i = Build.VERSION.SDK_INT;
        if (i >= 30) {
            ArrayList urisToModify = new ArrayList(mediaList.size());
            Iterator<Media> it = mediaList.iterator();
            while (it.hasNext()) {
                Media media = it.next();
                Uri uri = null;
                if (media.getId() > 0) {
                    uri = ContentUris.withAppendedId(FileUtils.isImage(media.getPath()) ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI : MediaStore.Video.Media.EXTERNAL_CONTENT_URI, media.getId());
                } else {
                    if (Build.VERSION.SDK_INT >= 24) {
                        uriLocal = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".provider", media.getFile());
                    } else {
                        uriLocal = Uri.fromFile(media.getFile());
                    }
                    if (uriLocal != null) {
                        uri = uriLocal;
                    }
                }
                if (uri != null) {
                    urisToModify.add(uri);
                }
            }
            pendingMediaList = mediaList;
            PendingIntent editPendingIntent = MediaStore.createWriteRequest(mActivity.getContentResolver(), urisToModify);

            try {
                mActivity.startIntentSenderForResult(editPendingIntent.getIntentSender(), androidx.appcompat.R.styleable.AppCompatTheme_windowNoTitle, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                throw new RuntimeException(e);
            }
        } else {
            Iterator<Media> it2 = mediaList.iterator();
            while (it2.hasNext()) {
                Media media3 = it2.next();
                boolean success = false;
                try {
                    success = new File(media3.getPath()).delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!success) {
                }
                if (success) {

                    onDeleteProcessListener.onMediaDeleteSuccess(true, media3);
                    UtilApp.scanFile(mActivity, new String[]{new File(media3.getPath()).getPath()});
                }
            }
            onDeleteProcessListener.onDeleteComplete();
        }
    }

    public static ArrayList<Media> getPendingMediaList() {
        return pendingMediaList;
    }

    public static void deletePendingMedia(Activity mActivity, int requestCode, int resultCode, OnDeleteInterface onSuccessListener) {
        if (resultCode == -1 && (requestCode == 124 || requestCode == 126)) {
            Iterator<Media> it = pendingMediaList.iterator();
            while (it.hasNext()) {
                Media media = it.next();
                try {
                    Uri uri = ContentUris.withAppendedId(FileUtils.isImage(media.getPath()) ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI : MediaStore.Video.Media.EXTERNAL_CONTENT_URI, media.getId());

                    int deletedFiles = mActivity.getContentResolver().delete(uri, null, null);
                    if (deletedFiles > 0) {
                        onSuccessListener.onMediaDeleteSuccess(true, media);
                    }
                } catch (SecurityException securityException) {
                }
            }
            onSuccessListener.onDeleteComplete();
        }
    }
}
