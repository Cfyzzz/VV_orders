package ru.nedovizin.vvorders.http;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.nedovizin.vvorders.models.OrdersForSend;


public interface APIInterface {
    @GET("/OrdersAS/hs/db/update_clients/{name}")
    Call<MultipleResource> doGetListClients(@Path("name") String name, @Header("Authorization") String auth);

    @GET("/OrdersAS/hs/db/update_products/{name}")
    Call<MultipleResource> doGetListProducts(@Path("name") String name, @Header("Authorization") String auth);

    @POST("/OrdersAS/hs/db/send_orders")
    Call<MultipleResource> sendOrders(@Body OrdersForSend orders, @Header("Authorization") String auth);

    @GET("/OrdersAS/hs/db/get_status/{date}")
    Call<MultipleResource> doGetStatus(@Path("date") String date, @Header("Authorization") String auth);

    @DELETE("/OrdersAS/hs/db/delete_order/{code}")
    Call<MultipleResource> sendDeleteOrder(@Path("code") String code, @Header("Authorization") String auth);

    @GET("/OrdersAS/hs/db/cancel_delete_order/{code}")
    Call<MultipleResource> sendCancelDeleteOrder(@Path("code") String code, @Header("Authorization") String auth);
}
