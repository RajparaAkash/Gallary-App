package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import com.gallaryapp.privacyvault.photoeditor.Interface.CursorHandlers;
import com.gallaryapp.privacyvault.photoeditor.Interface.IMediaFilters;
import com.gallaryapp.privacyvault.photoeditor.Model.Album;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.Interface.MediaLoadListener;
import com.gallaryapp.privacyvault.photoeditor.Model.DateGroup;
import com.gallaryapp.privacyvault.photoeditor.Model.Querys;
import com.gallaryapp.privacyvault.photoeditor.MyApp;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.firebase.annotations.concurrent.Background;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class UtilApp {

    public static boolean isThemeChanged = false;

    public static boolean isAlbumsFragChange = false;
    public static boolean isAllMediaFragChange = false;
    public static boolean isValutActChange = false;
    public static boolean isAlbumsDataActChange = false;
    public static boolean isValutGridActChange = false;
    public static boolean isValutSearchActChange = false;
    public static boolean isMediaTypeActChange = false;

    public static ArrayList<Media> searchArrayList;
    public static ArrayList<Media> mediaArrayList;
    public static boolean isLoading = true;

    public static File privateVaultFile = new File(MyApp.getInstance().getExternalFilesDir(null), "Private_Vault");
    public static File recycleBinFile = new File(MyApp.getInstance().getExternalFilesDir(null), "Recycle_Bin");
    public static String privateUnlockPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MyGallery";
    public static String editFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MyGallery";
    public static String whatsAppStatusPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MyGallery/Status Saver";
    public static String AiBackgroundRemoverPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MyGallery/Ai Background Remover";
    public static File createNewAlbumFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    public static File secretSnapFile = new File(MyApp.getInstance().getExternalFilesDir(null), "Secret_Snap");

    public static String[] myColorList = new String[]{
            "#000000", "#ffffff", "#f44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50"
            , "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722", "#795548", "#9E9E9E", "#607D8B", "#d3b2d1", "#630947", "#a40778"
            , "#35235d", "#4c49a2", "#4800e9", "#9692ff", "#49aec0", "#69d2e7", "#a7dbd8", "#0b2e59", "#0d6d59", "#7ab317", "#a0c55f", "#fff600"
            , "#ff5a27", "#a31a48", "#dd577a", "#AF8993", "#0E768B", "#FF9A7B", "#65CE38", "#47457C", "#53C2AB", "#8763D7", "#12c19d", "#8000BF"
            , "#D47500", "#BA18AD", "#FF0004", "#d3b2d1", "#630947", "#a40778", "#35235d", "#4c49a2", "#4800e9", "#9692ff", "#49aec0", "#69d2e7"
            , "#a7dbd8", "#0b2e59", "#0d6d59", "#7ab317", "#a0c55f", "#fff600", "#ff5a27", "#a31a48", "#dd577a", "#AF8993", "#0E768B", "#FF9A7B"
            , "#65CE38", "#47457C", "#53C2AB", "#8763D7", "#12c19d", "#8000BF", "#D47500", "#BA18AD", "#FF0004"};

    public static Observable<Album> getMainAllAlbum(Context context, String sortBy, boolean isAscending) {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter observableEmitter) {
                getAlbums(context, observableEmitter, sortBy, isAscending);
            }
        });
    }

    public static void getAlbums(Context context, ObservableEmitter subscriber, String sortBy, boolean isAscending) {
        ArrayList<String> albumsNames = new ArrayList<>();

        String[] projection = {
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.DATE_TAKEN,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.BUCKET_ID,
                "parent"
        };

        Uri images = MediaStore.Files.getContentUri("external");

        StringBuilder sb = new StringBuilder();
        sb.append(sortBy);
        sb.append(isAscending ? " ASC" : " DESC");

        String BUCKET_ORDER_BY = sb.toString();

        Cursor cur = null;
        try {
            cur = context.getContentResolver().query(
                    images,
                    projection,
                    MediaStore.Files.FileColumns.MEDIA_TYPE + " = ? OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ? ",
                    new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)},
                    BUCKET_ORDER_BY
            );

            if (cur != null && cur.getCount() > 0) {
                if (cur.moveToFirst()) {
                    do {
                        String data = cur.getString(cur.getColumnIndex(MediaStore.MediaColumns.DATA));
                        String imageId = cur.getString(cur.getColumnIndex(MediaStore.MediaColumns._ID));
                        String bucket_id = cur.getString(cur.getColumnIndex(MediaStore.MediaColumns.BUCKET_ID));

                        if (!albumsNames.contains(bucket_id)) {
                            albumsNames.add(bucket_id);
                            Album asd = new Album(cur);
                            if (TextUtils.isEmpty(asd.getName())) {
                                asd.setName("InternalStorage");
                            }
                            asd.setLastMedia(new Media(data, -1L, Long.parseLong(imageId)));
                            asd.setCount(getAlbumDataCount(context, asd));
                            subscriber.onNext(asd);
                        }
                    } while (cur.moveToNext());
                }
                subscriber.onComplete();
            } else {
                subscriber.onNext(new Album());
                subscriber.onComplete();
            }
        } catch (Exception e) {
            subscriber.onError(e);
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    private static int getAlbumDataCount(Context context, Album asd) {
        Querys.Builder query = new Querys.Builder().uri(MediaStore.Files.getContentUri("external"))
                .projection(Media.getProjection());
        int i = 0;

        query.selection(String.format("(%s=? or %s=?) and %s=?", "media_type", "media_type", "parent"));
        query.args(1, 3, Long.valueOf(asd.getId()));

        Cursor cursor = query.build().getCursor(context.getContentResolver());
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    i = cursor.getCount();
                }
            } catch (Throwable th) {
                try {
                    cursor.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return i;
    }

    public static Observable<Media> getMedia(Context context, Album album) {
        return getMediaFromMediaStore(context, album);
    }

    private static Observable<Media> getMediaFromMediaStore(Context context, Album album) {
        if (album == null) {
            throw new IllegalArgumentException("Album cannot be null");
        }

        Querys.Builder query = new Querys.Builder().uri(MediaStore.Files.getContentUri("external"))
                .projection(Media.getProjection())
                .sort(MyPreference.get_AlbumsAS_SortBy()).ascending(MyPreference.get_AlbumsAS_IsAscending());

        query.selection(String.format("(%s=? or %s=?) and %s=?", "media_type", "media_type", "parent"));
        query.args(1, 3, Long.valueOf(album.getId()));

        return UtilQuery.query(query.build(), context.getContentResolver(), new CursorHandlers() {
            @Override
            public Object handle(Cursor cursor) {
                return new Media(cursor);
            }
        });
    }

    public static void scanMedia(Context context) {
        String[] paths = {Environment.getExternalStorageDirectory().toString()};
        String[] mimeTypes = {"image/*", "video/*"};

        MediaScannerConnection.scanFile(context, paths, mimeTypes, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
            }
        });
    }

    public static ArrayList<DateGroup> getAllMediaDateWise(Context context, int initialLoadCount, MediaLoadListener listener, String sortBy, boolean isAscending) {
        scanMedia(context);
        mediaArrayList = new ArrayList<>();
        mediaArrayList.clear();

        ArrayList<DateGroup> dateGroups = new ArrayList<>();
        String[] projection = {
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.MIME_TYPE};

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        String sortOrder = sortBy + (isAscending ? " ASC" : " DESC");

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                null,
                sortOrder
        );

        if (cursor != null) {
            int totalMediaCount = cursor.getCount(); // Get total number of media items
            cursor.close();

            int actualLoadCount = Math.min(initialLoadCount, totalMediaCount); // Adjust initial load count if necessary

            cursor = context.getContentResolver().query(
                    MediaStore.Files.getContentUri("external"),
                    projection,
                    selection,
                    null,
                    sortOrder
            );

            if (cursor != null) {
                HashMap<String, DateGroup> dateGroupHashMap = new HashMap<>();
                int count = 0;

                while (cursor.moveToNext() && count < actualLoadCount) {
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                    long dateTime = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED));

                    Media mediaItem = new Media(id, path, name);
                    mediaArrayList.add(mediaItem);

                    String date = getDateFromTimestamp(dateTime);

                    if (!dateGroupHashMap.containsKey(date)) {
                        DateGroup dateGroup = new DateGroup(date, new ArrayList<>());
                        dateGroupHashMap.put(date, dateGroup);
                        dateGroups.add(dateGroup);
                    }
                    dateGroupHashMap.get(date).getMediaItems().add(mediaItem);

                    count++;
                }

                cursor.close();

                // Start background task to load remaining items
                HashSet<Long> loadedIds = new HashSet<>();
                for (DateGroup dateGroup : dateGroups) {
                    for (Media media : dateGroup.getMediaItems()) {
                        loadedIds.add(media.getId());
                    }
                }
                new LoadMediaItemsTask(context, dateGroups, listener, loadedIds, sortBy, isAscending).execute();
            }
        }
        return dateGroups;
    }

    private static class LoadMediaItemsTask extends AsyncTask<Void, Void, ArrayList<DateGroup>> {

        private Context context;
        private ArrayList<DateGroup> dateGroups;
        private MediaLoadListener listener;
        private HashSet<Long> loadedIds;
        private String moreSortBy;
        private Boolean moreIsAscending;

        public LoadMediaItemsTask(Context context, ArrayList<DateGroup> dateGroups, MediaLoadListener listener, HashSet<Long> loadedIds, String moreSortBy, Boolean moreIsAscending) {
            this.context = context;
            this.dateGroups = dateGroups;
            this.listener = listener;
            this.loadedIds = loadedIds;
            this.moreSortBy = moreSortBy;
            this.moreIsAscending = moreIsAscending;
        }

        @Override
        protected ArrayList<DateGroup> doInBackground(Void... voids) {
            String[] projection = {
                    MediaStore.MediaColumns._ID,
                    MediaStore.MediaColumns.DATA,
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    MediaStore.MediaColumns.DATE_MODIFIED,
                    MediaStore.MediaColumns.DATE_ADDED,
                    MediaStore.MediaColumns.SIZE,
                    MediaStore.MediaColumns.MIME_TYPE};
            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

            String sortOrder = moreSortBy + (moreIsAscending ? " ASC" : " DESC");

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Files.getContentUri("external"),
                    projection,
                    selection + " AND " + MediaStore.MediaColumns._ID + " NOT IN (" + TextUtils.join(",", loadedIds) + ")",
                    null,
                    sortOrder
            );

            if (cursor != null) {
                HashMap<String, DateGroup> dateGroupHashMap = new HashMap<>();

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                    if (!loadedIds.contains(id)) {
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
                        long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));

                        Media mediaItem = new Media(id, path, name);
                        mediaArrayList.add(mediaItem);
                        String date = getDateFromTimestamp(dateTime);

                        DateGroup existingDateGroup = null;
                        for (DateGroup group : dateGroups) {
                            if (group.getDate().equals(date)) {
                                existingDateGroup = group;
                                break;
                            }
                        }

                        if (existingDateGroup == null) {
                            DateGroup dateGroup = new DateGroup(date, new ArrayList<>());
                            dateGroup.getMediaItems().add(mediaItem);
                            dateGroups.add(dateGroup);
                            dateGroupHashMap.put(date, dateGroup);
                        } else {
                            existingDateGroup.getMediaItems().add(mediaItem);
                        }
                    }
                }

                cursor.close();
            }
            return dateGroups;
        }

        @Override
        protected void onPostExecute(ArrayList<DateGroup> dateGroups) {
            super.onPostExecute(dateGroups);
            if (listener != null) {
                listener.onMediaLoaded(dateGroups);
            }
        }
    }

    public static String getDateFromTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd, MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp * 1000));
    }

    public static ArrayList<Media> getAllMediaFiles(Context context) {
        ArrayList<Media> searchDataList = new ArrayList<>();

        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.DISPLAY_NAME
        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        )) {
            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED);
                int mediaTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
                int mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
                int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    String path = cursor.getString(dataColumn);
                    long dateAdded = cursor.getLong(dateAddedColumn);
                    int mediaType = cursor.getInt(mediaTypeColumn);
                    String mimeType = cursor.getString(mimeTypeColumn);
                    String fileName = cursor.getString(displayNameColumn);

                    Media searchData = new Media(id, path, fileName);
                    searchDataList.add(searchData);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return searchDataList;
    }

    public static ArrayList<Media> getDownloadedStatus(Context context) {
        ArrayList<Media> creationArrayList = new ArrayList<>();

        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE
        };
        String selection = MediaStore.Files.FileColumns.DATA + " LIKE ?";
        String[] selectionArgs = new String[]{
                "%" + whatsAppStatusPath + "%"
        };
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                selectionArgs,
                null
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
                long fileSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));

                creationArrayList.add(new Media(id, filePath, fileName));
            }
            cursor.close();
        }
        return creationArrayList;
    }


    public static String getBack(String paramString1, String paramString2) {
        Matcher matcher = Pattern.compile(paramString2).matcher(paramString1);
        return matcher.find() ? matcher.group(1) : "";
    }

    public static boolean download(Context context, String sourceFile) {
        return copyFileInSavedDir(context, sourceFile);
    }

    public static boolean isVideoFile(Context context, String path) {
        if (path.startsWith("content")) {
            String type = DocumentFile.fromSingleUri(context, Uri.parse(path)).getType();
            return type != null && type.startsWith("video");
        }
        String guessContentTypeFromName = URLConnection.guessContentTypeFromName(path);
        return guessContentTypeFromName != null && guessContentTypeFromName.startsWith("video");
    }

    public static boolean copyFileInSavedDir(Context context, String sourceFile) {
        String absolutePath;
        if (isVideoFile(context, sourceFile)) {
            absolutePath = getWhastappStatusDownloadFolder().getAbsolutePath();
        } else {
            absolutePath = getWhastappStatusDownloadFolder().getAbsolutePath();
        }
        String str = absolutePath + File.separator + new File(Uri.parse(sourceFile).getPath()).getName();
        Uri fromFile = Uri.fromFile(new File(str));
        try {
            InputStream openInputStream = context.getContentResolver().openInputStream(Uri.parse(sourceFile));
            OutputStream openOutputStream = context.getContentResolver().openOutputStream(fromFile, "w");
            byte[] bArr = new byte[1024];
            while (true) {
                int read = openInputStream.read(bArr);
                if (read > 0) {
                    openOutputStream.write(bArr, 0, read);
                } else {
                    openInputStream.close();
                    openOutputStream.flush();
                    openOutputStream.close();
                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                    intent.setData(fromFile);
                    context.sendBroadcast(intent);
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    static File getWhastappStatusDownloadFolder() {
        File file = new File(whatsAppStatusPath);
        file.mkdirs();
        return file;
    }

    public static boolean appInstalledOrNot(Context context, String uri) {
        try {
            context.getPackageManager().getPackageInfo(uri, 1);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static int[] arrMainOptionIcon = {
            R.drawable.icon_edit_crop_unselected,
            R.drawable.icon_edit_text_unselected,
            R.drawable.icon_edit_sticker_unselected,
            R.drawable.icon_edit_brush_unselected};

    public static int getCameraPhotoOrientation(Context context, Uri imageUri) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imageUri.toString());
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt("Orientation", 1);
            switch (orientation) {
                case 3:
                    rotate = 180;
                    break;
                case 6:
                    rotate = 90;
                    break;
                case 8:
                    rotate = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static Typeface getTypefaceFromAsset(Context context, String fontName) {
        AssetManager assetManager = context.getAssets();
        Locale locale = Locale.US;
        Typeface typeface = Typeface.createFromAsset(assetManager, String.format(locale, "editFont/%s", fontName + ".ttf"));
        return typeface;
    }

    public static Bitmap decodeSampledBitmapFromFile(String image, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(image, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
    }

    public static void scanFile(Context context, String[] path) {
        MediaScannerConnection.scanFile(context.getApplicationContext(), path, null, null);
    }

    public static IMediaFilters getFilter() {
        return media -> true;
    }

    public static String getPhotoNameByPath(String path) {
        String[] b = path.split("/");
        String fi = b[b.length - 1];
        return fi.substring(0, fi.lastIndexOf(46));
    }

    public static String getName(String path) {
        String[] b = path.split("/");
        return b[b.length - 1];
    }

    public static String getBucketPathByImagePath(String path) {
        String[] b = path.split("/");
        String c = "";
        for (int x = 0; x < b.length - 1; x++) {
            c = c + b[x] + "/";
        }
        return c.substring(0, c.length() - 1);
    }

    public static void showToast(Context x, String s) {
        Toast t = Toast.makeText(x, s, Toast.LENGTH_SHORT);
        t.show();
    }
}
