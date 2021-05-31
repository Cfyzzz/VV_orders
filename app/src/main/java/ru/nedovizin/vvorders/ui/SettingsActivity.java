package ru.nedovizin.vvorders.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.nedovizin.vvorders.http.APIClient;
import ru.nedovizin.vvorders.http.APIInterface;
import ru.nedovizin.vvorders.R;
import ru.nedovizin.vvorders.http.MultipleResource;
import ru.nedovizin.vvorders.models.Address;
import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Contragent;
import ru.nedovizin.vvorders.models.Product;


public class SettingsActivity extends AppCompatActivity {

    public String OK_STATUS = "Ok";

    private Button mSendSettingsButton;
    private Button mUpdateCleintsButton;
    private Button mUpdateProductsButton;
    private TextView mConnectlineTv;
    private TextView mSettingsStatus;
    private EditText login;
    APIInterface apiInterface;

    private View mSettingsLayout;
    private static final String TAG = ".SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mSettingsLayout = findViewById(R.id.settings_layout);
        mConnectlineTv = findViewById(R.id.connect_line_settings);
        login = findViewById(R.id.login_settings);
        mSettingsStatus = findViewById(R.id.status_settings);

        // TODO - убрать после отладки
        login.setText("Елена");

        mUpdateCleintsButton = findViewById(R.id.update_clients);
        mUpdateCleintsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSettingsStatus.setText("");
                String url_api = mConnectlineTv.getText().toString();
                String userName = login.getText().toString();
                apiInterface = APIClient.getData(url_api).create(APIInterface.class);
                loadClients(userName);
            }
        });

        mUpdateProductsButton = findViewById(R.id.update_products);
        mUpdateProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSettingsStatus.setText("");
                String url_api = mConnectlineTv.getText().toString();
                String userName = login.getText().toString();
                apiInterface = APIClient.getData(url_api).create(APIInterface.class);
                loadProducts(userName);
            }
        });

        // TODO - Тестовая кнопка
        mSendSettingsButton = findViewById(R.id.send_settings_button);
        mSendSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - это кнопка для тестов
                // Открываем список клиентов в базе
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadProducts(String userName) {
        Call<MultipleResource> call = apiInterface.doGetListProducts(userName);
        loadData(call);
    }

    private void loadClients(String userName) {
        Call<MultipleResource> call = apiInterface.doGetListClients(userName);
        loadData(call);
    }

    private void loadData(Call<MultipleResource> call) {
        /*
         GET List Resources
         **/
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
//              // TODO - Нужно убрать response.body(), чтобы не сыпать в логи тело ответа
                Log.d(TAG, response.code() + " " + response.body());

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
                        ClientLab clietnLab = ClientLab.get(getBaseContext());

                        if (mContragents != null) {
                            for (Contragent contragent : mContragents) {
                                clietnLab.addClient(contragent);
                            }
                        }

                        if (mAdresses != null) {
                            for (Address address : mAdresses) {
                                clietnLab.addAddress(address);
                            }
                        }

                        if (mProduct != null) {
                            for (Product product : mProduct) {
                                clietnLab.addProduct(product);
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

            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                mSettingsStatus.setText("Ошибка соединения (Failure): ");
            }
        });
    }
}

//https://sidstudio.com.ua/sidstudio-blog/%D1%81%D0%B2%D1%8F%D0%B7%D1%8C-android-%D1%81-web-%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F-%D0%BF%D0%BE%D1%81%D1%80%D0%B5%D0%B4%D1%81%D1%82%D0%B2%D0%BE%D0%BC-rest-%D0%B0%D1%80%D1%85%D0%B8%D1%82%D0%B5%D0%BA%D1%82%D1%83%D1%80%D1%8B-%D1%87%D0%B0%D1%81%D1%82%D1%8C-3-%D1%80%D0%B5%D0%B0%D0%BB%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D1%8F-rest-%D0%B2-android
