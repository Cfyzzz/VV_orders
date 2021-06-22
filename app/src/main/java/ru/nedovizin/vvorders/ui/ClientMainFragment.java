package ru.nedovizin.vvorders.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.nedovizin.vvorders.R;
import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Contragent;
import ru.nedovizin.vvorders.models.Order;

public class ClientMainFragment extends Fragment {
    private RecyclerView mClientRecyclerView;
    private ClientsListAdapter mAdapter;
    private Date mDateOrder;

    private Button send_button;
    private Button date_button;
    private List<String> clients;
    private List<Order> mOrders;

    public static final int REQUEST_ORDER = 0;
    public static final String DIALOG_ORDER = "DialogOrder";
    public static final String DIALOG_DATE = "DialogDate";
    public static final int REQUEST_DATE = 0;
    public static final String EXTRA_DATE = "Date";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list, container, false);
        mClientRecyclerView = view.findViewById(R.id.table_clients);
        mClientRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        date_button = view.findViewById(R.id.date_button);
        mDateOrder = getTomorrowDate();
        updateDate();
        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mDateOrder);
                dialog.setTargetFragment(ClientMainFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        // TODO - Заменить на показ свежих заявок по клиентам
         ClientLab clietnLab = ClientLab.get(getContext());
        clients = clietnLab.getClientsByDate(mDateOrder);
        mAdapter = new ClientsListAdapter(clients);
        mClientRecyclerView.setAdapter(mAdapter);

        send_button = (Button) view.findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OrderActivity.class);
                intent.putExtra(EXTRA_DATE, mDateOrder);
                startActivity(intent);
                // TODO - Попытка открыть во фрагменте новую заявку
//                FragmentManager manager = getFragmentManager();
//                OrderFragment order = OrderFragment.newInstance();
//                order.setTargetFragment(ClientMainFragment.this, REQUEST_ORDER);
//                Intent intent = new Intent();
//                startActivity(order.getActivity().getIntent());
            }
        });

        return view;
    }

    private class ClientHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // будет заполнять ваш макет
        private TextView mIsUpdated;
        private TextView mNumberClient;
        private TextView mNameClient;
        private String mClient;

        public ClientHolder(View view) {
            super(view);

            mIsUpdated = itemView.findViewById(R.id.col1);
            mNumberClient = itemView.findViewById(R.id.col2);
            mNameClient = itemView.findViewById(R.id.col3);
            itemView.setOnClickListener(this);
        }

        public void bind(String client, int position) {
            mClient = client;
            mIsUpdated.setText((position%2==0)?"V":" ");
            mNumberClient.setText(Integer.toString(position));
            mNameClient.setText(client);
        }

        @Override
        public void onClick(View v) {
            // TODO - Реакция на клик по заявке в списке заявок
            Toast.makeText(getActivity(),
                    mClient + " clicked!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public class ClientsListAdapter extends RecyclerView.Adapter<ClientHolder> {

        private List<String> data;

        public ClientsListAdapter(List<String> data) {
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
            String client = data.get(position);
            holder.bind(client, position);
        }

        @Override
        public int getItemViewType(int position) {
            String client = data.get(position);
            return 0;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public Date getTomorrowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    private void updateDate() {
        date_button.setText(DateFormat.format("MMM d, yyyy", mDateOrder).toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mDateOrder = date;
            updateDate();
        }
    }
}