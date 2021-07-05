package ru.nedovizin.vvorders.http;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import ru.nedovizin.vvorders.models.Order;


public interface APIInterface {
    @GET("/OrdersAS/hs/db/update_clients/{name}")
    Call<MultipleResource> doGetListClients(@Path("name") String name);

    @GET("/OrdersAS/hs/db/update_products/{name}")
    Call<MultipleResource> doGetListProducts(@Path("name") String name);

    @POST("/OrdersAS/odata/standard.odata/Document_Заявка?$format=json")
    Call<Order> sendOrder(@Body Order order);

    @POST("/OrdersAS/odata/standard.odata/Document_Заявка?$format=json")
    Call<Order.NomenclaturaItem> sendProductsByOrder(@Body Order.NomenclaturaItem products);
}
