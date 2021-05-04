package ru.nedovizin.vvorders;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;


public class SettingsActivity extends AppCompatActivity {

    private Button send_settings_button;
    private TextView connectline_tv;
    private EditText login;

    private IRestUserAPI mUserAPI = null;
    private RestAdapter mRestAdapter = null;
    private View settings_layout;
    private static final String TAG = ".SettingsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings_layout = findViewById(R.id.settings_layout);
        connectline_tv = findViewById(R.id.connect_line_settings);
        login = findViewById(R.id.login_settings);

        send_settings_button = findViewById(R.id.send_settings_button);
        send_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_api = connectline_tv.getText().toString();
                mRestAdapter = new RestAdapter.Builder().setEndpoint(url_api).build();
                mUserAPI = mRestAdapter.create(IRestUserAPI.class);
                String word = login.getText().toString();
                loadUserFromRestById(word);
            }
        });
    }

    void loadUserFromRestById(final String userName) {
        if (mUserAPI == null) {
            Snackbar.make(settings_layout, "Cant load user", LENGTH_SHORT).show();
            return;
        }
        mUserAPI.getUserById(userName, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                viewUser(user);
                Snackbar.make(settings_layout, "Status: " + user.getStatus(), LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.toString());
                Snackbar.make(settings_layout, "Load user with id: " + userName + " error: "
                        + error.toString(), LENGTH_SHORT).show();
            }
        });
    }

    void viewUser(User user) {
        TextView status_settings = (TextView) findViewById(R.id.status_settings);
        status_settings.setText(user.getDescription());
    }
}

//https://sidstudio.com.ua/sidstudio-blog/%D1%81%D0%B2%D1%8F%D0%B7%D1%8C-android-%D1%81-web-%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F-%D0%BF%D0%BE%D1%81%D1%80%D0%B5%D0%B4%D1%81%D1%82%D0%B2%D0%BE%D0%BC-rest-%D0%B0%D1%80%D1%85%D0%B8%D1%82%D0%B5%D0%BA%D1%82%D1%83%D1%80%D1%8B-%D1%87%D0%B0%D1%81%D1%82%D1%8C-3-%D1%80%D0%B5%D0%B0%D0%BB%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D1%8F-rest-%D0%B2-android
