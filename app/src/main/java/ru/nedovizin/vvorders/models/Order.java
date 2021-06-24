package ru.nedovizin.vvorders.models;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("Наименование")
    public String name;
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
}
