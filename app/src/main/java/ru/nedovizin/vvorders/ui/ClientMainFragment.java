package ru.nedovizin.vvorders.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.nedovizin.vvorders.ProductItem;
import ru.nedovizin.vvorders.R;
import ru.nedovizin.vvorders.archive.ClientsListAdapter;
import ru.nedovizin.vvorders.http.APIClient;
import ru.nedovizin.vvorders.http.APIInterface;
import ru.nedovizin.vvorders.http.MultipleResource;
import ru.nedovizin.vvorders.models.Address;
import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Contragent;
import ru.nedovizin.vvorders.models.Order;
import ru.nedovizin.vvorders.models.Product;

public class ClientMainFragment extends Fragment {
    private RecyclerView mClientRecyclerView;
    private ClientsListAdapter mAdapter;
    private Date mDateOrder;
    private View mActivityListBase;
    private TextView mStatusLine;

    private Button newOrderButton;
    private Button sendOrderButton;
    private Button dateButton;
    private List<Order> mOrders;
    private String TAG = ".CMF";

    public static final int REQUEST_ORDER = 0;
    public static final String DIALOG_ORDER = "DialogOrder";
    public static final String DIALOG_DATE = "DialogDate";
    public static final int REQUEST_DATE = 0;
    public static final String EXTRA_DATE = "Date";

    // TODO - Получать из конфигов
    public static final String URL_BASE = "http://192.168.0.182";
    public static final String LOGIN_BASE = "Елена";

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
        mStatusLine = view.findViewById(R.id.status_line);

        dateButton = view.findViewById(R.id.date_button);
        mDateOrder = getTomorrowDate();
        updateDate();
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mDateOrder);
                dialog.setTargetFragment(ClientMainFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        updateUI();

        newOrderButton = view.findViewById(R.id.new_order_button);
        newOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OrderActivity.class);
                intent.putExtra(EXTRA_DATE, mDateOrder);
                startActivity(intent);
                Log.d(TAG, "new_order_button.onClick after startActivity");
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

        sendOrderButton = view.findViewById(R.id.send_button);
        sendOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // подготовить все выделенные заявки
                List<Order> orders = new ArrayList<>();
                for (Order order : mOrders) {
                    if (order.selected.equals("true")) {
                        ClientLab clientLab = ClientLab.get(getContext());
                        List<ProductItem> productItems = clientLab.getProductsByOrderId(order.code);
                        order.setProducts(productItems);
                        orders.add(order);
                    }
                }
                // запаковать в JSON все выделенные заявки
//                StringWriter output = new StringWriter();
//                try {
//                    writeOrderJsonStream(output, orders);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                // отправить данные на сервер AS
                APIInterface apiInterface;
                apiInterface = APIClient.getData(URL_BASE).create(APIInterface.class);
                sendOrders(apiInterface, LOGIN_BASE, orders);
            }
        });

        return view;
    }

    private class ClientHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // будет заполнять ваш макет
        private CheckBox mIsUpdated;
        private TextView mNumberClient;
        private TextView mNameClient;
        private Order order;

        public ClientHolder(View view) {
            super(view);

            mIsUpdated = itemView.findViewById(R.id.col1);
            mIsUpdated.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    order.selected = Boolean.toString(isChecked);
                }
            });
            mNumberClient = itemView.findViewById(R.id.col2);
            mNameClient = itemView.findViewById(R.id.col3);
            itemView.setOnClickListener(this);
        }

        public void bind(Order order, int position) {
            this.order = order;
            if (order.selected != null)
                mIsUpdated.setChecked(order.selected.equals("true"));
            mNumberClient.setText(String.format("%d", position + 1));
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
        dateButton.setText(DateFormat.format("MMM d, yyyy", mDateOrder).toString());
    }

    private void updateUI() {
        ClientLab mClientLab = ClientLab.get(getContext());
        mOrders = mClientLab.getOrdersByDate(mDateOrder);
        for (Order order : mOrders) {
            order.selected = "true";
        }
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

    // TODO - Не используется
    public static void writeOrderJsonStream(Writer output, List<Order> orders) throws IOException {
        JsonWriter jsonWriter = new JsonWriter(output);

        jsonWriter.beginObject();// begin root
        jsonWriter.name("Заявки");
        jsonWriter.beginObject();

        for (Order order : orders) {
            jsonWriter.beginObject();

            jsonWriter.name("Код").value(order.code);
            jsonWriter.name("Клиент").value(order.client);
            jsonWriter.name("Дата").value(order.date);
            jsonWriter.name("Адрес").value(order.address);

            ClientLab clientLab = ClientLab.get(null);
            List<ProductItem> productItems = clientLab.getProductsByOrderId(order.code);

            jsonWriter.name("Номенклатура");
            jsonWriter.beginObject();
            for (ProductItem item : productItems) {
                jsonWriter.beginObject();
                jsonWriter.name("Номенклатура").value(item.product.name);
                jsonWriter.name("КодНоменклатуры").value(item.product.code);
                jsonWriter.name("Количество").value(item.quantity);
                jsonWriter.endObject();
            }
            jsonWriter.endObject();

            jsonWriter.endObject();
        }
        jsonWriter.endObject();
        jsonWriter.endObject(); // end root
    }

    private void sendOrders(APIInterface apiInterface, String userName, List<Order> orders) {
        mStatusLine.setText("");
        Call<List<Order>> call = apiInterface.sendOrders(userName, orders);
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                Log.d(TAG, "sendOrders.body: " + response.body().toString());
                if (response.code() == 200) {
                    mStatusLine.setText("Заявки отправлены");
                } else {
                    mStatusLine.setText("Заявки не приняты сервером");
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.d(TAG, "sendOrders failure");
                mStatusLine.setText("Сервер не отвечает (failure)");
            }
        });
    }
}