package com.gallaryapp.privacyvault.photoeditor.DataHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBFavourite extends SQLiteOpenHelper {

    public DBFavourite(@Nullable Context context) {
        super(context, "FavouriteDataBase", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String qry="create table favdata(id INTEGER PRIMARY KEY AUTOINCREMENT,imageId TEXT)";
        sqLiteDatabase.execSQL(qry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public Cursor insertData(String imageId) {

        SQLiteDatabase db=getWritableDatabase();

        String qry="insert into favdata(imageId) values ('"+imageId+"')";
        db.execSQL(qry);
            return null;
    }

    public Cursor readdata(){

        SQLiteDatabase db=getReadableDatabase();
        String qry="select * from favdata";
        Cursor cursor=db.rawQuery(qry,null);
        return cursor;
    }

    public void deleteData(String id) {
        SQLiteDatabase db=getWritableDatabase();

        String qry="delete from favdata where imageId='"+id+"'";
        db.execSQL(qry);
    }
}
