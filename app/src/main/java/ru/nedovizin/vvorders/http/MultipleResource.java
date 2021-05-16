package ru.nedovizin.vvorders.http;

import com.google.gson.annotations.SerializedName;
import java.util.List;

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

    public class Address {
        @SerializedName("АдресДоставки")
        public String name;
        @SerializedName("Код")
        public String code;
        @SerializedName("КодКонтрагента")
        public String codeContragent;
    }
}


// https://www.journaldev.com/13639/retrofit-android-example-tutorial
