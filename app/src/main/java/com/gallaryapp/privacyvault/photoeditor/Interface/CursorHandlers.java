package com.gallaryapp.privacyvault.photoeditor.Interface;

import android.database.Cursor;

public interface CursorHandlers<T> {
    T handle(Cursor cursor);
}
