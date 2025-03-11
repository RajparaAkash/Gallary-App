package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterSearchAllMedia;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;
import java.util.Locale;

public class ActSearchMedia extends ActBase {

    RecyclerView searchRV;
    EditText searchEditText;
    AdapterSearchAllMedia adapterSearchAllMedia;
    ArrayList<Media> searchData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search);

        idBind();
        setOnBackPressed();

        searchData = new ArrayList<>();
        searchData = UtilApp.getAllMediaFiles(this);

        searchRV.setLayoutManager(new GridLayoutManager(this, 3));

        adapterSearchAllMedia = new AdapterSearchAllMedia(this, searchData, new AdapterSearchAllMedia.Click() {
            @Override
            public void onClick(int pos, ArrayList<Media> searchList, ImageView imageView) {

                hideKeyBoard();

                Intent intent = new Intent(getApplicationContext(), ActMainPager.class);

                if (searchList.size() == searchData.size()) {
                    intent.setAction("ALL_SEARCH");
                    UtilApp.searchArrayList = searchData;
                } else {
                    intent.setAction("SEARCHED");
                    intent.putExtra("args_media", searchList);
                }

                intent.putExtra("args_position", pos);

                InterstitialAds.ShowInterstitial(ActSearchMedia.this, () -> {
                    startActivityForResult(intent, 140);
                });
            }
        });
        searchRV.setAdapter(adapterSearchAllMedia);


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString().toLowerCase(Locale.getDefault());
                ArrayList<Media> filteredList = new ArrayList<>();
                for (Media searchData : searchData) {
                    if (searchData.getFileName().toLowerCase(Locale.getDefault()).startsWith(searchText)) {
                        filteredList.add(searchData);
                    }
                }
                adapterSearchAllMedia.filterList(filteredList);
            }
        });

        searchEditText.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 140 && resultCode == RESULT_OK) {
            if (adapterSearchAllMedia != null) {
                ArrayList<Media> deletedPosList = data.getParcelableArrayListExtra("deletedPosList");
                for (int i = 0; i < deletedPosList.size(); i++) {
                    if (FileUtils.isImage(deletedPosList.get(i).getPath())) {
                        adapterSearchAllMedia.removeSelectedMedia(deletedPosList.get(i));
                    }
                    if (FileUtils.isVideo(deletedPosList.get(i).getPath())) {
                        adapterSearchAllMedia.removeSelectedMedia(deletedPosList.get(i));
                    }
                }
                adapterSearchAllMedia.notifyDataSetChanged();
            }
        }
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    private void idBind() {
        searchRV = findViewById(R.id.searchRV);
        searchEditText = findViewById(R.id.searchEditText);
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActSearchMedia.this, () -> {
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