package com.gallaryapp.privacyvault.photoeditor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.gallaryapp.privacyvault.photoeditor.Model.Media;

public abstract class FragBase extends Fragment {

    protected Media media;
    private MediaTapListener mediaTapListener;
    protected int position;
    protected Activity mActivity;

    public interface MediaTapListener {
        void onViewTapped();
    }

    public static <T extends FragBase> T newInstance(T mediaFragment, Media media, int position) {
        Bundle args = new Bundle();
        args.putParcelable("args_media", media);
        args.putInt("args_media_position", position);
        mediaFragment.setArguments(args);
        return mediaFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
        if (context instanceof MediaTapListener) {
            this.mediaTapListener = (MediaTapListener) context;
        }
    }

    private void fetchArgs() {
        Bundle args = getArguments();
        if (args == null) {
            throw new RuntimeException("Must pass arguments to Media Fragments!");
        }
        this.media = (Media) getArguments().getParcelable("args_media");
        this.position = getArguments().getInt("args_media_position");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchArgs();
    }

    public void setTapListener(View view) {
        view.setOnClickListener(view2 -> {
            mediaTapListener.onViewTapped();
        });
    }
}
