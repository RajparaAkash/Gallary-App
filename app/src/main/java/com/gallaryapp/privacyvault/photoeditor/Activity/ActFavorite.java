package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterFavourite;
import com.gallaryapp.privacyvault.photoeditor.Ads.BannerAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.DataHelper.DBFavourite;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FavoriteHelper;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActFavorite extends ActBase {

    private ImageView favMoreImg;
    private TextView noDataFoundTxt;
    private RecyclerView favouriteRV;

    AdapterFavourite adapterFavourite;
    ArrayList<Media> favArrayList;
    ArrayList<Media> filteredList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_favorite);

        idBind();
        setOnBackPressed();

        favouriteData();

        favMoreImg.setOnClickListener(view -> dialogMore());
    }

    private void dialogMore() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_more_favorite, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        LinearLayout fv_more_empty_favorite = bottomSheetDialog.findViewById(R.id.fv_more_empty_favorite);

        fv_more_empty_favorite.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            unFavoriteAll();
        });
    }

    private void unFavoriteAll() {
        UnFavoriteAllTask task = new UnFavoriteAllTask();
        task.execute();
    }

    private class UnFavoriteAllTask extends AsyncTask<Void, Integer, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActFavorite.this, R.style.ProgressDialogStyle);
            progressDialog.setMessage(getResources().getString(R.string.str_166));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(0);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            int totalFiles = favArrayList.size();
            for (int i = 0; i < totalFiles; i++) {
                addFavoriteItem(favArrayList.get(i).getPath());
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            favArrayList.clear();
            favouriteData();
        }
    }

    public void addFavoriteItem(String path) {
        String likeListData = FavoriteHelper.getPreferenceString(ActFavorite.this, "likeList", "");
        List<String> likeList = new ArrayList<String>();
        if (!likeListData.isEmpty()) {
            likeList.addAll(Arrays.asList(likeListData.split(",")));
        }
        if (likeList.contains(path)) {
            likeList.remove(path);
            DBFavourite dbFavourite = new DBFavourite(ActFavorite.this);
            dbFavourite.deleteData(path);

        } else {
            likeList.add(path);
        }
        String str = String.join(",", likeList);
        FavoriteHelper.setPreferenceString(ActFavorite.this, "likeList", str);
    }

    private void idBind() {
        favMoreImg = findViewById(R.id.favMoreImg);
        noDataFoundTxt = findViewById(R.id.noDataFoundTxt);
        favouriteRV = findViewById(R.id.favouriteRV);
    }

    public ArrayList<Media> getFavoriteData() {

        DBFavourite dbFavourite = new DBFavourite(this);
        Cursor cursor = dbFavourite.readdata();

        favArrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(1);
            GridLayoutManager manager = new GridLayoutManager(this, 3);
            favouriteRV.setLayoutManager(manager);
            Media dat = new Media(id);
            favArrayList.add(dat);
        }

        return favArrayList;
    }

    public void favouriteData() {
        favArrayList = getFavoriteData();
        if (favArrayList.size() > 0) {

            // Adaptive_Banner
            new BannerAds(this).AdaptiveBanner(findViewById(R.id.adaptiveBanner));

            adapterFavourite = new AdapterFavourite(this, favArrayList, new AdapterFavourite.ClickFavImg() {
                @Override
                public void onClickFavImg(int pos) {
                    if (favArrayList.size() == 0) {
                        favMoreImg.setVisibility(View.GONE);
                        noDataFoundTxt.setVisibility(View.VISIBLE);
                    } else {
                        noDataFoundTxt.setVisibility(View.GONE);
                    }
                }
            }, new AdapterFavourite.Click() {
                @Override
                public void onItemClick(int pos) {

                    if (filteredList != null) {
                        favArrayList = filteredList;
                    }

                    InterstitialAds.ShowInterstitial(ActFavorite.this, () -> {
                        Intent i = new Intent(ActFavorite.this, ActFavoritePager.class);
                        i.putExtra("data", favArrayList);
                        i.putExtra("position", pos);
                        startActivity(i);
                    });
                }
            });
            favouriteRV.setAdapter(adapterFavourite);
            favouriteRV.setVisibility(View.VISIBLE);

        } else {
            favMoreImg.setVisibility(View.GONE);
            favouriteRV.setVisibility(View.GONE);
            noDataFoundTxt.setVisibility(View.VISIBLE);
        }
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActFavorite.this, () -> {
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