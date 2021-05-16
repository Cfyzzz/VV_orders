package ru.nedovizin.vvorders.http;

import com.google.gson.annotations.SerializedName;
import java.util.List;

import ru.nedovizin.vvorders.models.Address;
import ru.nedovizin.vvorders.models.Contragent;

public class MultipleResource {
    @SerializedName("status")
    public String status;
    @SerializedName("description")
    public String description;
    @SerializedName("answer")
    public Answer answer = null;

    public class Answer {
        @SerializedName("Контрагенты")
        public List<Contragent> contragents = null;
        @SerializedName("АдресаДоставки")
        public List<Address> mAddresses = null;
    }
}


// https://www.journaldev.com/13639/retrofit-android-example-tutorial
