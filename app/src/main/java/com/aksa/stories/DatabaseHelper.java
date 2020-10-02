package com.aksa.stories;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "db.sqlite";
    public static final String DB_LOCATION = "/data/data/com.aksa.stories/databases/";
    private SQLiteDatabase mDatabase;
    private Context mContext;


    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void openDatabase() {
        String dbPath = mContext.getDatabasePath(DB_NAME).getPath();
        if (mDatabase != null && mDatabase.isOpen()) {
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    public void closeDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    /**public void update() {
        mDatabase.execSQL("UPDATE tbl_subjects SET content = REPLACE(content, 'hmclub', 'muskaka')");
    }**/


    public ArrayList<ChapterModel> getListChapters() {
        ChapterModel chapter = null;
        ArrayList<ChapterModel> chapterList = new ArrayList<>();
        openDatabase();
        //update();

        //Cursor cursor = mDatabase.rawQuery("SELECT * FROM story_table", null);
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM items", null);
        cursor.moveToFirst();


        while (!cursor.isAfterLast()) {

            String [] split_content = cursor.getString(3).split("<hr>");
            String new_content = split_content[0] ;
            chapter = new ChapterModel(cursor.getInt(0), cursor.getString(2), new_content);
            //chapter = new ChapterModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2));

            chapterList.add(chapter);

            //Log.wtf("WTF", String.valueOf(cursor.getString(2).length()));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();

        return chapterList;
    }
}
