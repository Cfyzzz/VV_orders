package ru.nedovizin.vvorders.models;

import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("АдресДоставки")
    public String name;
    @SerializedName("Код")
    public String code;
    @SerializedName("Активность")
    public String activity;
}
