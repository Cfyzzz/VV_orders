package ru.nedovizin.vvorders.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ru.nedovizin.vvorders.ProductItem;

public class Order {
    @SerializedName("Код")
    public String code;
    @SerializedName("Дата")
    public String date;
    @SerializedName("Клиент")
    public String client;
    @SerializedName("Адрес")
    public String address;
    @SerializedName("Активность")
    public String activity;
    @SerializedName("Выделено")
    public String selected;

    @SerializedName("Номенклатура")
    public List<NomenclaturaItem> nomenclaturaItems = null;

    public class NomenclaturaItem {
        @SerializedName("Номенклатура")
        public String name;
        @SerializedName("КодНоменклатуры")
        public String code;
        @SerializedName("Количество")
        public String quantity;
    }

    public void setProducts(List<ProductItem> products) {
        nomenclaturaItems = new ArrayList<>();
        for (ProductItem pItem : products) {
            NomenclaturaItem nItem = new NomenclaturaItem();
            nItem.name = pItem.product.name;
            nItem.code = pItem.product.code;
            nItem.quantity = pItem.quantity;
            nomenclaturaItems.add(nItem);
        }
    }
}
