package com.gallaryapp.privacyvault.photoeditor.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Activity.ActWaStatusPager;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterWaStatusDownalod;
import com.gallaryapp.privacyvault.photoeditor.Ads.BannerAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;

public class FragStatusDownlaod extends Fragment {

    RecyclerView statusDownloadedRv;
    ProgressBar progressBar;
    TextView noDataFoundTxt;
    LinearLayout adaptiveBanner;

    AdapterWaStatusDownalod adapterWaStatusDownalod;
    ArrayList<Media> statusDownloadList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.frag_status_downlaod, container, false);

        idBinding(inflate);

        statusDownloadedRv.setLayoutManager(new GridLayoutManager(getContext(), 3));

        return inflate;
    }

    private void idBinding(View inflate) {
        statusDownloadedRv = (RecyclerView) inflate.findViewById(R.id.statusDownloadedRv);
        progressBar = (ProgressBar) inflate.findViewById(R.id.progressBar);
        noDataFoundTxt = (TextView) inflate.findViewById(R.id.noDataFoundTxt);
        adaptiveBanner = (LinearLayout) inflate.findViewById(R.id.adaptiveBanner);
    }

    public class loadDataAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            noDataFoundTxt.setVisibility(View.GONE);
            statusDownloadedRv.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public Void doInBackground(Void... voids) {
            statusDownloadList = UtilApp.getDownloadedStatus(getContext());
            return null;
        }

        @Override
        public void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (statusDownloadList == null || statusDownloadList.size() == 0) {
                progressBar.setVisibility(View.GONE);
                noDataFoundTxt.setVisibility(View.VISIBLE);
            } else {

                // Adaptive_Banner
                new BannerAds(getContext()).AdaptiveBanner(adaptiveBanner);

                noDataFoundTxt.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                statusDownloadedRv.setVisibility(View.VISIBLE);

                adapterWaStatusDownalod = new AdapterWaStatusDownalod(getContext(), statusDownloadList, new AdapterWaStatusDownalod.Click() {
                    @Override
                    public void onClick(int pos) {
                        viewImage(adapterWaStatusDownalod.getMedia(), pos);
                    }
                });
                statusDownloadedRv.setAdapter(adapterWaStatusDownalod);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 140 && resultCode == -2) {
            statusDownloadedRv.setVisibility(View.GONE);
            noDataFoundTxt.setVisibility(View.VISIBLE);
            return;
        }

        if (requestCode == 140 && resultCode == -1) {
            if (adapterWaStatusDownalod != null) {
                ArrayList<Media> deletedList = data.getParcelableArrayListExtra("deletedPosList");
                for (int i = 0; i < deletedList.size(); i++) {
                    adapterWaStatusDownalod.removeSelectedMedia(deletedList.get(i));
                }
            }
        }
    }

    private void viewImage(ArrayList<Media> media, int position) {
        InterstitialAds.ShowInterstitial(getActivity(), () -> {
            Intent intent = new Intent(getContext(), ActWaStatusPager.class);
            intent.putExtra("args_media", media);
            intent.putExtra("args_position", position);
            startActivityForResult(intent, 140);
        });
    }

    public void updateDowloadData() {
        new loadDataAsync().execute(new Void[0]);
    }
}