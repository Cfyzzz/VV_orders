package ru.nedovizin.vvorders;

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


public class SettingsActivity extends AppCompatActivity {

    public String OK_STATUS = "Ok";

    private Button send_settings_button;
    private TextView connectline_tv;
    private TextView settings_status;
    private EditText login;
    APIInterface apiInterface;

    private View settings_layout;
    private static final String TAG = ".SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        settings_layout = findViewById(R.id.settings_layout);
        connectline_tv = findViewById(R.id.connect_line_settings);
        login = findViewById(R.id.login_settings);
        settings_status = findViewById(R.id.status_settings);

        login.setText("Елена");
        send_settings_button = findViewById(R.id.send_settings_button);
        send_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_api = connectline_tv.getText().toString();
                String word = login.getText().toString();
                apiInterface = APIClient.getClient(url_api).create(APIInterface.class);
                loadClients(word);
            }
        });
    }

    private void loadClients(String userName) {
        /*
         GET List Resources
         **/
        Call<MultipleResource> call = apiInterface.doGetListResources(userName);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                Log.d(TAG, response.code() + "");

                if (response.code() == 200) {

                    MultipleResource resource = response.body();
                    String status = resource.status;
                    String description = resource.description;

                    if (status.equals(OK_STATUS)) {
                        MultipleResource.Answer answer = resource.answer;
                        List<MultipleResource.Contragent> mContragents = resource.answer.contragents;
                        List<MultipleResource.Adress> mAdressess = resource.answer.adresses;

//                        for (MultipleResource.Datum datum : datumList) {
//                            displayResponse += datum.id + " " + datum.name + " " + datum.pantoneValue + " " + datum.year + "\n";
//                        }

                        settings_status.setText(description + " " + Integer.toString(mAdressess.size()));
                    } else {
                        settings_status.setText(description);
                    }
                } else {
                    settings_status.setText("Ошибка соединения");
                }
            }

            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                settings_status.setText("Ошибка соединения");
            }
        });
    }
}

//https://sidstudio.com.ua/sidstudio-blog/%D1%81%D0%B2%D1%8F%D0%B7%D1%8C-android-%D1%81-web-%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F-%D0%BF%D0%BE%D1%81%D1%80%D0%B5%D0%B4%D1%81%D1%82%D0%B2%D0%BE%D0%BC-rest-%D0%B0%D1%80%D1%85%D0%B8%D1%82%D0%B5%D0%BA%D1%82%D1%83%D1%80%D1%8B-%D1%87%D0%B0%D1%81%D1%82%D1%8C-3-%D1%80%D0%B5%D0%B0%D0%BB%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D1%8F-rest-%D0%B2-android
