package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Model.VaultSnap;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyViewCustom.SquareImagesView;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import dev.dworks.libs.astickyheader.SimpleSectionedGridAdapter;

public class ActPrivacySecretSnap extends ActBase {

    private SwitchCompat secretSnapSwitch;
    private GridView secretSnapGridView;
    private TextView noDataFoundTxt;

    public static int VIEW_IMG_REQUEST_CODE = 28;
    private SecretSnapAdapter secretSnapAdapter;

    private ArrayList<VaultSnap> imgArrayList = new ArrayList<>();
    private ArrayList<SimpleSectionedGridAdapter.Section> sections = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_privacy_secret_snap);

        idBind();
        setOnBackPressed();

        int[][] states = new int[][]{new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}};
        int[] thumbColors = new int[]{getResources().getColor(R.color.switch_color_1), getResources().getColor(R.color.switch_color_2)};
        int[] trackColors = new int[]{getResources().getColor(R.color.switch_color_3), getResources().getColor(R.color.switch_color_4)};

        secretSnapSwitch.setThumbTintList(new ColorStateList(states, thumbColors));
        secretSnapSwitch.setTrackTintList(new ColorStateList(states, trackColors));

        secretSnapSwitch.setChecked(MyPreference.get_IsEnableSecretSnap());
        secretSnapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                MyPreference.set_IsEnableSecretSnap(checked);
            }
        });
        startNextTask();
    }

    private void idBind() {
        secretSnapSwitch = findViewById(R.id.secretSnapSwitch);
        secretSnapGridView = findViewById(R.id.secretSnapGridView);
        noDataFoundTxt = findViewById(R.id.noDataFoundTxt);
    }

    public String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public void getFromSdcard() {
        String miliSec;
        imgArrayList = new ArrayList<>();

        if (UtilApp.secretSnapFile.isDirectory()) {
            File[] listFile = UtilApp.secretSnapFile.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].getAbsolutePath().contains("SecretSnap")) {
                    String strPath = listFile[i].getAbsolutePath();
                    String imgName = strPath.substring(strPath.lastIndexOf("/") + 1, strPath.lastIndexOf("."));
                    if (imgName.contains("_")) {
                        miliSec = imgName.substring(imgName.indexOf("_") + 1).replace("_", ".");
                    } else {
                        miliSec = imgName.substring(9);
                    }
                    String captureDate = getDate(Long.parseLong(miliSec), "dd MMM yyyy");
                    VaultSnap model = new VaultSnap();
                    model.setImgPath(listFile[i].getAbsolutePath());
                    model.setImgName(imgName);
                    model.setCapMillis(miliSec);
                    this.imgArrayList.add(model);
                }
            }

            Collections.sort(this.imgArrayList, new Comparator<VaultSnap>() {
                final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

                @Override
                public int compare(VaultSnap t1, VaultSnap t2) {
                    Date d1 = null;
                    Date d2 = null;
                    try {
                        d1 = this.sdf.parse(getDate(Long.parseLong(t1.getCapMillis()), "dd MMM yyyy"));
                        d2 = this.sdf.parse(getDate(Long.parseLong(t2.getCapMillis()), "dd MMM yyyy"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return d1.getTime() > d2.getTime() ? -1 : 1;
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 111:
                if (grantResults.length <= 0 || grantResults[0] != 0) {
                    Toast.makeText(this, "File access permission denied", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    startNextTask();
                    return;
                }
            case 406:
                boolean gotPermission = grantResults.length > 0;
                int length = grantResults.length;
                for (int i = 0; i < length; i++) {
                    int result = grantResults[i];
                    gotPermission &= result == 0;
                }
                if (!gotPermission) {
                    secretSnapSwitch.setChecked(false);
                    Toast.makeText(this, "Permission to access camera was denied", Toast.LENGTH_LONG).show();
                    return;
                }
                MyPreference.set_IsEnableSecretSnap(true);
                return;
            default:
                return;
        }
    }

    private void startNextTask() {
        getFromSdcard();
        initControls();
    }

    private void initControls() {
        this.sections = new ArrayList<>();
        this.secretSnapAdapter = new SecretSnapAdapter(ActPrivacySecretSnap.this);
        List arrTemp = new ArrayList();
        for (int p = 0; p < this.imgArrayList.size(); p++) {
            String date = getDate(Long.parseLong(this.imgArrayList.get(p).getCapMillis()), "dd MMM yyyy");
            if (!arrTemp.contains(date)) {
                arrTemp.add(date);
                this.sections.add(new SimpleSectionedGridAdapter.Section(p, date));
                StringBuilder sb = new StringBuilder();
                sb.append("Section Array Size : ");
                sb.append(this.sections.size());
                sb.append(" || Header : ");
                ArrayList<SimpleSectionedGridAdapter.Section> arrayList = this.sections;
                sb.append((Object) arrayList.get(arrayList.size() - 1).getTitle());
            }
        }
        if (this.imgArrayList.size() > 0) {
            noDataFoundTxt.setVisibility(View.GONE);
        } else {
            noDataFoundTxt.setVisibility(View.VISIBLE);
        }
        SimpleSectionedGridAdapter simpleSectionedGridAdapter = new SimpleSectionedGridAdapter(this, secretSnapAdapter,
                R.layout.item_privacy_secret_snap_header, R.id.header_layout, R.id.secretSnapDateTxt);

        simpleSectionedGridAdapter.setGridView(secretSnapGridView);
        simpleSectionedGridAdapter.setSections((SimpleSectionedGridAdapter.Section[]) this.sections.toArray(new SimpleSectionedGridAdapter.Section[0]));
        secretSnapGridView.setAdapter((ListAdapter) simpleSectionedGridAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == VIEW_IMG_REQUEST_CODE) {
            refreshActivity();
        }
    }

    public void refreshActivity() {
        startNextTask();
    }


    public class SecretSnapAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;

        public SecretSnapAdapter(Context context) {
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imgArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final RowHolder holder;
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.item_privacy_secret_snap, parent, false);
                holder = new RowHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (RowHolder) convertView.getTag();
            }
            if (holder.secretSnapImg.getTag() != null) {
                ((ImageGetter) holder.secretSnapImg.getTag()).cancel(true);
            }
            ImageGetter task = new ImageGetter(holder.secretSnapImg);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new File(imgArrayList.get(position).getImgPath()));
            holder.secretSnapImg.setTag(task);
            holder.secretSnapImg.setId(position);
            holder.secretSnapImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(ActPrivacySecretSnap.this, ActPrivacySecretSnapShow.class);
                    intent.putExtra("imgPath", imgArrayList.get(holder.secretSnapImg.getId()).getImgPath());
                    intent.putExtra("imgCapMilli", imgArrayList.get(holder.secretSnapImg.getId()).getCapMillis());
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(ActPrivacySecretSnap.this, view, "snap");
                    startActivityForResult(intent, VIEW_IMG_REQUEST_CODE, options.toBundle());
                }
            });
            return convertView;
        }
    }


    public class ImageGetter extends AsyncTask<File, Void, Bitmap> {

        private final SquareImagesView squareImagesView;

        public ImageGetter(SquareImagesView img) {
            this.squareImagesView = img;
        }

        @Override
        public Bitmap doInBackground(File... params) {
            return UtilApp.decodeSampledBitmapFromFile(params[0].getAbsolutePath(), 100, 100);
        }


        @Override
        public void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            this.squareImagesView.setImageBitmap(result);
        }
    }


    public class RowHolder {

        SquareImagesView secretSnapImg;

        public RowHolder(View view) {
            secretSnapImg = (SquareImagesView) view.findViewById(R.id.secretSnapImg);
        }
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActPrivacySecretSnap.this, () -> {
                    finish();
                });
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        findViewById(R.id.back_img).setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}