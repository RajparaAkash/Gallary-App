package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.app.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UtilUnzipFile {

    private static String TAG = "FileZipOperation";

    public static boolean unzip(InputStream inputStream, String destDir) {
        dirChecker(destDir, "");
        byte[] buffer = new byte[2048];
        try {
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            File destDirFile = new File(destDir);
            String canonicalDestDirPath = destDirFile.getCanonicalPath();

            while (true) {
                ZipEntry nextEntry = zipInputStream.getNextEntry();
                if (nextEntry != null) {
                    String entryName = nextEntry.getName();

                    File newFile = new File(destDirFile, entryName);
                    String canonicalNewFilePath = newFile.getCanonicalPath();

                    // Path traversal protection
                    if (!canonicalNewFilePath.startsWith(canonicalDestDirPath)) {
                        throw new SecurityException("Entry is outside of the target dir: " + entryName);
                    }

                    if (nextEntry.isDirectory()) {
                        dirChecker(destDir, entryName);
                    } else {
                        File parentFile = newFile.getParentFile();
                        if (!parentFile.exists() && !parentFile.mkdirs()) {
                            throw new IOException("Failed to create directory: " + parentFile.getAbsolutePath());
                        }

                        if (!newFile.exists() && newFile.createNewFile()) {
                            try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                                int len;
                                while ((len = zipInputStream.read(buffer)) > 0) {
                                    fileOutputStream.write(buffer, 0, len);
                                }
                            }
                        } else {
                        }
                    }
                    zipInputStream.closeEntry();
                } else {
                    zipInputStream.close();
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static void dirChecker(String baseDir, String subDir) {
        File file = new File(baseDir, subDir);
        if (!file.isDirectory() && !file.mkdirs()) {

        }
    }

    public static String getStickerPath(Activity appCompatActivity) {
        String absolutePath = appCompatActivity.getFilesDir().getAbsolutePath();
        File file = new File(absolutePath + File.separator + "Sticker");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }
}
