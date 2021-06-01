package ru.nedovizin.vvorders.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import ru.nedovizin.vvorders.models.Product;

public class ProductCursorWrapper extends CursorWrapper {
    public ProductCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Product getProduct() {
        String code = getString(getColumnIndex(ClientDbSchema.ProductTable.Cols.CODE));
        String name = getString(getColumnIndex(ClientDbSchema.ProductTable.Cols.NAME));
        String weight = getString(getColumnIndex(ClientDbSchema.ProductTable.Cols.WEIGHT));

        Product product = new Product();
        product.name = name;
        product.code = code;
        product.weight = weight;
        return product;
    }
}
