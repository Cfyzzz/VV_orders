package ru.nedovizin.vvorders.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ru.nedovizin.vvorders.ProductItem;

public class Order {
    @SerializedName("Код")
    public String code;
    @SerializedName("Date")
    public String date;
    @SerializedName("Клиент")
    public String client;
    @SerializedName("Адрес")
    public String address;
    @SerializedName("Активность")
    public String activity;
    @SerializedName("Выделено")
    public String selected;
    @SerializedName("Статус")
    public String status;

    @SerializedName("Товары@odata.type")
    public String nomenclaturaProperty = "Collection(StandardODATA.Document_Заявка_Товары_RowType)";

    @SerializedName("Товары")
    public List<NomenclaturaItem> nomenclaturaItems = null;

    public class NomenclaturaItem {
        @SerializedName("Номенклатура")
        public String name;
        @SerializedName("LineNumber")
        public String lineNumber;
        @SerializedName("Количество")
        public String quantity;
    }

    public void setProducts(List<ProductItem> products) {
        nomenclaturaItems = new ArrayList<>();
        int i = 1;
        for (ProductItem pItem : products) {
            NomenclaturaItem nItem = new NomenclaturaItem();
            nItem.name = pItem.product.name;
            nItem.lineNumber = Integer.toString(i++);
            nItem.quantity = pItem.quantity;
            nomenclaturaItems.add(nItem);
        }
    }
}
