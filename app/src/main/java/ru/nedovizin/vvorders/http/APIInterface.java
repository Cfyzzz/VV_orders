package ru.nedovizin.vvorders.http;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface APIInterface {
    @GET("/OrdersAS/hs/db/update_clients/{name}")
    Call<MultipleResource> doGetListClients(@Path("name") String name);

    @GET("/OrdersAS/hs/db/update_products/{name}")
    Call<MultipleResource> doGetListProducts(@Path("name") String name);

//    @POST("/api/users")
//    Call<User> createUser(@Body User user);
}
