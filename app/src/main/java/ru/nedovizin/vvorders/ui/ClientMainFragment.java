package ru.nedovizin.vvorders.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.nedovizin.vvorders.ProductItem;
import ru.nedovizin.vvorders.R;
import ru.nedovizin.vvorders.http.APIClient;
import ru.nedovizin.vvorders.http.APIInterface;
import ru.nedovizin.vvorders.http.MultipleResource;
import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Order;
import ru.nedovizin.vvorders.models.OrdersForSend;
import ru.nedovizin.vvorders.models.SettingsConnect;

/**
 * Отображает главное окно программы
 */
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
    private SettingsConnect mSettingsConnect;

    public static final String DIALOG_DATE = "DialogDate";
    public static final int REQUEST_DATE = 0;
    public static final String EXTRA_DATE = "Date";
    public static final String STATUS_ORDER_RECIEVED = "Recieved";
    public static final String STATUS_ORDER_POSTED = "Posted";

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "id item menu: " + id);
        if (id == R.id.action_update_statuses) {
            // update statuses
            Log.d(TAG, "press update statuses");
            APIInterface apiInterface;
            apiInterface = APIClient.getData(mSettingsConnect.getHost()).create(APIInterface.class);
            updateOrderStatuses(apiInterface, mDateOrder);
            updateUI();
            return true;
        }
        return getActivity().onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.activity_list, container, false);
        mClientRecyclerView = view.findViewById(R.id.table_clients);
        mClientRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActivityListBase = view.findViewById(R.id.activity_list_base);
        mStatusLine = view.findViewById(R.id.status_line);

        dateButton = view.findViewById(R.id.date_button);
        mDateOrder = getTomorrowDate();
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Вызвать диалоговое окно для выбора даты
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
                // Открыть окно для ввода новой заявки
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
                mStatusLine.setText("");
                List<Order> orders = new ArrayList<>();
                for (Order order : mOrders) {
                    if (order.selected != null && order.selected.equals("true")) {
                        ClientLab clientLab = ClientLab.get(getContext());
                        List<ProductItem> productItems = clientLab.getProductsByOrderId(order.code);
                        order.setProducts(productItems);
                        orders.add(order);
                    }
                }

                APIInterface apiInterface;
                apiInterface = APIClient.getData(mSettingsConnect.getHost()).create(APIInterface.class);
                sendOrders(apiInterface, orders);
            }
        });

        return view;
    }

    private class ClientHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // будет заполнять макет списка
        private CheckBox mIsUpdated;
        private TextView mNumberClient;
        private TextView mNameClient;
        private TextView mStatus;
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
            mStatus = itemView.findViewById(R.id.col4);
            itemView.setOnClickListener(this);
        }

        /**
         * Установить свзять между номером позиции в списке и конкретной заявкой
         * Отобразить данные в UI
         *
         * @param order    Заявка
         * @param position Позиция в списке
         */
        public void bind(Order order, int position) {
            this.order = order;
            if (order.selected != null)
                mIsUpdated.setChecked(order.selected.equals("true"));
            else
                order.selected = "false";

            // UI
            mNumberClient.setText(String.format("%d", position + 1));
            mNameClient.setText(order.client);
            if (order.status == null)
                order.status = "";
            updateViewStatusOrder(order);
        }

        /**
         * Обновить статус заявки в UI
         *
         * @param order Заявка
         */
        private void updateViewStatusOrder(Order order) {
            switch (order.status) {
                case STATUS_ORDER_POSTED:
                    mStatus.setText("V");
                    mStatus.setTextColor(getResources().getColor(R.color.green));
                    break;
                case STATUS_ORDER_RECIEVED:
                    mStatus.setText("O");
                    mStatus.setTextColor(getResources().getColor(R.color.blue));
                    break;
                default:
                    mStatus.setText("-");
            }
        }

        @Override
        public void onClick(View v) {
            // Реакция на клик по заявке в списке заявок
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

        /**
         * Удалить элемент заявка из списка заявок
         *
         * @param position Номер позции элемента в спике заявок
         */
        public void removeItem(int position) {
            // Деактивируем в базе
            Order order = data.get(position);
            ClientLab.get(getContext()).inactivateOrder(order);
            // Чистим в списке
            data.remove(position);
            notifyItemRemoved(position);
        }

        /**
         * Восстановить удалённый недавно элемент
         *
         * @param order    Заявка
         * @param position Номер позиции в списке
         */
        public void restoreItem(Order order, int position) {
            // Активируем в базе
            ClientLab.get(getContext()).activateOrder(order);
            // Возвращаем в список
            data.add(position, order);
            notifyItemInserted(position);
        }
    }

    /**
     * Получить дату завтрашнего дня
     *
     * @return Дата завтрашнего дня
     */
    public Date getTomorrowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    /**
     * Одновить дату в UI
     */
    private void updateDate() {
        dateButton.setText(DateFormat.format("MMM d, yyyy", mDateOrder).toString());
    }

    /**
     * Обновить UI
     */
    private void updateUI() {
        updateDate();
        ClientLab mClientLab = ClientLab.get(getContext());
        mOrders = mClientLab.getOrdersByDate(mDateOrder);
        for (Order order : mOrders) {
            if (!order.hasStatusSent())
                order.selected = "true";
            else
                order.selected = "false";
        }

        mAdapter = new ClientsListAdapter(mOrders);
        mClientRecyclerView.setAdapter(mAdapter);
        enableSwipeToDeleteAndUndo();
    }

    /**
     * Подключть интерфейс удаление свайпом влево
     */
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final Order order = mAdapter.getData().get(position);

                mAdapter.removeItem(position);

                if (order.hasStatusSent()) {
                    // Возможность отмены удаления отправленных заявок
                    APIInterface apiInterface;
                    apiInterface = APIClient.getData(mSettingsConnect.getHost()).create(APIInterface.class);
                    sendDeleteOrder(apiInterface, order.code);
                } else {
                    // Возможность отмены удаления неотправленных заявок
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
        mSettingsConnect = ClientLab.get(getContext()).getSettingsConnect();
        updateUI();
        Log.d(TAG, "Resume");
    }

    /**
     * Отправить уведомление на AS об отмене пометки удаения заявки
     *
     * @param apiInterface API интерфейс
     * @param codeOrder    Поле код {@code code} заявки
     */
    private void sendCancelDeleteOrder(APIInterface apiInterface, String codeOrder) {
        mStatusLine.setText("");
        try {
            Call<MultipleResource> call = apiInterface.sendCancelDeleteOrder(codeOrder, mSettingsConnect.getAuthBase64());
            call.enqueue(new Callback<MultipleResource>() {
                @Override
                public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                    if (response.code() != 200)
                        return;
                    if (response.body().status.equals("Ok")) {
                        restoreDeletedOrder(codeOrder);
                    } else {
                        mStatusLine.setText(response.body().description);
                    }
                }

                @Override
                public void onFailure(Call<MultipleResource> call, Throwable t) {
                    Log.d(TAG, "sendCancelDeleteOrder failure");
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Отправить на AS ведомление об удалении заявки
     *
     * @param apiInterface API интерфейс
     * @param codeOrder    Поле код {@code code} заявки
     */
    private void sendDeleteOrder(APIInterface apiInterface, String codeOrder) {
        mStatusLine.setText("");
        try {
            Call<MultipleResource> call = apiInterface.sendDeleteOrder(codeOrder, mSettingsConnect.getAuthBase64());
            call.enqueue(new Callback<MultipleResource>() {
                @Override
                public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                    int code = response.code();
                    if (code == 204) {
                        Order order = ClientLab.get(getContext()).getOrder(codeOrder);
                        ClientLab.get(getContext()).inactivateOrder(order);
                        updateUI();
                        mStatusLine.setText("Заявка удалена");

                        Snackbar snackbar = Snackbar
                                .make(mActivityListBase, "Элемент был удалён из списка.", Snackbar.LENGTH_LONG);
                        snackbar.setAction("ОТМЕНА", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendCancelDeleteOrder(apiInterface, codeOrder);
                                mStatusLine.setText("");
                            }
                        });
                        snackbar.setActionTextColor(Color.YELLOW);
                        snackbar.show();
                    } else {
                        restoreDeletedOrder(codeOrder);
                        mStatusLine.setText(response.body().description);
                    }
                }

                @Override
                public void onFailure(Call<MultipleResource> call, Throwable t) {
                    restoreDeletedOrder(codeOrder);
                    Log.d(TAG, "sendDeleteOrder failure");
                    mStatusLine.setText("Сервер не отвечает (failure)");
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Отправить выделенные заявки на AS
     *
     * @param apiInterface API интерфейс
     * @param orders       Поле код {@code code} заявки
     */
    private void sendOrders(APIInterface apiInterface, List<Order> orders) {
        mStatusLine.setText("");
        OrdersForSend mOrdersForSend = new OrdersForSend(orders);
        try {
            Call<MultipleResource> call = apiInterface.sendOrders(mOrdersForSend, mSettingsConnect.getAuthBase64());
            call.enqueue(new Callback<MultipleResource>() {
                @Override
                public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                    int code = response.code();
                    if (code == 201) {
                        updateOrderStatuses(apiInterface, mDateOrder);
                        mStatusLine.setText("Заявки отправлены на сервер");
                    } else {
                        mStatusLine.setText(response.body().description);
                    }
                }

                @Override
                public void onFailure(Call<MultipleResource> call, Throwable t) {
                    Log.d(TAG, "sendOrders failure");
                    mStatusLine.setText("Сервер не отвечает (failure) " + TAG);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обновить статусы заявок на дату {@date}
     *
     * @param apiInterface API интерфейс
     * @param date         Текущая рабочая дата
     */
    private void updateOrderStatuses(APIInterface apiInterface, Date date) {
        mStatusLine.setText("");
        String d = ClientLab.get(getContext()).DateToString(date);
        Log.d(TAG, ">> updateOrderStatuses Date: " + d);
        try {
            Call<MultipleResource> call = apiInterface.doGetStatus(d, mSettingsConnect.getAuthBase64());
            call.enqueue(new Callback<MultipleResource>() {
                @Override
                public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                    if (response.code() == 200) {
                        mStatusLine.setText("");
                        Log.d(TAG, response.body().toString());
                        ClientLab clientLab = ClientLab.get(getContext());
                        for (MultipleResource.StatusOrder statusOrder : response.body().answer.mStatusOrders) {
                            Order order = clientLab.getOrder(statusOrder.code);
                            if (order != null) {
                                Log.d(TAG, "update status " + order.client + " is " + statusOrder.status);
                                order.status = statusOrder.status;
                                clientLab.updateOrder(order);
                            }
                        }
                        updateUI();
                        mStatusLine.setText("Статусы заявок обновлены");
                    } else {
                        mStatusLine.setText(response.body().description);
                    }
                }

                @Override
                public void onFailure(Call<MultipleResource> call, Throwable t) {
                    Log.d(TAG, "updateOrderStatuses failure");
                    mStatusLine.setText("Сервер не отвечает (failure)" + TAG);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Восстанавливает удалённую заявку
     *
     * @param codeOrder Поле код {@code code} заявки
     */
    private void restoreDeletedOrder(String codeOrder) {
        Order order = ClientLab.get(getContext()).getOrder(codeOrder);
        ClientLab.get(getContext()).activateOrder(order);
        updateUI();
    }
}