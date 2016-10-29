package com.first.app.barcodescanner.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Waheed Manii on 12/10/2016.
 */

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context) {
        super(context , Data.DATABASE_NAME , null , Data.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL = "CREATE TABLE "+Data.TABLE_NAME+" ("+Data.KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                Data.KEY_RESULT+" TEXT )";

        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String SQL = "DROP TABLE "+Data.TABLE_NAME;

        db.execSQL(SQL);
        onCreate(db);

    }
}
