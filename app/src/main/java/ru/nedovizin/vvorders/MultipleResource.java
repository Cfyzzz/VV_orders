package ru.nedovizin.vvorders;

import com.google.gson.annotations.SerializedName;
import java.util.List;

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
        public List<Adress> adresses = null;
    }

    public class Adress {
        @SerializedName("АдресДоставки")
        public String name;
        @SerializedName("Код")
        public String code;
    }
}


// https://www.journaldev.com/13639/retrofit-android-example-tutorial
