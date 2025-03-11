package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static String getFileSize(Context context, Uri uri) {
        String path = getPathFromUri(context, uri);
        File file = new File(path);
        long size = file.length();
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return (size / 1024) + " KB";
        } else {
            return (size / (1024 * 1024)) + " MB";
        }
    }

    public static String getFileSize(File file) {
        long size = file.length();
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return (size / 1024) + " KB";
        } else {
            return (size / (1024 * 1024)) + " MB";
        }
    }

    public static String getFileSize(String path) {
        File file = new File(path);
        long size = file.length();
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return (size / 1024) + " KB";
        } else {
            return (size / (1024 * 1024)) + " MB";
        }
    }

    public static long getFileSizeInKb(File file) {
        long size = file.length();
        return size / 1024;
    }

    public static long getFileSizeInMb(File file) {
        long size = file.length();
        return size / (1024 * 1024);
    }

    public static String getPathFromUri(Context context, Uri uri) {
        String path = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            path = uri.getPath();
        } else {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
                cursor.close();
            }
        }
        return path;
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static File createFile(String path, String fileName) {
        File file = new File(path, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static long getAvailableExternalStorageSize() {
        if (isExternalStorageReadable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return availableBlocks * blockSize;
        } else {
            return 0;
        }
    }

    public static long getTotalExternalStorageSize() {
        if (isExternalStorageReadable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            return totalBlocks * blockSize;
        } else {
            return 0;
        }
    }

    public static String formatFileSize(long size) {
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int index = 0;
        while (size >= 1024) {
            size /= 1024;
            index++;
        }
        return size + " " + units[index];
    }

    public static String formatFileSize(double size) {
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int index = 0;
        while (size >= 1024) {
            size /= 1024;
            index++;
        }
        return BigDecimal.valueOf(size).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " " + units[index];
    }

    public static String formatFileSize(Context context, Uri uri) {
        String path = getPathFromUri(context, uri);
        File file = new File(path);
        return formatFileSize(file.length());
    }

    public static String formatFileSize(Context context, File file) {
        return formatFileSize(file.length());
    }

    public static String formatFileSize(String path) {
        File file = new File(path);
        return formatFileSize(file.length());
    }

    public static boolean isImage(String path) {
        if (path == null) {
            return false; // or you can throw an IllegalArgumentException if null is not expected
        }

        String[] extensions = new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        for (String extension : extensions) {
            if (path.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVideo(String path) {
        String[] extensions = new String[]{".mp4", ".mov", ".avi", ".wmv", ".flv"};
        for (String extension : extensions) {
            if (path.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAudio(String path) {
        String[] extensions = new String[]{".mp3", ".wav", ".ogg", ".aac", ".flac"};
        for (String extension : extensions) {
            if (path.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDocument(String path) {
        String[] extensions = new String[]{".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx"};
        for (String extension : extensions) {
            if (path.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCompressed(String path) {
        String[] extensions = new String[]{".zip", ".rar", ".7z", ".tar", ".gz"};
        for (String extension : extensions) {
            if (path.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public static String getMimeType(String path) {
        String mimeType = null;
        if (isImage(path)) {
            mimeType = "image/*";
        } else if (isVideo(path)) {
            mimeType = "video/*";
        } else if (isAudio(path)) {
            mimeType = "audio/*";
        } else if (isDocument(path)) {
            mimeType = "application/*";
        } else if (isCompressed(path)) {
            mimeType = "application/octet-stream";
        } else {
            mimeType = "*/*";
        }
        return mimeType;
    }

    public static String getExtension(String path) {
        int index = path.lastIndexOf(".");
        if (index != -1) {
            return path.substring(index);
        } else {
            return "";
        }
    }

    public static String getFileNameWithoutExtension(String path) {
        int index = path.lastIndexOf(".");
        if (index != -1) {
            return path.substring(0, index);
        } else {
            return path;
        }
    }

    public static String getFileNameFromUri(Context context, Uri uri) {
        String path = getPathFromUri(context, uri);
        int index = path.lastIndexOf("/");
        if (index != -1) {
            return path.substring(index + 1);
        } else {
            return path;
        }
    }

    public static String getFileNameFromPath(String path) {
        int index = path.lastIndexOf("/");
        if (index != -1) {
            return path.substring(index + 1);
        } else {
            return path;
        }
    }

    public static String getDirectoryPath(String path) {
        int index = path.lastIndexOf("/");
        if (index != -1) {
            return path.substring(0, index);
        } else {
            return path;
        }
    }

    public static boolean isFileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static boolean isFileEmpty(String path) {
        File file = new File(path);
        return file.length() == 0;
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    public static boolean copyFile(String srcPath, String dstPath) {
        try {
            FileInputStream in = new FileInputStream(srcPath);
            FileOutputStream out = new FileOutputStream(dstPath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean moveFile(String srcPath, String dstPath) {
        if (copyFile(srcPath, dstPath)) {
            return deleteFile(srcPath);
        } else {
            return false;
        }
    }

    public static boolean renameFile(String path, String newName) {
        File file = new File(path);
        return file.renameTo(new File(file.getParent(), newName));
    }

    public static String getFileMimeType(String path) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        String mimeType = options.outMimeType;
        return mimeType;
    }

    public static String getFileLastModified(String path) {

        File imageFile = new File(path);
        if (imageFile.exists()) {
            long lastModified = imageFile.lastModified();

            Date date = new Date(lastModified);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.getDefault());
            String modifiedDate = sdf.format(date);
            return modifiedDate;
        }
        return " ";
    }

    public static String getFileResolution(String path) {

        File imageFile = new File(path);
        if (imageFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            int width = options.outWidth;
            int height = options.outHeight;
            return width + "X" + height;
        }
        return " ";
    }

    public static String getVideoResolution(String path) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);

        String resolution = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) + "X" +
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

        return resolution;
    }

    public static String getVideoMimeType(String path) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);

        String mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);

        return mimeType;
    }
}
