package ru.nedovizin.vvorders.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.nedovizin.vvorders.database.ClientBaseHelper;
import ru.nedovizin.vvorders.database.ClientCursorWrapper;
import ru.nedovizin.vvorders.database.ClientDbSchema.ClientTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.ClientTable.Cols;

public class ClientLab {

    private static ClientLab sClientLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ClientLab get(Context context) {
        if (sClientLab == null) {
            sClientLab = new ClientLab(context);
        }
        return sClientLab;
    }

    public ClientLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ClientBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addClient(Contragent c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(ClientTable.NAME, null, values);
    }

    public List<Contragent> getClientsByLikeName(String word) {
        List<Contragent> clients = new ArrayList<>();
        try (ClientCursorWrapper cursor = queryClients(Cols.NAME + " LIKE \'" + word + "%\'", null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                clients.add(cursor.getClient());
                cursor.moveToNext();
            }
        }
        return clients;
    }

    public List<Contragent> getClients() {
        List<Contragent> client = new ArrayList<>();
        try (ClientCursorWrapper cursor = queryClients(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                client.add(cursor.getClient());
                cursor.moveToNext();
            }
        }
        return client;
    }

    public Contragent getClient(String code) {
        try (ClientCursorWrapper cursor = queryClients(Cols.CODE + " = ?", new String[]{code})) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getClient();
        }
    }

    public void updateClient(Contragent client) {
        String code = client.code;
        ContentValues values = getContentValues(client);
        mDatabase.update(ClientTable.NAME, values, Cols.CODE + " = ?", new String[]{code});
    }

    private ClientCursorWrapper queryClients(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ClientTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new ClientCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Contragent client) {
        ContentValues values = new ContentValues();
        values.put(Cols.NAME, client.name);
        values.put(Cols.CODE, client.code);
        return values;
    }
}
