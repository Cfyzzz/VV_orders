package ru.nedovizin.vvorders.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ru.nedovizin.vvorders.ProductItem;
import ru.nedovizin.vvorders.R;
import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Contragent;
import ru.nedovizin.vvorders.models.Order;

public class ClientMainFragment extends Fragment {
    private RecyclerView mClientRecyclerView;
    private ClientsListAdapter mAdapter;
    private Date mDateOrder;
    private View mActivityListBase;

    private Button send_button;
    private Button date_button;
    private List<Order> mOrders;
    private String TAG = ".CMF";

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
        mActivityListBase = view.findViewById(R.id.activity_list_base);

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

        updateUI();

        send_button = (Button) view.findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OrderActivity.class);
                intent.putExtra(EXTRA_DATE, mDateOrder);
                startActivity(intent);
                Log.d(TAG, "send_button.onClick after startActivity");
                // TODO - Попытка открыть во фрагменте новую заявку,
                //  но при закрытии заявки закрывается всё приложение
//                Bundle args = new Bundle();
//                args.putSerializable(EXTRA_DATE, mDateOrder);
//                OrderFragment order = OrderFragment.newInstance(null);
//                order.setArguments(args);
//                FragmentManager manager = getFragmentManager();
//                FragmentTransaction transaction = manager.beginTransaction();
//                transaction.replace(R.id.fragment_container, order);
//                transaction.commit();
            }
        });

        return view;
    }

    private class ClientHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // будет заполнять ваш макет
        private TextView mIsUpdated;
        private TextView mNumberClient;
        private TextView mNameClient;
        private Order order;

        public ClientHolder(View view) {
            super(view);

            mIsUpdated = itemView.findViewById(R.id.col1);
            mNumberClient = itemView.findViewById(R.id.col2);
            mNameClient = itemView.findViewById(R.id.col3);
            itemView.setOnClickListener(this);
        }

        public void bind(Order order, int position) {
            this.order = order;
            mIsUpdated.setText((position%2==0)?"V":" ");
            mNumberClient.setText(String.format("%d", position));
            mNameClient.setText(order.client);
        }

        @Override
        public void onClick(View v) {
            // TODO - Реакция на клик по заявке в списке заявок
            Intent intent = OrderPagerActivity.newIntent(getActivity(), order.code, mDateOrder);
            startActivity(intent);
        }
    }

    public class ClientsListAdapter extends RecyclerView.Adapter<ClientHolder> {

        private List<Order> data;

        public ClientsListAdapter(List<Order> data) {
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
            Order order = data.get(position);
            holder.bind(order, position);
        }

        @Override
        public int getItemViewType(int position) {
            String client = data.get(position).client;
            return 0;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public List<Order> getData() {
            return data;
        }

        public void removeItem(int position) {
            data.remove(position);
            notifyItemRemoved(position);
        }

        public void restoreItem(Order item, int position) {
            data.add(position, item);
            notifyItemInserted(position);
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

    private void updateUI() {
        ClientLab mClientLab = ClientLab.get(getContext());
        mOrders = mClientLab.getOrdersByDate(mDateOrder);
        // TODO - Костыль обновления списка после добавления элемента
        mAdapter = null;

        if (mAdapter == null) {
            mAdapter = new ClientsListAdapter(mOrders);
            mClientRecyclerView.setAdapter(mAdapter);
            enableSwipeToDeleteAndUndo();
        } else  {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final Order order = mAdapter.getData().get(position);

                mAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(mActivityListBase, "Элемент был удалён из списка.", Snackbar.LENGTH_LONG);
                snackbar.setAction("ОТМЕНА", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAdapter.restoreItem(order, position);
                        mClientRecyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mClientRecyclerView);
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
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        Log.d(TAG, "Resume");
    }
}