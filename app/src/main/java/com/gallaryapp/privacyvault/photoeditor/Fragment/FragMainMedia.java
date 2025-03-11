package com.gallaryapp.privacyvault.photoeditor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gallaryapp.privacyvault.photoeditor.Activity.ActDashboard;
import com.gallaryapp.privacyvault.photoeditor.Activity.ActMainPager;
import com.gallaryapp.privacyvault.photoeditor.Activity.ActPrivacySetting;
import com.gallaryapp.privacyvault.photoeditor.Activity.ActSettings;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterAllMedia;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.Interface.MoreClickListener;
import com.gallaryapp.privacyvault.photoeditor.Interface.MediaLoadListener;
import com.gallaryapp.privacyvault.photoeditor.Model.DateGroup;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class FragMainMedia extends Fragment implements MediaLoadListener, MoreClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView allMediaRv;

    private AdapterAllMedia adapterAllMedia;
    private ArrayList<DateGroup> dateGroups;
    boolean isFirstTime = true;
    protected Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_main_media, container, false);

        idBinding(view);

        ActDashboard actDashboard = (ActDashboard) getActivity();
        if (actDashboard != null) {
            actDashboard.setDbMoreClickListener(this);
        }

        set_Adapter();
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refresh_Adapter();
        });

        return view;
    }

    public void set_Adapter() {
        UtilApp.isLoading = true;
        dateGroups = UtilApp.getAllMediaDateWise(getContext(), 500, this,
                MyPreference.get_AllMediaAS_SortBy(), MyPreference.get_AllMediaAS_IsAscending());
        refresh_Adapter();
    }

    public void refresh_Adapter() {
        int spanCount = MyPreference.get_AllMedia_GridSize();

        allMediaRv.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        ((GridLayoutManager) allMediaRv.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapterAllMedia.getItemViewType(position) == AdapterAllMedia.VIEW_TYPE_DATE ? spanCount : 1;
            }
        });

        adapterAllMedia = new AdapterAllMedia(getContext(), new AdapterAllMedia.ClickInterface() {
            @Override
            public void ClickToActivity(int position, ImageView imageView) {

                InterstitialAds.ShowInterstitial(getActivity(), () -> {
                    Media media = UtilApp.mediaArrayList.get(position);
                    Intent intent = new Intent(getContext(), ActMainPager.class);
                    intent.setAction("ALL_MEDIA");
                    intent.putExtra("args_media", media);
                    intent.putExtra("args_position", position);
                    startActivityForResult(intent, 140);
                });
            }
        });

        allMediaRv.setAdapter(adapterAllMedia);
        adapterAllMedia.setMediaItems(dateGroups);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onMediaLoaded(ArrayList<DateGroup> dateGroups) {
        // Update adapter with remaining data
        adapterAllMedia.setMediaItems(dateGroups);
        UtilApp.isLoading = false;
        swipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (UtilApp.isAllMediaFragChange) {
                UtilApp.isAllMediaFragChange = false;
                set_Adapter();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstTime) {
            if (UtilApp.isAllMediaFragChange) {
                swipeRefreshLayout.setEnabled(false);
                UtilApp.isAllMediaFragChange = false;
                set_Adapter();
            }
        }
        isFirstTime = false;
    }

    private void idBinding(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        allMediaRv = (RecyclerView) view.findViewById(R.id.allMediaRv);
    }

    public void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mActivity, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_more_all_media, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        LinearLayout allMediaDialogLy_thumbnail_columns = bottomSheetDialog.findViewById(R.id.allMediaDialogLy_thumbnail_columns);

        LinearLayout allMediaDialogLy_last_modified = bottomSheetDialog.findViewById(R.id.allMediaDialogLy_last_modified);
        LinearLayout allMediaDialogLy_name = bottomSheetDialog.findViewById(R.id.allMediaDialogLy_name);
        LinearLayout allMediaDialogLy_size = bottomSheetDialog.findViewById(R.id.allMediaDialogLy_size);
        LinearLayout allMediaDialogLy_created_date = bottomSheetDialog.findViewById(R.id.allMediaDialogLy_created_date);

        LinearLayout allMediaDialogLy_ascending = bottomSheetDialog.findViewById(R.id.allMediaDialogLy_ascending);
        LinearLayout allMediaDialogLy_descending = bottomSheetDialog.findViewById(R.id.allMediaDialogLy_descending);

        CheckBox allMediaDialogCb_last_modified = bottomSheetDialog.findViewById(R.id.allMediaDialogCb_last_modified);
        CheckBox allMediaDialogCb_name = bottomSheetDialog.findViewById(R.id.allMediaDialogCb_name);
        CheckBox allMediaDialogCb_size = bottomSheetDialog.findViewById(R.id.allMediaDialogCb_size);
        CheckBox allMediaDialogCb_created_date = bottomSheetDialog.findViewById(R.id.allMediaDialogCb_created_date);

        CheckBox allMediaDialogCb_ascending = bottomSheetDialog.findViewById(R.id.allMediaDialogCb_ascending);
        CheckBox allMediaDialogCb_descending = bottomSheetDialog.findViewById(R.id.allMediaDialogCb_descending);

        TextView text_cancel = bottomSheetDialog.findViewById(R.id.text_cancel);
        TextView text_ok = bottomSheetDialog.findViewById(R.id.text_ok);

        allMediaDialogLy_thumbnail_columns.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            dialogThumbnailColumns();
        });


        if (MyPreference.get_AllMediaAS_SortBy().equalsIgnoreCase("date_modified")) {
            allMediaDialogCb_last_modified.setChecked(true);

        } else if (MyPreference.get_AllMediaAS_SortBy().equalsIgnoreCase("_display_name")) {
            allMediaDialogCb_name.setChecked(true);

        } else if (MyPreference.get_AllMediaAS_SortBy().equalsIgnoreCase("_size")) {
            allMediaDialogCb_size.setChecked(true);

        } else if (MyPreference.get_AllMediaAS_SortBy().equalsIgnoreCase("date_added")) {
            allMediaDialogCb_created_date.setChecked(true);
        }

        if (MyPreference.get_AllMediaAS_IsAscending()) {
            allMediaDialogCb_ascending.setChecked(true);
        } else {
            allMediaDialogCb_descending.setChecked(true);
        }

        allMediaDialogLy_last_modified.setOnClickListener(v -> {
            allMediaDialogCb_last_modified.setChecked(true);
            allMediaDialogCb_name.setChecked(false);
            allMediaDialogCb_size.setChecked(false);
            allMediaDialogCb_created_date.setChecked(false);
        });

        allMediaDialogLy_name.setOnClickListener(v -> {
            allMediaDialogCb_last_modified.setChecked(false);
            allMediaDialogCb_name.setChecked(true);
            allMediaDialogCb_size.setChecked(false);
            allMediaDialogCb_created_date.setChecked(false);
        });

        allMediaDialogLy_size.setOnClickListener(v -> {
            allMediaDialogCb_last_modified.setChecked(false);
            allMediaDialogCb_name.setChecked(false);
            allMediaDialogCb_size.setChecked(true);
            allMediaDialogCb_created_date.setChecked(false);
        });

        allMediaDialogLy_created_date.setOnClickListener(v -> {
            allMediaDialogCb_last_modified.setChecked(false);
            allMediaDialogCb_name.setChecked(false);
            allMediaDialogCb_size.setChecked(false);
            allMediaDialogCb_created_date.setChecked(true);
        });


        allMediaDialogLy_ascending.setOnClickListener(v -> {
            allMediaDialogCb_ascending.setChecked(true);
            allMediaDialogCb_descending.setChecked(false);
        });

        allMediaDialogLy_descending.setOnClickListener(v -> {
            allMediaDialogCb_ascending.setChecked(false);
            allMediaDialogCb_descending.setChecked(true);
        });

        text_cancel.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        text_ok.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

            if (allMediaDialogCb_last_modified.isChecked()) {
                MyPreference.set_AllMediaAS_SortBy("date_modified");

            } else if (allMediaDialogCb_name.isChecked()) {
                MyPreference.set_AllMediaAS_SortBy("_display_name");

            } else if (allMediaDialogCb_size.isChecked()) {
                MyPreference.set_AllMediaAS_SortBy("_size");

            } else if (allMediaDialogCb_created_date.isChecked()) {
                MyPreference.set_AllMediaAS_SortBy("date_added");
            }

            if (allMediaDialogCb_ascending.isChecked()) {
                MyPreference.set_AllMediaAS_IsAscending(true);

            } else if (allMediaDialogCb_descending.isChecked()) {
                MyPreference.set_AllMediaAS_IsAscending(false);
            }

            new Handler().post(() -> {
                set_Adapter();
            });
        });

    }

    public void dialogThumbnailColumns() {
        BottomSheetDialog sortListDialog = new BottomSheetDialog(mActivity, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_thumbnail_columns, null);
        sortListDialog.setContentView(bottomSheetView);
        sortListDialog.show();

        LinearLayout columns2Lay = sortListDialog.findViewById(R.id.columns2Lay);
        LinearLayout columns3Lay = sortListDialog.findViewById(R.id.columns3Lay);
        LinearLayout columns4Lay = sortListDialog.findViewById(R.id.columns4Lay);

        CheckBox columns2CheckBox = sortListDialog.findViewById(R.id.columns2CheckBox);
        CheckBox columns3CheckBox = sortListDialog.findViewById(R.id.columns3CheckBox);
        CheckBox columns4CheckBox = sortListDialog.findViewById(R.id.columns4CheckBox);

        TextView cancelTxt = sortListDialog.findViewById(R.id.cancelTxt);
        TextView saveTxt = sortListDialog.findViewById(R.id.saveTxt);

        if (MyPreference.get_AllMedia_GridSize() == 2) {
            columns2CheckBox.setChecked(true);
        } else if (MyPreference.get_AllMedia_GridSize() == 3) {
            columns3CheckBox.setChecked(true);
        } else if (MyPreference.get_AllMedia_GridSize() == 4) {
            columns4CheckBox.setChecked(true);
        }

        columns2Lay.setOnClickListener(v -> {
            columns2CheckBox.setChecked(true);
            columns3CheckBox.setChecked(false);
            columns4CheckBox.setChecked(false);
        });

        columns3Lay.setOnClickListener(v -> {
            columns2CheckBox.setChecked(false);
            columns3CheckBox.setChecked(true);
            columns4CheckBox.setChecked(false);
        });

        columns4Lay.setOnClickListener(v -> {
            columns2CheckBox.setChecked(false);
            columns3CheckBox.setChecked(false);
            columns4CheckBox.setChecked(true);
        });

        cancelTxt.setOnClickListener(v -> {
            sortListDialog.dismiss();
        });

        saveTxt.setOnClickListener(v -> {
            sortListDialog.dismiss();

            if (columns2CheckBox.isChecked()) {
                MyPreference.set_AllMedia_GridSize(2);
            } else if (columns3CheckBox.isChecked()) {
                MyPreference.set_AllMedia_GridSize(3);
            } else if (columns4CheckBox.isChecked()) {
                MyPreference.set_AllMedia_GridSize(4);
            }

            new Handler().post(() -> {
                refresh_Adapter();
            });
        });
    }

    @Override
    public void onMoreClicked() {
        showBottomSheetDialog();
    }
}
