package ru.nedovizin.vvorders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MenuActivity {

    private Button send_button;
    private List<Contragent> clients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO - Заменить на показ свежих заявок по клиентам
//        for (int i = 1; i < 20; i++) {
//            clients.add("any predprinimatel any predprinimatel any predprinimatel");
//        }
        ClientLab clietnLab = new ClientLab(getBaseContext());
        clients = clietnLab.getClients();
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

    private void addRows(List<Contragent> clients) {
        ListView listViewClients=(ListView)findViewById(R.id.table_clients);
        ClientsListAdapter clientsListAdapter =new ClientsListAdapter(clients);
        listViewClients.setAdapter(clientsListAdapter);
    }
}