package ru.nedovizin.vvorders.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Описание заявок для отправки на сервер
 *
 */
public class OrdersForSend {
    @SerializedName("Заявки")
    public List<Order> mOrders = null;

    public OrdersForSend(List<Order> orders) {
        mOrders = orders;
    }
}
