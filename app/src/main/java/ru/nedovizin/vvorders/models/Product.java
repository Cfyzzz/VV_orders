package ru.nedovizin.vvorders.models;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("Наименование")
    public String name;
    @SerializedName("Код")
    public String code;
    @SerializedName("Вес")
    public String weight;
    @SerializedName("Активность")
    public String activity;
}
