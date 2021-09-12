package ru.nedovizin.vvorders.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import ru.nedovizin.vvorders.models.SettingsConnect;

public class SettingsConnectCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public SettingsConnectCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public SettingsConnect getSettings() {
        String host = getString(getColumnIndex(ClientDbSchema.SettingsTable.Cols.HOST));
        String login = getString(getColumnIndex(ClientDbSchema.SettingsTable.Cols.LOGIN));
        String password = getString(getColumnIndex(ClientDbSchema.SettingsTable.Cols.PASSWORD));

        SettingsConnect settingsConnect = new SettingsConnect(host, login, password);
        return settingsConnect;
    }
}
