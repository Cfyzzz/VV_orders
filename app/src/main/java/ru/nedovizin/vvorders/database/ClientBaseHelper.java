package ru.nedovizin.vvorders.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.nedovizin.vvorders.database.ClientDbSchema.AddressTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.ClientTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.ProductTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.OrderTable;

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
                ClientTable.Cols.ACTIVITY + ", " +
                " unique(code) ON CONFLICT replace)"
        );
        db.execSQL("create table " + AddressTable.NAME + "(" +
                AddressTable.Cols.CODE + ", " +
                AddressTable.Cols.NAME + ", " +
                AddressTable.Cols.ACTIVITY + ", " +
                "unique(code, name) ON CONFLICT replace)"
        );
        db.execSQL("create table " + ProductTable.NAME + "(" +
                ProductTable.Cols.CODE + ", " +
                ProductTable.Cols.NAME + ", " +
                ProductTable.Cols.WEIGHT + ", " +
                ProductTable.Cols.ACTIVITY + ", " +
                " unique(code) ON CONFLICT replace)"
        );
        db.execSQL("create table " + OrderTable.Cols.Products.NAME + "(" +
                OrderTable.Cols.Products.CODE + ", " +
                OrderTable.Cols.Products.PRODUCT + ", " +
                OrderTable.Cols.Products.QUANTITY +
                ")"
        );
        db.execSQL("create table " + OrderTable.NAME + "(" +
                OrderTable.Cols.CODE + ", " +
                OrderTable.Cols.DATE + ", " +
                OrderTable.Cols.CLIENT + ", " +
                OrderTable.Cols.ACTIVITY + ", " +
                " unique(code) ON CONFLICT replace)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
