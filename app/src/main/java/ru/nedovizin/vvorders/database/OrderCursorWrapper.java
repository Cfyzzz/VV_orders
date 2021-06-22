package ru.nedovizin.vvorders.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import ru.nedovizin.vvorders.models.Order;

public class OrderCursorWrapper extends CursorWrapper {
    public OrderCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Order getOrder() {
        String code = getString(getColumnIndex(ClientDbSchema.OrderTable.Cols.CODE));
        String date = getString(getColumnIndex(ClientDbSchema.OrderTable.Cols.DATE));
        String client = getString(getColumnIndex(ClientDbSchema.OrderTable.Cols.CLIENT));

        Order order = new Order();
        order.code = code;
        order.date = date;
        order.client = client;
        return order;
    }
}
