package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.gallaryapp.privacyvault.photoeditor.Activity.ActVideoView;

import java.util.ArrayList;

public class AdapterPrivacyPager extends PagerAdapter {

    private Activity activity;
    private ArrayList<Media> imagePaths;

    public AdapterPrivacyPager(Activity activity, ArrayList<Media> imagePaths) {
        this.activity = activity;
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        ArrayList<Media> arrayList = this.imagePaths;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View viewLayout = inflater.inflate(R.layout.item_privacy_pager, container, false);

        ImageView imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        ImageView mPlayIcon = (ImageView) viewLayout.findViewById(R.id.ic_play_icon);

        if (FileUtils.isImage(imagePaths.get(position).getPath())) {
            mPlayIcon.setVisibility(View.GONE);
        } else if (FileUtils.isVideo(imagePaths.get(position).getPath())) {
            mPlayIcon.setVisibility(View.VISIBLE);
        }

        Glide.with(activity)
                .load(imagePaths.get(position).getPath())
                .error(R.drawable.place_holder_img)
                .into(imgDisplay);


        mPlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String videoPath = imagePaths.get(position).getPath();
                if (videoPath != null && !videoPath.isEmpty()) {
                    // TAG
                    InterstitialAds.ShowInterstitial(activity, () -> {
                        Intent intent = new Intent(activity, ActVideoView.class);
                        intent.putExtra("videoPath", videoPath);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(0, 0);
                    });
                }

            }
        });

        container.addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
