package com.gallaryapp.privacyvault.photoeditor.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Model.WaStatus;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.List;

public class AdapterWaStatus extends RecyclerView.Adapter<AdapterWaStatus.ViewHolder> {

    Context context;
    List<WaStatus> arrayList;

    public AdapterWaStatus(Context context, List<WaStatus> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wa_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaStatus waStatus = arrayList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(waStatus.getFilePath())
                .into(holder.status_mainImg);

        holder.status_playImg.setVisibility(UtilApp.getBack(waStatus.getFilePath(),
                "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)").isEmpty() ? View.GONE : View.VISIBLE);

        holder.statusDownloadImg.setOnClickListener(v -> {
            new downloadAll(waStatus).execute(new Void[0]);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class downloadAll extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;

        int success = -1;
        WaStatus waStatus;

        public downloadAll(WaStatus waStatus) {
            this.waStatus = waStatus;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context, R.style.ProgressDialogStyle);
            progressDialog.setMessage(context.getResources().getString(R.string.str_166));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(0);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        public Void doInBackground(Void... voids) {
            if (DocumentFile.fromSingleUri(context, Uri.parse(waStatus.getFilePath())).exists()) {
                if (UtilApp.download(context, waStatus.getFilePath())) {
                    this.success = 1;
                } else {
                    this.success = 0;
                }
            } else {
                this.success = 0;
            }
            return null;
        }

        @Override
        public void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();

            int i = this.success;
            if (i == 0) {
                Toast.makeText(context, "Could not save some files", Toast.LENGTH_SHORT).show();
            } else if (i == 1) {
                Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT).show();
                UtilApp.isAlbumsFragChange = true;
                UtilApp.isAllMediaFragChange = true;
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView status_mainImg;
        ImageView status_playImg;
        ImageView statusDownloadImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            status_mainImg = itemView.findViewById(R.id.status_mainImg);
            status_playImg = itemView.findViewById(R.id.status_playImg);
            statusDownloadImg = itemView.findViewById(R.id.status_downalodImg);
        }
    }
}
