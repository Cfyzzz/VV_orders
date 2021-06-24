package ru.nedovizin.vvorders.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import ru.nedovizin.vvorders.ProductItem;
import ru.nedovizin.vvorders.models.Product;

public class ProductItemCursorWrapper extends CursorWrapper {
    public ProductItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public ProductItem getProductItem() {
        String name = getString(getColumnIndex(ClientDbSchema.OrderTable.Cols.Products.PRODUCT));
        String quantity = getString(getColumnIndex(ClientDbSchema.OrderTable.Cols.Products.QUANTITY));

        ProductItem productItem = new ProductItem();
        Product product = new Product();
        product.name = name;
        productItem.product = product;
        productItem.quantity = quantity;
        return productItem;
    }
}
