package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.Fragment.FragFileImage;
import com.gallaryapp.privacyvault.photoeditor.Fragment.FragFileVideo;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;

import java.util.ArrayList;

public class AdapterMainPager extends FragmentStatePagerAdapter {

    private ArrayList<Media> media;
    private final SparseArray<Fragment> registeredFragments;

    public AdapterMainPager(FragmentManager fm, ArrayList<Media> media) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.registeredFragments = new SparseArray<>();
        this.media = media;
    }

    @Override
    public Fragment getItem(int pos) {
        Media media = this.media.get(pos);
        return FileUtils.isVideo(
                media.getPath()) ? FragFileVideo.newInstance(media, pos)
                : FragFileImage.newInstance(media, pos);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        this.registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        this.registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return this.registeredFragments.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return -2;
    }

    @Override
    public int getCount() {
        ArrayList<Media> arrayList = this.media;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }
}
