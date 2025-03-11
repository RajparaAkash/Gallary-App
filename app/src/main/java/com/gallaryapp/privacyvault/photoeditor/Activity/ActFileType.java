package com.gallaryapp.privacyvault.photoeditor.Activity;

import static com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp.isMediaTypeActChange;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterFileType;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.NativeAds;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;

public class ActFileType extends ActBase {

    private TextView headerTxt;
    private RecyclerView mediaTypeRv;
    private TextView noDataFoundTxt;

    private AdapterFileType adapter;
    private ArrayList<Media> mediaItems;
    private String category;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_file_type);

        idBind();
        setOnBackPressed();

        category = getIntent().getStringExtra("category");
        headerTxt.setText(category);

        mediaTypeRv.setLayoutManager(new GridLayoutManager(this, 3));
        mediaItems = getMediaItems(category);

        if (mediaItems.size() == 0) {
            mediaTypeRv.setVisibility(View.GONE);
            noDataFoundTxt.setVisibility(View.VISIBLE);
        } else {
            adapter = new AdapterFileType(this, mediaItems);
            mediaTypeRv.setAdapter(adapter);

            // NativeSmall
            NativeAds.ShowNativeSmall(this, findViewById(R.id.nativeSmallLay), findViewById(R.id.nativeLay));
        }
    }

    public void refreshData() {
        mediaItems.clear();
        mediaItems = getMediaItems(category);
        adapter = new AdapterFileType(this, mediaItems);
        mediaTypeRv.setAdapter(adapter);
    }

    private void idBind() {
        headerTxt = findViewById(R.id.headerTxt);
        mediaTypeRv = findViewById(R.id.mediaTypeRv);
        noDataFoundTxt = findViewById(R.id.noDataFoundTxt);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            if (isMediaTypeActChange) {
                refreshData();
                isMediaTypeActChange = false;
            }
        }
    }

    private ArrayList<Media> getMediaItems(String category) {
        ArrayList<Media> items = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.RESOLUTION, // This might be null on some devices
                MediaStore.Files.FileColumns.SIZE
        };
        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
        String selection;
        String[] selectionArgs;
        int widthMedia = 0;
        int heightMedia = 0;

        switch (category) {
            case "Portrait":
                selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ? OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?) AND " + MediaStore.Files.FileColumns.MIME_TYPE + " NOT LIKE ?";
                selectionArgs = new String[]{
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                        "image/gif%"
                };
                break;

            case "High Definition":
                selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ? OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?) AND " + MediaStore.Files.FileColumns.SIZE + " > ? AND " + MediaStore.Files.FileColumns.MIME_TYPE + " NOT LIKE ?";
                selectionArgs = new String[]{
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                        "600000",
                        "image/gif%"
                };
                break;

            case "Animated":
                selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = ? AND " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?";
                selectionArgs = new String[]{
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                        "image/gif"
                };
                break;

            default:
                return items;
        }

        try (Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)) {
            if (cursor != null) {
                int resolutionColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.RESOLUTION);
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));

                    if (resolutionColumnIndex != -1) {
                        String resolution = cursor.getString(resolutionColumnIndex);
                        if (resolution != null) {
                            String[] parts = resolution.split("×");
                            if (parts.length == 2) {
                                widthMedia = Integer.parseInt(parts[0]);
                                heightMedia = Integer.parseInt(parts[1]);
                            }
                        }
                    }

                    if (category.equals("Portrait") && (widthMedia < heightMedia)) {
                        items.add(new Media(id, path, fileName));
                    } else if (!category.equals("Portrait")) {
                        items.add(new Media(id, path, fileName));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }


//    private ArrayList<Media> getMediaItems(String category) {
//        ArrayList<Media> items = new ArrayList<>();
//        ContentResolver contentResolver = getContentResolver();
//        Uri uri;
//        String[] projection;
//        String selection;
//        String[] selectionArgs;
//        String sortOrder;
//        int widthMedia = 0;
//        int HeightMedia = 0;
//
//        uri = MediaStore.Files.getContentUri("external");
//        projection = new String[]{
//                MediaStore.Files.FileColumns._ID,
//                MediaStore.Files.FileColumns.MEDIA_TYPE,
//                MediaStore.Files.FileColumns.MIME_TYPE,
//                MediaStore.Files.FileColumns.DATA,
//                MediaStore.Files.FileColumns.DISPLAY_NAME,
//                MediaStore.Files.FileColumns.RESOLUTION,
//                MediaStore.Files.FileColumns.SIZE};
//        sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
//
//        switch (category) {
//            case "Portrait":
//                selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ? OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?) AND " + MediaStore.Files.FileColumns.MIME_TYPE + " NOT LIKE ?";
//                selectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO), "image/gif%"};
//                break;
//
//            case "High Definition":
//                selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ? OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?) AND " + MediaStore.Files.FileColumns.SIZE + " > ? AND " + MediaStore.Files.FileColumns.MIME_TYPE + " NOT LIKE ?";
//                selectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO), "600000", "image/gif%"};
//                break;
//
//            case "Animated":
//                selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = ? AND " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?";
//                selectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), "image/gif"};
//                break;
//
//            default:
//                return items;
//        }
//
//        try {
//            Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
//            if (cursor != null) {
//                while (cursor.moveToNext()) {
//                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
//                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
//                    String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
//                    String RESOLUTION = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.RESOLUTION));
//
//                    String[] parts = null;
//
//                    if (RESOLUTION != null) {
//                        parts = RESOLUTION.split("×");
//                    }
//
//                    if (parts != null && parts.length == 2) {
//                        widthMedia = Integer.parseInt(parts[0]);
//                        HeightMedia = Integer.parseInt(parts[1]);
//                    }
//
//                    if (category.equals("Portrait") && (widthMedia < HeightMedia)) {
//                        items.add(new Media(id, path, fileName));
//                    } else if (!category.equals("Portrait")) {
//                        items.add(new Media(id, path, fileName));
//                    }
//                }
//                cursor.close();
//            }
//            return items;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return items;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 140 && resultCode == -1) {
            if (adapter != null) {
                ArrayList<Media> deletedList = data.getParcelableArrayListExtra("deletedPosList");
                for (int i = 0; i < deletedList.size(); i++) {
                    adapter.removeSelectedMedia(deletedList.get(i));
                }

                new Handler().post(() -> {
                    if (adapter.getItemCount() == 0) {
                        finish();
                    }
                });
            }
        }
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActFileType.this, () -> {
                    finish();
                });
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        findViewById(R.id.back_img).setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}