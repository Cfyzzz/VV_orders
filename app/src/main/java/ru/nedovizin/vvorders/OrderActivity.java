package ru.nedovizin.vvorders;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;

public class OrderActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        DelayAutoCompleteTextView clientTitle = (DelayAutoCompleteTextView) findViewById(R.id.client);
        clientTitle.setThreshold(4);
        clientTitle.setAdapter(new ClientAutoCompleteAdapter(getBaseContext()));
        clientTitle.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar));
        clientTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Contragent client = (Contragent) adapterView.getItemAtPosition(position);
                clientTitle.setText(client.name);
            }
        });
    }
}