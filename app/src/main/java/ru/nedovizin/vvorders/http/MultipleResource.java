package ru.nedovizin.vvorders.http;

import com.google.gson.annotations.SerializedName;
import java.util.List;

import ru.nedovizin.vvorders.models.Address;
import ru.nedovizin.vvorders.models.Contragent;
import ru.nedovizin.vvorders.models.Product;

/** Класс для получения ответов от AS
 *
 */
public class MultipleResource {
    @SerializedName("status")
    public String status;
    @SerializedName("description")
    public String description;
    @SerializedName("answer")
    public Answer answer = null;

    public class Answer {
        @SerializedName("Контрагенты")
        public List<Contragent> mContragents = null;
        @SerializedName("АдресаДоставки")
        public List<Address> mAddresses = null;
        @SerializedName("Номенклатура")
        public List<Product> mProducts = null;
        @SerializedName("Статусы")
        public List<StatusOrder> mStatusOrders = null;
    }

    public class StatusOrder {
        @SerializedName("Код")
        public String code;
        @SerializedName("Статус")
        public String status;
    }
}
