package ru.nedovizin.vvorders.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import ru.nedovizin.vvorders.database.ClientDbSchema.ClientTable;

public class ClientBaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DTABASE_NAME = "clientBase.db";

    public ClientBaseHelper(Context context) {
        super(context, DTABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ClientTable.NAME + "(" +
                " _id integer primary key AUTOINCREMENT, " +
                ClientTable.Cols.CODE + ", " +
                ClientTable.Cols.NAME +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
