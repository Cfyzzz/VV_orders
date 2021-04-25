package ru.nedovizin.vvorders;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;

public interface IRestUserAPI {
    @GET("/v1/users/")
    void getUserList(retrofit.Callback<List<User>> cb);
    @GET("/{name}")
    void getUserById(@Path("name") String userName, retrofit.Callback<User> cb);

}
