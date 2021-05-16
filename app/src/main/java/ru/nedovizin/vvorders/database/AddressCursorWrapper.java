package ru.nedovizin.vvorders.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import ru.nedovizin.vvorders.models.Address;

public class AddressCursorWrapper extends CursorWrapper {
    public AddressCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Address getAddress() {
        String code = getString(getColumnIndex(ClientDbSchema.AddressTable.Cols.CODE));
        String name = getString(getColumnIndex(ClientDbSchema.AddressTable.Cols.NAME));

        Address address = new Address();
        address.name = name;
        address.code = code;
        return address;
    }
}
