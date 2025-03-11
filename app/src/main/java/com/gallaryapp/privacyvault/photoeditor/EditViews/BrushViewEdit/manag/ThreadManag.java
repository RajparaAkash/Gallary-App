package com.gallaryapp.privacyvault.photoeditor.EditViews.BrushViewEdit.manag;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class ThreadManag {

    private static ThreadManag instance = null;
    private final HandlerThread apiQueueThread;
    private final Handler mAPIQueueThread;
    private final Handler mBackgroundHandler;
    private final HandlerThread mThread;
    private final Handler mUI_Handler = new Handler(Looper.getMainLooper());

    private ThreadManag() {
        HandlerThread handlerThread = new HandlerThread("backgroundThread");
        this.mThread = handlerThread;
        handlerThread.start();
        this.mBackgroundHandler = new Handler(handlerThread.getLooper());
        HandlerThread handlerThread2 = new HandlerThread("apiQueueThread");
        this.apiQueueThread = handlerThread2;
        handlerThread2.start();
        this.mAPIQueueThread = new Handler(handlerThread2.getLooper());
    }

    public static synchronized ThreadManag getInstance() {
        ThreadManag threadManag;
        synchronized (ThreadManag.class) {
            if (instance == null) {
                instance = new ThreadManag();
            }
            threadManag = instance;
        }
        return threadManag;
    }

    public void postToUIThread(Runnable runnable) {
        this.mUI_Handler.post(runnable);
    }
}
