package ru.nedovizin.vvorders.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.List;

import ru.nedovizin.vvorders.ClientsListAdapter;
import ru.nedovizin.vvorders.R;
import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Contragent;

public class ClientMainFragment extends Fragment {
    private Button send_button;
    private List<Contragent> clients;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list, container, false);

        // TODO - Заменить на показ свежих заявок по клиентам
        ClientLab clietnLab = ClientLab.get(getContext());
        clients = clietnLab.getClients();
        addRows(clients, view);

        send_button = (Button) view.findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OrderActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void addRows(List<Contragent> clients, View v) {
        ListView listViewClients = (ListView) v.findViewById(R.id.table_clients);
        ClientsListAdapter clientsListAdapter = new ClientsListAdapter(clients);
        listViewClients.setAdapter(clientsListAdapter);
    }
}