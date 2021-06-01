package ru.nedovizin.vvorders.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.nedovizin.vvorders.R;
import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Contragent;

public class ClientMainFragment extends Fragment {
    private RecyclerView mClientRecyclerView;
    private ClientsListAdapter mAdapter;

    private Button send_button;
    private List<Contragent> clients;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list, container, false);
        mClientRecyclerView = view.findViewById(R.id.table_clients);
        mClientRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // TODO - Заменить на показ свежих заявок по клиентам
         ClientLab clietnLab = ClientLab.get(getContext());
        clients = clietnLab.getClients();
        mAdapter = new ClientsListAdapter(clients);
        mClientRecyclerView.setAdapter(mAdapter);

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

    private class ClientHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // будет заполнять ваш макет
        private TextView mIsUpdated;
        private TextView mNumberClient;
        private TextView mNameClient;
        private Contragent mClient;

        public ClientHolder(View view) {
            super(view);

            mIsUpdated = itemView.findViewById(R.id.col1);
            mNumberClient = itemView.findViewById(R.id.col2);
            mNameClient = itemView.findViewById(R.id.col3);
            itemView.setOnClickListener(this);
        }

        public void bind(Contragent client, int position) {
            mClient = client;
            mIsUpdated.setText((position%2==0)?"V":" ");
            mNumberClient.setText(Integer.toString(position));
            mNameClient.setText(client.name);
        }

        @Override
        public void onClick(View v) {
            // TODO - Реакция на клик по заявке в списке заявок
            Toast.makeText(getActivity(),
                    mClient.name + " clicked!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public class ClientsListAdapter extends RecyclerView.Adapter<ClientHolder> {

        private List<Contragent> data;

        public ClientsListAdapter(List<Contragent> data) {
            this.data = data;
        }

        @Override
        public ClientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.table_row, parent, false);
            return new ClientHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ClientHolder holder, int position) {
            Contragent client = data.get(position);
            holder.bind(client, position);
        }

        @Override
        public int getItemViewType(int position) {
            Contragent client = data.get(position);
            return 0;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}