package com.yalantis.ucrop;

import androidx.annotation.NonNull;

import okhttp3.OkHttpClient;

public class OkHttpClientStore {

    private OkHttpClientStore() {}

    public final static OkHttpClientStore INSTANCE = new OkHttpClientStore();

    private OkHttpClient client;

    @NonNull
    public OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    void setClient(@NonNull OkHttpClient client) {
        this.client = client;
    }
}
