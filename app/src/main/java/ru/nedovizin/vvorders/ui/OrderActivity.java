package ru.nedovizin.vvorders.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.nedovizin.vvorders.ClientAutoCompleteAdapter;
import ru.nedovizin.vvorders.DelayAutoCompleteTextView;
import ru.nedovizin.vvorders.R;
import ru.nedovizin.vvorders.models.Contragent;

public class OrderActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        DelayAutoCompleteTextView addressTitle = (DelayAutoCompleteTextView) findViewById(R.id.address);
        DelayAutoCompleteTextView clientTitle = (DelayAutoCompleteTextView) findViewById(R.id.client);

        clientTitle.setThreshold(4);
        clientTitle.setAdapter(new ClientAutoCompleteAdapter(getBaseContext()));
        clientTitle.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar));
        clientTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Contragent client = (Contragent) adapterView.getItemAtPosition(position);
                clientTitle.setText(client.name);
                addressTitle.requestFocus();
            }
        });

        addressTitle.setThreshold(4);
        // TODO - Здесь адаптер нужен для адреса
        addressTitle.setAdapter(new ClientAutoCompleteAdapter(getBaseContext()));
        addressTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}