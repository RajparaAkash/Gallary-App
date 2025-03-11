package com.yalantis.ucrop;

import androidx.annotation.NonNull;

import okhttp3.OkHttpClient;

public class UCropInitializer {

    public UCropInitializer setOkHttpClient(@NonNull OkHttpClient client) {
        OkHttpClientStore.INSTANCE.setClient(client);
        return this;
    }

}
