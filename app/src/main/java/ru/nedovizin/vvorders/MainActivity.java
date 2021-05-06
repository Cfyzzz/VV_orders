package ru.nedovizin.vvorders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends MenuActivity {

    private Button send_button;
    private ArrayList<String> clients = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        for (int i = 1; i < 20; i++) {
            clients.add("any predprinimatel any predprinimatel any predprinimatel");
        }
        addRows(clients);

        send_button = (Button) findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addRows(ArrayList<String> line) {
        ListView listViewTest=(ListView)findViewById(R.id.table_clients);
        ClientsListAdapter clientsListAdapter =new ClientsListAdapter(line);
        listViewTest.setAdapter(clientsListAdapter);
    }
}