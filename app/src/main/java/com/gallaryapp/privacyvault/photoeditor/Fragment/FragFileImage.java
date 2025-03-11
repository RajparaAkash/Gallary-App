package com.gallaryapp.privacyvault.photoeditor.Fragment;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.io.File;

public class FragFileImage extends FragBase {

    PhotoView imageView;

    public static FragFileImage newInstance(Media media, int position) {
        return (FragFileImage) newInstance(new FragFileImage(), media, position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_file_image, container, false);

        idBinding(view);

        return view;
    }

    private void idBinding(View view) {
        imageView = (PhotoView) view.findViewById(R.id.imageView);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Uri mediaUri;
        super.onViewCreated(view, savedInstanceState);
        try {
            this.imageView.setTransitionName(String.valueOf(this.media.getId()));
            if (Build.VERSION.SDK_INT >= 24) {
                File imgFile = this.media.getFile();
                mediaUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", imgFile);
            } else {
                mediaUri = Uri.fromFile(this.media.getFile());
            }
            Glide.with(imageView.getContext()).load(mediaUri).into(imageView);
            setTapListener(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rotatePicture(int rotationInDegrees) {
        if (rotationInDegrees == -90 && this.imageView.getRotation() == 0.0f) {
            this.imageView.setRotation(270.0f);
            return;
        }
        PhotoView photoView = this.imageView;
        photoView.setRotation(Math.abs(photoView.getRotation() + rotationInDegrees) % 360.0f);
    }
}
