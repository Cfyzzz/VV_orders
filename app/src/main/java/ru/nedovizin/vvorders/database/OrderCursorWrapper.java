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
        String address = getString(getColumnIndex(ClientDbSchema.OrderTable.Cols.ADDRESS));
        String status = getString(getColumnIndex(ClientDbSchema.OrderTable.Cols.STATUS));
        String activity = getString(getColumnIndex(ClientDbSchema.OrderTable.Cols.ACTIVITY));

        Order order = new Order();
        order.code = code;
        order.date = date;
        order.client = client;
        order.address = address;
        order.status = status;
        order.activity = activity;
        return order;
    }
}
