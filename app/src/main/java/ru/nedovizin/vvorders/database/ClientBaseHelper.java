package ru.nedovizin.vvorders.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.nedovizin.vvorders.database.ClientDbSchema.AddressTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.ClientTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.ProductTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.OrderTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.SettingsTable;

/** Создание БД
 *
 */
public class ClientBaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "clientBase2.db";

    public ClientBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ClientTable.NAME + "(" +
                ClientTable.Cols.CODE + ", " +
                ClientTable.Cols.NAME + ", " +
                ClientTable.Cols.ACTIVITY + ", " +
                " unique(" + ClientTable.Cols.CODE + ") ON CONFLICT replace)"
        );
        db.execSQL("create table " + AddressTable.NAME + "(" +
                AddressTable.Cols.CODE + ", " +
                AddressTable.Cols.NAME + ", " +
                AddressTable.Cols.ACTIVITY + ", " +
                "unique(" + AddressTable.Cols.CODE +", " + AddressTable.Cols.NAME + ") ON CONFLICT replace)"
        );
        db.execSQL("create table " + ProductTable.NAME + "(" +
                ProductTable.Cols.CODE + ", " +
                ProductTable.Cols.NAME + ", " +
                ProductTable.Cols.WEIGHT + ", " +
                ProductTable.Cols.ACTIVITY + ", " +
                " unique(" + ProductTable.Cols.CODE + ") ON CONFLICT replace)"
        );
        db.execSQL("create table " + OrderTable.Cols.Products.NAME + "(" +
                OrderTable.Cols.Products.pCols.CODE + ", " +
                OrderTable.Cols.Products.pCols.PRODUCT + ", " +
                OrderTable.Cols.Products.pCols.QUANTITY +
                ")"
        );
        db.execSQL("create table " + OrderTable.NAME + "(" +
                OrderTable.Cols.CODE + ", " +
                OrderTable.Cols.DATE + ", " +
                OrderTable.Cols.CLIENT + ", " +
                OrderTable.Cols.ADDRESS + ", " +
                OrderTable.Cols.ACTIVITY + ", " +
                OrderTable.Cols.STATUS + ", " +
                " unique(" + OrderTable.Cols.CODE + ") ON CONFLICT replace)"
        );
        db.execSQL("create table " + SettingsTable.NAME + "(" +
                SettingsTable.Cols.HOST + ", " +
                SettingsTable.Cols.LOGIN + ", " +
                SettingsTable.Cols.PASSWORD + ", " +
                " unique(" + SettingsTable.Cols.LOGIN + ") ON CONFLICT replace)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
