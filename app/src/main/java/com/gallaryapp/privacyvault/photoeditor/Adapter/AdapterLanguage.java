package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Interface.LanguageClickListener;
import com.gallaryapp.privacyvault.photoeditor.Model.Language;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterLanguage extends RecyclerView.Adapter<AdapterLanguage.ViewHolder> {

    private int selectedPosition;
    private final Context context;
    private final List<Language> languageList;
    private final LanguageClickListener clickListener;

    public AdapterLanguage(Context context, List<Language> languageList, LanguageClickListener languageClickListener) {
        this.context = context;
        this.languageList = new ArrayList<>(languageList);
        this.clickListener = languageClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_language, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int pos) {
        Language language = languageList.get(pos);
        viewHolder.langNameTxt.setText(language.name);
        viewHolder.langFlagImg.setImageResource(language.icon);

        if (pos == selectedPosition) {
            viewHolder.langMainLay.setBackgroundResource(R.drawable.bg_lang_selected);
            viewHolder.langSelectImg.setImageResource(R.drawable.checkbox_round_selected);
        } else {
            viewHolder.langMainLay.setBackgroundResource(R.drawable.bg_lang_unselected);
            viewHolder.langSelectImg.setImageResource(R.drawable.checkbox_round_unselected);
        }
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        clickListener.onLanguageClicked(position);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout langMainLay;
        public ImageView langFlagImg;
        public TextView langNameTxt;
        public ImageView langSelectImg;

        ViewHolder(View view) {
            super(view);
            langMainLay = view.findViewById(R.id.langMainLay);
            langFlagImg = view.findViewById(R.id.langFlagImg);
            langNameTxt = view.findViewById(R.id.langNameTxt);
            langSelectImg = view.findViewById(R.id.langSelectImg);

            langMainLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    selectedPosition = getAdapterPosition();
                    clickListener.onLanguageClicked(selectedPosition);
                    notifyDataSetChanged();
                }
            });
        }
    }
}