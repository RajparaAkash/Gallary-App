package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.content.ContentResolver;
import android.database.Cursor;

import com.gallaryapp.privacyvault.photoeditor.Interface.CursorHandlers;
import com.gallaryapp.privacyvault.photoeditor.Model.Querys;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class UtilQuery {

    public static <T> Observable<T> query(final Querys q, final ContentResolver cr, final CursorHandlers<T> ch) {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter observableEmitter) throws Exception {
                UtilQuery.getQuery(q, cr, ch, observableEmitter);
            }
        });
    }
    
    public static void getQuery(Querys q, ContentResolver cr, CursorHandlers ch, ObservableEmitter subscriber) throws Exception {
        Cursor cursor = null;
        try {
            try {
                cursor = q.getCursor(cr);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        subscriber.onNext(ch.handle(cursor));
                    }
                }
                subscriber.onComplete();
                if (cursor == null) {
                    return;
                }
            } catch (Exception err) {
                subscriber.onError(err);
                if (cursor == null) {
                    return;
                }
            }
            cursor.close();
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }
}
