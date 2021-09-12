package ru.nedovizin.vvorders.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.nedovizin.vvorders.R;
import ru.nedovizin.vvorders.http.APIClient;
import ru.nedovizin.vvorders.http.APIInterface;
import ru.nedovizin.vvorders.http.MultipleResource;
import ru.nedovizin.vvorders.models.Address;
import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Contragent;
import ru.nedovizin.vvorders.models.Product;
import ru.nedovizin.vvorders.models.SettingsConnect;


public class SettingsActivity extends AppCompatActivity {

    public String OK_STATUS = "Ok";

    private Button mSaveSettingsButton;
    private Button mUpdateClientsButton;
    private Button mUpdateProductsButton;
    private TextView mConnectlineTv;
    private TextView mSettingsStatus;
    private EditText mLogin;
    private EditText mPasswrd;
    private SettingsConnect mSettingsConnect;
    private APIInterface apiInterface;
    private static final String TAG = ".SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mConnectlineTv = findViewById(R.id.connect_line_settings);
        mLogin = findViewById(R.id.login_settings);
        mPasswrd = findViewById(R.id.passwrd_settings);
        mSettingsStatus = findViewById(R.id.status_settings);

        // Установить настройки из базы
        mSettingsConnect = ClientLab.get(getBaseContext()).getSettingsConnect();
        mConnectlineTv.setText(mSettingsConnect.getHost());
        mLogin.setText(mSettingsConnect.getLogin());
        mPasswrd.setText(mSettingsConnect.getPassword());

        mUpdateClientsButton = findViewById(R.id.update_clients);
        mUpdateClientsButton.setOnClickListener(v -> {
            // Обновить клиентов из Базы
            mSettingsStatus.setText("");
            String url_api = mConnectlineTv.getText().toString();
            apiInterface = APIClient.getData(url_api).create(APIInterface.class);
            loadClients();
        });

        mUpdateProductsButton = findViewById(R.id.update_products);
        mUpdateProductsButton.setOnClickListener(v -> {
            // Обновить список продуктов из базы
            mSettingsStatus.setText("");
            String url_api = mConnectlineTv.getText().toString();
            apiInterface = APIClient.getData(url_api).create(APIInterface.class);
            loadProducts();
        });

        mSaveSettingsButton = findViewById(R.id.save_settings_button);
        mSaveSettingsButton.setOnClickListener(v -> {
            // Сохранить настройки в базе
            String host = mConnectlineTv.getText().toString();
            String login = mLogin.getText().toString();
            String password = mPasswrd.getText().toString();
            mSettingsConnect = new SettingsConnect(host, login, password);

            ClientLab.get(getBaseContext()).setSettingsConnect(mSettingsConnect);
            mSettingsStatus.setText("Настройки сохранены");
        });
    }

    /** Загрузить с AS список продуктов
     *
     */
    private void loadProducts() {
        Call<MultipleResource> call = apiInterface.doGetListProducts(mSettingsConnect.getAuthBase64());
        loadData(call);
    }

    /** Загрузить с AS список клиентов данного менеджера
     *
     */
    private void loadClients() {
        Call<MultipleResource> call = apiInterface.doGetListClients(mSettingsConnect.getAuthBase64());
        loadData(call);
    }

    /** Загрузить данные с AS: клиенты, адреса, продукты
     *
     * @param call Объект Call класса
     */
    private void loadData(Call<MultipleResource> call) {
        call.enqueue(new Callback<MultipleResource>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (response.code() == 200) {

                    MultipleResource resource = response.body();
                    String status = resource.status;
                    String description = resource.description;

                    if (status.equals(OK_STATUS)) {
                        MultipleResource.Answer answer = resource.answer;
                        List<Contragent> mContragents = answer.mContragents;
                        List<Address> mAdresses = answer.mAddresses;
                        List<Product> mProduct = answer.mProducts;

                        // Пытаемся записать в базу принятые данные
                        ClientLab clientLab = ClientLab.get(getBaseContext());

                        if (mContragents != null) {
                            clientLab.updateActivityToFalseAllClients();
                            for (Contragent contragent : mContragents) {
                                clientLab.addClient(contragent);
                            }
                        }

                        if (mAdresses != null) {
                            clientLab.updateActivityToFalseAllAddresses();
                            for (Address address : mAdresses) {
                                clientLab.addAddress(address);
                            }
                        }

                        if (mProduct != null) {
                            clientLab.updateActivityToFalseAllProducts();
                            for (Product product : mProduct) {
                                clientLab.addProduct(product);
                            }
                        }

                        mSettingsStatus.setText(description + " It's saved...");
                    } else {
                        mSettingsStatus.setText(description + " It's not saved!");
                    }
                } else {
                    mSettingsStatus.setText("Ошибка соединения (не 200)");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                mSettingsStatus.setText(R.string.message_server_is_not_recieve + TAG);
            }
        });
    }
}
