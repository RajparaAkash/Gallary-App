package com.gallaryapp.privacyvault.photoeditor.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterWaStatus;
import com.gallaryapp.privacyvault.photoeditor.Ads.BannerAds;
import com.gallaryapp.privacyvault.photoeditor.Model.WaStatus;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.SharedPrefs;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class FragStatusWB extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView statusRv;
    LinearLayout permissionLay;
    ProgressBar progressBar;
    ConstraintLayout noDataFoundTxt;
    ImageView openWaImg;
    LinearLayout adaptiveBanner;

    loadDataAsync async;
    AdapterWaStatus adapterWaStatus;
    ArrayList<WaStatus> statusList = new ArrayList<>();
    int REQUEST_ACTION_OPEN_DOCUMENT_TREE = 1001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.frag_status, container, false);

        idBinding(inflate);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
        });

        permissionLay.setOnClickListener(view -> {
            waStatusPermission();
        });

        openWaImg.setOnClickListener(view -> {
            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.whatsapp.w4b");
            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "WhatsApp Business is not installed", Toast.LENGTH_SHORT).show();
            }
        });

        statusRv.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        if (!SharedPrefs.getWBTree(getActivity()).equals("")) {
            populateGrid();
        } else {
            permissionLay.setVisibility(View.VISIBLE);
        }

        return inflate;
    }

    private void idBinding(View inflate) {
        swipeRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.swipeRefreshLayout);
        statusRv = (RecyclerView) inflate.findViewById(R.id.statusRv);
        permissionLay = (LinearLayout) inflate.findViewById(R.id.permissionLay);
        progressBar = (ProgressBar) inflate.findViewById(R.id.progressBar);
        noDataFoundTxt = (ConstraintLayout) inflate.findViewById(R.id.noDataFoundTxt);
        openWaImg = (ImageView) inflate.findViewById(R.id.openWaImg);
        adaptiveBanner = (LinearLayout) inflate.findViewById(R.id.adaptiveBanner);
    }

    public void refreshData() {
        if (!SharedPrefs.getWBTree(getActivity()).equals("")) {
            if (adapterWaStatus != null) {
                adapterWaStatus.notifyDataSetChanged();
            }
            populateGrid();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public void waStatusPermission() {
        Intent intent;
        if (UtilApp.appInstalledOrNot(getActivity(), "com.whatsapp.w4b")) {
            StorageManager storageManager = (StorageManager) getActivity().getSystemService(Context.STORAGE_SERVICE);
            String whatsupFolder = getWhatsupFolder();
            if (Build.VERSION.SDK_INT >= 29) {
                intent = storageManager.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
                String replace = ((Uri) intent.getParcelableExtra("android.provider.extra.INITIAL_URI")).toString().replace("/root/", "/document/");
                intent.putExtra("android.provider.extra.INITIAL_URI", Uri.parse(replace + "%3A" + whatsupFolder));
            } else {
                intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
                intent.putExtra("android.provider.extra.INITIAL_URI", Uri.parse("content://com.android.externalstorage.documents/document/primary%3A" + whatsupFolder));
            }
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(intent, this.REQUEST_ACTION_OPEN_DOCUMENT_TREE);
            return;
        }
        Toast.makeText(getActivity(), "Please Install WhatsApp Business For Download Status", Toast.LENGTH_SHORT).show();
    }

    public void populateGrid() {
        loadDataAsync loaddataasync = new loadDataAsync();
        this.async = loaddataasync;
        loaddataasync.execute(new Void[0]);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loadDataAsync loaddataasync = this.async;
        if (loaddataasync != null) {
            loaddataasync.cancel(true);
        }
    }

    public class loadDataAsync extends AsyncTask<Void, Void, Void> {
        DocumentFile[] allFiles;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusRv.setVisibility(View.GONE);
            permissionLay.setVisibility(View.GONE);
            noDataFoundTxt.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public Void doInBackground(Void... voids) {
            this.allFiles = null;
            statusList = new ArrayList<>();
            DocumentFile[] fromSdcard = FragStatusWB.this.getFromSdcard();
            this.allFiles = fromSdcard;
            Arrays.sort(fromSdcard, new Comparator() {
                @Override
                public int compare(Object obj, Object obj2) {
                    int compare;
                    compare = Long.compare(((DocumentFile) obj2).lastModified(), ((DocumentFile) obj).lastModified());
                    return compare;
                }
            });
            int i = 0;
            while (true) {
                DocumentFile[] documentFileArr = this.allFiles;
                if (i >= documentFileArr.length) {
                    return null;
                }
                if (!documentFileArr[i].getUri().toString().contains(".nomedia")) {
                    statusList.add(new WaStatus(this.allFiles[i].getUri().toString()));
                }
                i++;
            }
        }

        @Override
        public void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (getActivity() != null) {
                if (statusList == null || statusList.size() == 0) {
                    noDataFoundTxt.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                } else {

                    // Adaptive_Banner
                    new BannerAds(getContext()).AdaptiveBanner(adaptiveBanner);

                    noDataFoundTxt.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    statusRv.setVisibility(View.VISIBLE);

                    adapterWaStatus = new AdapterWaStatus(getContext(), statusList);
                    statusRv.setAdapter(adapterWaStatus);
                }
            }
        }
    }

    public DocumentFile[] getFromSdcard() {
        DocumentFile fromTreeUri = DocumentFile.fromTreeUri(requireContext().getApplicationContext(), Uri.parse(SharedPrefs.getWBTree(getActivity())));
        if (fromTreeUri != null && fromTreeUri.exists() && fromTreeUri.isDirectory() && fromTreeUri.canRead() && fromTreeUri.canWrite()) {
            return fromTreeUri.listFiles();
        }
        return null;
    }

    public String getWhatsupFolder() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory());
        sb.append(File.separator);
        sb.append("Android/media/com.whatsapp.w4b/WhatsApp Business");
        sb.append(File.separator);
        sb.append("Media");
        sb.append(File.separator);
        sb.append(".Statuses");
        return new File(sb.toString()).isDirectory() ? "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses" : "WhatsApp Business%2FMedia%2F.Statuses";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == 10) {
            statusList.clear();
            DocumentFile[] fromSdcard = getFromSdcard();
            for (int i = 0; i < fromSdcard.length - 1; i++) {
                if (!fromSdcard[i].getUri().toString().contains(".nomedia")) {
                    statusList.add(new WaStatus(fromSdcard[i].getUri().toString()));
                }
            }
            adapterWaStatus.notifyDataSetChanged();
        }
        if (requestCode == this.REQUEST_ACTION_OPEN_DOCUMENT_TREE && resultCode == -1) {
            Uri treeUri = data.getData();
            try {
                requireContext().getContentResolver().takePersistableUriPermission(treeUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SharedPrefs.setWBTree(getActivity(), treeUri.toString());
            populateGrid();
        }
    }
}