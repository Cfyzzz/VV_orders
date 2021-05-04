package ru.nedovizin.vvorders;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface APIInterface {
    @GET("/OrdersAS/hs/db/update_clients/{name}")
    Call<MultipleResource> doGetListResources(@Path("name") String name);

//    @POST("/api/users")
//    Call<User> createUser(@Body User user);
}
