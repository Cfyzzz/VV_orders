package ru.nedovizin.vvorders.http;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.nedovizin.vvorders.models.Order;


public interface APIInterface {
    @GET("/OrdersAS/hs/db/update_clients/{name}")
    Call<MultipleResource> doGetListClients(@Path("name") String name);

    @GET("/OrdersAS/hs/db/update_products/{name}")
    Call<MultipleResource> doGetListProducts(@Path("name") String name);

    @POST("/OrdersAS/orders/{name}")
    Call<List<Order>> sendOrders(@Path("name") String name, @Body List<Order> orders);
}
