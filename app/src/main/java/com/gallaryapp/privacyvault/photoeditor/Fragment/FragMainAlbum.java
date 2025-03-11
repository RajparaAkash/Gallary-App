package com.gallaryapp.privacyvault.photoeditor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gallaryapp.privacyvault.photoeditor.Activity.ActAlbumFiles;
import com.gallaryapp.privacyvault.photoeditor.Activity.ActDashboard;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterAlbums;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceAllAlbum;
import com.gallaryapp.privacyvault.photoeditor.Interface.MoreClickListener;
import com.gallaryapp.privacyvault.photoeditor.Model.Album;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class FragMainAlbum extends Fragment implements MoreClickListener, InterfaceAllAlbum {

    SwipeRefreshLayout swipeRefreshAlbums;
    RecyclerView allAlbumsRv;
    TextView noDataFoundTxt;

    public static AdapterAlbums mainAllAlbumsAdapter;
    protected Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_main_album, container, false);

        ActDashboard actDashboard = (ActDashboard) getActivity();
        if (actDashboard != null) {
            actDashboard.setDbMoreClickListener(this);
        }

        idBinding(view);
        refreshAlbumGrid();

        swipeRefreshAlbums.setOnRefreshListener(() -> {
            displayAlbums();
        });

        return view;
    }

    private void idBinding(View view) {
        swipeRefreshAlbums = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshAlbums);
        allAlbumsRv = (RecyclerView) view.findViewById(R.id.allAlbumsRv);
        noDataFoundTxt = (TextView) view.findViewById(R.id.noDataFoundTxt);
    }

    public void displayAlbums() {
        if (mainAllAlbumsAdapter != null) {
            swipeRefreshAlbums.setRefreshing(true);
            mainAllAlbumsAdapter.clear();

            mainAllAlbumsAdapter.add(null);
            UtilApp.getMainAllAlbum(mActivity, MyPreference.get_AlbumsAS_SortBy(), MyPreference.get_AlbumsAS_IsAscending())
                    .subscribeOn(Schedulers.io()).map(new Function() {
                        @Override
                        public Object apply(Object obj) {
                            return (Album) obj;
                        }
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() {
                        @Override
                        public void accept(Object obj) {
                            mainAllAlbumsAdapter.add((Album) obj);
                        }
                    }, new Consumer() {
                        @Override
                        public void accept(Object obj) {

                        }
                    }, new Action() {
                        @Override
                        public void run() {
                            displayAlbumsData();
                        }
                    });
        }
    }

    public void displayAlbumsData() {
        boolean z = true;
        if (getCount() != 0) {
            z = false;
        }
        changedNothingToShow(z);
        swipeRefreshAlbums.setRefreshing(false);
    }

    public void changedNothingToShow(boolean showEmptyView) {
        if (showEmptyView) {
            allAlbumsRv.setVisibility(View.GONE);
            noDataFoundTxt.setVisibility(View.VISIBLE);
            return;
        }
        /*mainAllAlbumsAdapter.add(null);*/
        allAlbumsRv.setVisibility(View.VISIBLE);
        noDataFoundTxt.setVisibility(View.GONE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setUpColumns();
    }

    public void setUpColumns() {
        if (MyPreference.get_AlbumsAS_IsGrid()) {

            GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), MyPreference.get_Albums_GridSize(), LinearLayoutManager.VERTICAL, false);
            mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position < 0 || position >= mainAllAlbumsAdapter.getItemCount()) {
                        return -1; // Return an appropriate value if position is invalid
                    }

                    switch (mainAllAlbumsAdapter.getItemViewType(position)) {
                        case AdapterAlbums.TYPE_OPTION:
                            return MyPreference.get_Albums_GridSize();
                        case AdapterAlbums.TYPE_ALBUM:
                            return 1;
                        default:
                            return -1;
                    }
                }
            });
            /*mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (mainAllAlbumsAdapter.getItemViewType(position)) {
                        case AdapterAlbums.TYPE_OPTION:
                            return MyPreference.get_Albums_GridSize();
                        case AdapterAlbums.TYPE_ALBUM:
                            return 1;
                        default:
                            return -1;
                    }
                }
            });*/
            allAlbumsRv.setLayoutManager(mLayoutManager);

        } else {
            allAlbumsRv.setLayoutManager(new LinearLayoutManager(mActivity));
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (UtilApp.isAlbumsFragChange) {
                displayAlbums();
                UtilApp.isAlbumsFragChange = false;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UtilApp.isAlbumsFragChange) {
            displayAlbums();
            UtilApp.isAlbumsFragChange = false;
        }
    }

    private void refreshAlbum() {
        setUpColumns();
    }

    private void refreshAlbumGrid() {
        mainAllAlbumsAdapter = new AdapterAlbums(mActivity, this);
        allAlbumsRv.setAdapter(mainAllAlbumsAdapter);
        setUpColumns();
        displayAlbums();
    }

    public int getCount() {
        return mainAllAlbumsAdapter.getItemCount();
    }

    @Override
    public void onItemSelected(int position, ImageView imageView) {
        InterstitialAds.ShowInterstitial(mActivity, () -> {
            Intent intent = new Intent(mActivity, ActAlbumFiles.class);
            intent.putExtra("albumPos", position);
            intent.putExtra("keyy", "MainAlbum");
            mActivity.startActivity(intent);
        });
    }

    public void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mActivity, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_more_albums, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        LinearLayout albumDialogLy_thumbnail_columns = bottomSheetDialog.findViewById(R.id.albumDialogLy_thumbnail_columns);
        LinearLayout albumDialogLy_view_as = bottomSheetDialog.findViewById(R.id.albumDialogLy_view_as);
        ImageView albumDialogImg_view_as = bottomSheetDialog.findViewById(R.id.albumDialogImg_view_as);
        TextView albumDialogTxt_view_as = bottomSheetDialog.findViewById(R.id.albumDialogTxt_view_as);

        if (MyPreference.get_AlbumsAS_IsGrid()) {
            albumDialogLy_thumbnail_columns.setVisibility(View.VISIBLE);
            albumDialogImg_view_as.setImageResource(R.drawable.icon_view_as_list);
            albumDialogTxt_view_as.setText(getResources().getString(R.string.str_88));
        } else {
            albumDialogLy_thumbnail_columns.setVisibility(View.GONE);
            albumDialogImg_view_as.setImageResource(R.drawable.icon_view_as_grid);
            albumDialogTxt_view_as.setText(getResources().getString(R.string.str_89));
        }

        albumDialogLy_thumbnail_columns.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            dialogThumbnailColumns();
        });

        albumDialogLy_view_as.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            if (MyPreference.get_AlbumsAS_IsGrid()) {
                MyPreference.set_AlbumsAS_IsGrid(false);
            } else {
                MyPreference.set_AlbumsAS_IsGrid(true);
            }
            refreshAlbumGrid();
        });

        LinearLayout albumDialogLy_last_modified = bottomSheetDialog.findViewById(R.id.albumDialogLy_last_modified);
        LinearLayout albumDialogLy_name = bottomSheetDialog.findViewById(R.id.albumDialogLy_name);
        LinearLayout albumDialogLy_size = bottomSheetDialog.findViewById(R.id.albumDialogLy_size);
        LinearLayout albumDialogLy_created_date = bottomSheetDialog.findViewById(R.id.albumDialogLy_created_date);

        LinearLayout albumDialogLy_ascending = bottomSheetDialog.findViewById(R.id.albumDialogLy_ascending);
        LinearLayout albumDialogLy_descending = bottomSheetDialog.findViewById(R.id.albumDialogLy_descending);

        CheckBox albumDialogCb_last_modified = bottomSheetDialog.findViewById(R.id.albumDialogCb_last_modified);
        CheckBox albumDialogCb_name = bottomSheetDialog.findViewById(R.id.albumDialogCb_name);
        CheckBox albumDialogCb_size = bottomSheetDialog.findViewById(R.id.albumDialogCb_size);
        CheckBox albumDialogCb_created_date = bottomSheetDialog.findViewById(R.id.albumDialogCb_created_date);

        CheckBox albumDialogCb_ascending = bottomSheetDialog.findViewById(R.id.albumDialogCb_ascending);
        CheckBox albumDialogCb_descending = bottomSheetDialog.findViewById(R.id.albumDialogCb_descending);

        TextView text_cancel = bottomSheetDialog.findViewById(R.id.text_cancel);
        TextView text_ok = bottomSheetDialog.findViewById(R.id.text_ok);


        if (MyPreference.get_AlbumsAS_SortBy().equalsIgnoreCase("date_modified")) {
            albumDialogCb_last_modified.setChecked(true);

        } else if (MyPreference.get_AlbumsAS_SortBy().equalsIgnoreCase("bucket_display_name")) {
            albumDialogCb_name.setChecked(true);

        } else if (MyPreference.get_AlbumsAS_SortBy().equalsIgnoreCase("_size")) {
            albumDialogCb_size.setChecked(true);

        } else if (MyPreference.get_AlbumsAS_SortBy().equalsIgnoreCase("date_added")) {
            albumDialogCb_created_date.setChecked(true);
        }

        if (MyPreference.get_AlbumsAS_IsAscending()) {
            albumDialogCb_ascending.setChecked(true);
        } else {
            albumDialogCb_descending.setChecked(true);
        }

        albumDialogLy_last_modified.setOnClickListener(v -> {
            albumDialogCb_last_modified.setChecked(true);
            albumDialogCb_name.setChecked(false);
            albumDialogCb_size.setChecked(false);
            albumDialogCb_created_date.setChecked(false);
        });

        albumDialogLy_name.setOnClickListener(v -> {
            albumDialogCb_last_modified.setChecked(false);
            albumDialogCb_name.setChecked(true);
            albumDialogCb_size.setChecked(false);
            albumDialogCb_created_date.setChecked(false);
        });

        albumDialogLy_size.setOnClickListener(v -> {
            albumDialogCb_last_modified.setChecked(false);
            albumDialogCb_name.setChecked(false);
            albumDialogCb_size.setChecked(true);
            albumDialogCb_created_date.setChecked(false);
        });

        albumDialogLy_created_date.setOnClickListener(v -> {
            albumDialogCb_last_modified.setChecked(false);
            albumDialogCb_name.setChecked(false);
            albumDialogCb_size.setChecked(false);
            albumDialogCb_created_date.setChecked(true);
        });


        albumDialogLy_ascending.setOnClickListener(v -> {
            albumDialogCb_ascending.setChecked(true);
            albumDialogCb_descending.setChecked(false);
        });

        albumDialogLy_descending.setOnClickListener(v -> {
            albumDialogCb_ascending.setChecked(false);
            albumDialogCb_descending.setChecked(true);
        });

        text_cancel.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        text_ok.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

            if (albumDialogCb_last_modified.isChecked()) {
                MyPreference.set_AlbumsAS_SortBy("date_modified");

            } else if (albumDialogCb_name.isChecked()) {
                MyPreference.set_AlbumsAS_SortBy("bucket_display_name");

            } else if (albumDialogCb_size.isChecked()) {
                MyPreference.set_AlbumsAS_SortBy("_size");

            } else if (albumDialogCb_created_date.isChecked()) {
                MyPreference.set_AlbumsAS_SortBy("date_added");
            }

            if (albumDialogCb_ascending.isChecked()) {
                MyPreference.set_AlbumsAS_IsAscending(true);

            } else if (albumDialogCb_descending.isChecked()) {
                MyPreference.set_AlbumsAS_IsAscending(false);
            }

            new Handler().post(() -> {
                displayAlbums();
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

        if (MyPreference.get_Albums_GridSize() == 2) {
            columns2CheckBox.setChecked(true);
        } else if (MyPreference.get_Albums_GridSize() == 3) {
            columns3CheckBox.setChecked(true);
        } else if (MyPreference.get_Albums_GridSize() == 4) {
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
                MyPreference.set_Albums_GridSize(2);
            } else if (columns3CheckBox.isChecked()) {
                MyPreference.set_Albums_GridSize(3);
            } else if (columns4CheckBox.isChecked()) {
                MyPreference.set_Albums_GridSize(4);
            }

            new Handler().post(() -> {
                refreshAlbum();
            });
        });
    }

    @Override
    public void onMoreClicked() {
        showBottomSheetDialog();
    }
}
