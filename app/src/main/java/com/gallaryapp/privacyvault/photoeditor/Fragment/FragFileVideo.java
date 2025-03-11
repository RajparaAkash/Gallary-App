package com.gallaryapp.privacyvault.photoeditor.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.gallaryapp.privacyvault.photoeditor.Activity.ActVideoView;

import java.io.File;

public class FragFileVideo extends FragBase {

    ImageView imageView;
    ImageView playVideoImg;

    public static FragFileVideo newInstance(Media media, int position) {
        return (FragFileVideo) FragBase.newInstance(new FragFileVideo(), media, position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_file_video, container, false);

        idBinding(view);

        playVideoImg.setOnClickListener(view12 -> {
            playVideo();
        });

        return view;
    }

    private void idBinding(View view) {
        imageView = (ImageView) view.findViewById(R.id.imageView);
        playVideoImg = (ImageView) view.findViewById(R.id.playVideoImg);
    }

    public void playVideo() {
        try {
            if (media.getFile() != null && media.getFile().exists()) {
                String videoPath = media.getPath();
                if (videoPath != null && !videoPath.isEmpty()) {
                    // TAG
                    InterstitialAds.ShowInterstitial(getActivity(), () -> {
                        Intent intent = new Intent(getActivity(), ActVideoView.class);
                        intent.putExtra("videoPath", videoPath);
                        startActivity(intent);
                        requireActivity().overridePendingTransition(0, 0);
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Uri mediaUri;
        super.onViewCreated(view, savedInstanceState);
        try {
            imageView.setTransitionName(String.valueOf(this.media.getId()));
            if (Build.VERSION.SDK_INT >= 24) {
                File imgFile = this.media.getFile();
                mediaUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", imgFile);
            } else {
                mediaUri = Uri.fromFile(this.media.getFile());
            }
            Glide.with(imageView.getContext())
                    .load(mediaUri)
                    .into(imageView);
            setTapListener(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public void onVisibilityChange(int visibility) {
        if (visibility == 8) {
            ((ActMainPager) this.mActivity).hideSystemUI();
        } else if (visibility == 0) {
            ((ActMainPager) this.mActivity).showSystemUI();
        }
    }*/
}
