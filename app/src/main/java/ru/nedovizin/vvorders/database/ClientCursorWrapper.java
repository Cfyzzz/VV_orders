package ru.nedovizin.vvorders.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import ru.nedovizin.vvorders.models.Contragent;

public class ClientCursorWrapper extends CursorWrapper {
    public ClientCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Contragent getClient() {
        String code = getString(getColumnIndex(ClientDbSchema.ClientTable.Cols.CODE));
        String name = getString(getColumnIndex(ClientDbSchema.ClientTable.Cols.NAME));

        Contragent client = new Contragent();
        client.name = name;
        client.code = code;
        return client;
    }
}
