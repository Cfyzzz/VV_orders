package ru.nedovizin.vvorders.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.nedovizin.vvorders.database.ClientDbSchema.AddressTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.ClientTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.ProductTable;

public class ClientBaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DTABASE_NAME = "clientBase.db";

    public ClientBaseHelper(Context context) {
        super(context, DTABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ClientTable.NAME + "(" +
                ClientTable.Cols.CODE + ", " +
                ClientTable.Cols.NAME + ", " +
                " unique(code) ON CONFLICT ignore)"
        );
        db.execSQL("create table " + AddressTable.NAME + "(" +
                AddressTable.Cols.CODE + ", " +
                AddressTable.Cols.NAME + ", " +
                "unique(code, name) ON CONFLICT replace)"
        );
        db.execSQL("create table " + ProductTable.NAME + "(" +
                ProductTable.Cols.CODE + ", " +
                ProductTable.Cols.NAME + ", " +
                ProductTable.Cols.WEIGHT + ", " +
                " unique(code) ON CONFLICT ignore)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
