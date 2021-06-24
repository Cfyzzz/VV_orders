package ru.nedovizin.vvorders;

import ru.nedovizin.vvorders.models.Product;

public class ProductItem {
    public Product product;
    public String quantity;

    public ProductItem() {
        product = null;
        quantity = "0";
    }

    public ProductItem(Product product) {
        this.product = product;
        quantity = "0";
    }

    public ProductItem(Product product, String quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}
