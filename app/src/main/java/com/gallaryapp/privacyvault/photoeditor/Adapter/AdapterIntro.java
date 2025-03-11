package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.gallaryapp.privacyvault.photoeditor.Model.Intro;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.List;

public class AdapterIntro extends PagerAdapter {

    private Context context;
    private List<Intro> pages;

    public AdapterIntro(Context context, List<Intro> pages) {
        this.context = context;
        this.pages = pages;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_intro, container, false);

        ImageView wcImageView = view.findViewById(R.id.wcImageView);
        TextView titalTextView = view.findViewById(R.id.titalTextView);
        TextView desTextView = view.findViewById(R.id.desTextView);

        Intro page = pages.get(position);
        wcImageView.setImageResource(page.getImageResId());
        titalTextView.setText(page.getTextTital());
        desTextView.setText(page.getTextDes());

        container.addView(view);

        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
