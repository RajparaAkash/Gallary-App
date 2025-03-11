package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterSearchAlbums;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Model.Album;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceAllAlbum;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ActSearchAlbum extends ActBase {

    EditText searchEditText;
    RecyclerView searchRV;
    TextView noDataFoundTxt;
    ProgressBar progressBar;

    public static AdapterSearchAlbums searchAllAlbumsAdapter;
    private List<Album> allAlbumSearchList = new ArrayList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search);

        idBind();
        setOnBackPressed();

        displayAlbums();

        searchAllAlbumsAdapter = new AdapterSearchAlbums(this, new InterfaceAllAlbum() {
            @Override
            public void onItemSelected(int position, ImageView imageView) {
                InterstitialAds.ShowInterstitial(ActSearchAlbum.this, () -> {
                    Intent intent = new Intent(ActSearchAlbum.this, ActAlbumFiles.class);
                    intent.putExtra("albumPos", position);
                    intent.putExtra("keyy", "SearchAlbum");
                    startActivity(intent);
                });
            }
        });

        searchRV.setLayoutManager(new GridLayoutManager(this, 3));
        searchRV.setAdapter(searchAllAlbumsAdapter);

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
                ArrayList<Album> filteredList = new ArrayList<>();
                for (Album searchData : allAlbumSearchList) {
                    if (searchData.getName().toLowerCase(Locale.getDefault()).startsWith(searchText)) {
                        filteredList.add(searchData);
                    }
                }
                searchAllAlbumsAdapter.filterList(filteredList);
            }
        });

        searchEditText.requestFocus();
    }


    public void displayAlbums() {
        progressBar.setVisibility(View.VISIBLE);

        UtilApp.getMainAllAlbum(this, MyPreference.get_AlbumsAS_SortBy(), MyPreference.get_AlbumsAS_IsAscending()).subscribeOn(Schedulers.io()).map(new Function() {
            @Override
            public Object apply(Object obj) {
                return (Album) obj;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() {
            @Override
            public void accept(Object obj) {
                searchAllAlbumsAdapter.add((Album) obj);
                allAlbumSearchList.add((Album) obj);
            }
        }, new Consumer() {
            @Override
            public void accept(Object obj) {

            }
        }, new Action() {
            @Override
            public void run() {
                displayAlbumsData();
            }
        });
    }

    public void displayAlbumsData() {
        boolean z = true;
        if (getCount() != 0 && getCount() != 1) {
            z = false;
        }
        changedNothingToShow(z);
        progressBar.setVisibility(View.GONE);
    }


    public void changedNothingToShow(boolean showEmptyView) {
        if (showEmptyView) {
            searchRV.setVisibility(View.GONE);
            noDataFoundTxt.setVisibility(View.VISIBLE);
            return;
        }
        searchRV.setVisibility(View.VISIBLE);
        noDataFoundTxt.setVisibility(View.GONE);
    }

    public int getCount() {
        return searchAllAlbumsAdapter.getItemCount();
    }

    private void idBind() {
        searchEditText = findViewById(R.id.searchEditText);
        searchRV = findViewById(R.id.searchRV);
        noDataFoundTxt = findViewById(R.id.noDataFoundTxt);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActSearchAlbum.this, () -> {
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