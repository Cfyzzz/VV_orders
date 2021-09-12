package ru.nedovizin.vvorders.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import ru.nedovizin.vvorders.AddressAutoCompleteAdapter;
import ru.nedovizin.vvorders.ClientAutoCompleteAdapter;
import ru.nedovizin.vvorders.DelayAutoCompleteTextView;
import ru.nedovizin.vvorders.ProductAutoCompleteAdapter;
import ru.nedovizin.vvorders.ProductItem;
import ru.nedovizin.vvorders.R;
import ru.nedovizin.vvorders.models.Address;
import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Contragent;
import ru.nedovizin.vvorders.models.Order;

/** Окно редактирования заявки (Фрагмент)
 *
 */
public class OrderFragment extends Fragment {
    public static final String ARG_ORDER_ID = "order_id";
    public static final String EXTRA_DATE = "Date";

    private ClientLab mClientLab;
    private RecyclerView mProductRecyclerView;
    private OrderFragment.ProductListAdapter mAdapter;
    private List<ProductItem> mProducts;
    private View mOrderActivityBase;
    private Order mOrder;
    private Button mSaveOrderButton;
    private final String TAG = ".OrderFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        String orderId = (String) getArguments().getSerializable(ARG_ORDER_ID);
        mOrder = ClientLab.get(getActivity()).getOrder(orderId);
    }

    /** Получить настроенный фрагмент окна редактирования заявки
     *
     * @param orderId Поле код {@code code} заявки
     * @return Настроенный фрагмент заявки
     */
    public static OrderFragment newInstance(String orderId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ORDER_ID, orderId);

        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_order, container, false);

        mOrderActivityBase = view.findViewById(R.id.order_activity_base);
        mProductRecyclerView = view.findViewById(R.id.table_products);
        mProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mClientLab = ClientLab.get(getActivity());
        mSaveOrderButton = view.findViewById(R.id.save_order_button);

        DelayAutoCompleteTextView addressTitle = (DelayAutoCompleteTextView) view.findViewById(R.id.address);
        DelayAutoCompleteTextView clientTitle = (DelayAutoCompleteTextView) view.findViewById(R.id.client);
        DelayAutoCompleteTextView productTitle = (DelayAutoCompleteTextView) view.findViewById(R.id.product_input);

        if (mOrder != null) {
            clientTitle.setText(mOrder.client);
            addressTitle.setText(mOrder.address);

            // Блокируем элементы, если заявка имеет статус (значит она уже отправлена на сервер)
            if (mOrder.hasStatusSent()) {
                mProductRecyclerView.setEnabled(false);
                addressTitle.setEnabled(false);
                clientTitle.setEnabled(false);
                productTitle.setEnabled(false);
            }
        } else {
            // На случай новой заявки
            mOrder = new Order();
            mOrder.client = clientTitle.getText().toString();
            assert getArguments() != null;
            Date date = (Date) getArguments().getSerializable(EXTRA_DATE);
            Log.d(TAG, "Date = " + date);
            mOrder.date = mClientLab.DateToString(date);
            mOrder.code = UUID.randomUUID().toString();
            mOrder.address = addressTitle.getText().toString();
        }

        mProducts = mClientLab.getProductsByOrderId(mOrder.code);
        updateUI();

        mSaveOrderButton.setOnClickListener(v -> {
            // Сохранить заявку
            mOrder.client = clientTitle.getText().toString();
            mOrder.address = addressTitle.getText().toString();
            mClientLab.clearProducts(mOrder);
            mClientLab.addOrder(mOrder, mProducts);
            Objects.requireNonNull(getActivity()).finish();
        });

        clientTitle.setThreshold(4);
        clientTitle.setAdapter(new ClientAutoCompleteAdapter(getActivity()));
        clientTitle.setLoadingIndicator((ProgressBar) view.findViewById(R.id.progress_bar));
        clientTitle.setOnItemClickListener((adapterView, view1, position, id) -> {
            // Выбор клиента из предложенного списка
            Contragent client = (Contragent) adapterView.getItemAtPosition(position);
            clientTitle.setText(client.name);
            addressTitle.requestFocus();
            mClientLab.setCurrentClient(client);
            List<Address> addresses = mClientLab.getAddressesByClient(client);
            if (addresses.size() == 1) {
                addressTitle.setText(addresses.get(0).name);
                productTitle.requestFocus();
            } else {
                addressTitle.setText(" ");
            }
        });

        addressTitle.setThreshold(1);
        addressTitle.setAdapter(new AddressAutoCompleteAdapter(getContext()));
        addressTitle.setOnItemClickListener((adapterView, view12, position, id) -> {
            // Выбор адреса из предложенного списка
            Address address = (Address) adapterView.getItemAtPosition(position);
            addressTitle.setText(address.name);
            productTitle.requestFocus();
        });

        productTitle.setThreshold(5);
        productTitle.setAdapter(new ProductAutoCompleteAdapter(getContext()));
        productTitle.setOnItemClickListener((adapterView, view13, position, id) -> {
            // Выбор продукта из предложенного списка
            ProductItem productItem = (ProductItem) adapterView.getItemAtPosition(position);
            mProducts.add(productItem);
            productTitle.setText("");
            updateUI();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /** Обновение UI
     *
     */
    private void updateUI() {
        if (mAdapter == null) {
            Log.d(TAG, "updateUI mAdapter == null");
            mAdapter = new OrderFragment.ProductListAdapter(mProducts);
            mProductRecyclerView.setAdapter(mAdapter);
            enableSwipeToDeleteAndUndo();
        } else {
            Log.d(TAG, "updateUI mAdapter != null");
            mAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "quantity products: " + mProducts.size());
    }

    /** Подключение интерфейса удаление элемента свайпом влево
     *
     */
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final ProductItem item = mAdapter.getData().get(position);

                mAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(mOrderActivityBase, "Элемент был удалён из списка.", Snackbar.LENGTH_LONG);
                snackbar.setAction("ОТМЕНА", view -> {
                    // Восстановление удаленной позиции в списке продуктов
                    mAdapter.restoreItem(item, position);
                    mProductRecyclerView.scrollToPosition(position);
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mProductRecyclerView);
    }

    private class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mNameProduct;
        private final TextView mQuantityProducts;
        private ProductItem mProduct;

        public ProductHolder(View view) {
            super(view);

            mNameProduct = itemView.findViewById(R.id.row_col3);
            mQuantityProducts = itemView.findViewById(R.id.row_col4);
            itemView.setOnClickListener(this);
        }

        /** Связь продукта с UI
         *
         * @param productItem Строка продукта с количеством
         */
        public void bind(ProductItem productItem) {
            mProduct = productItem;
            mNameProduct.setText(productItem.product.name);
            mQuantityProducts.setText(productItem.quantity);
        }

        @Override
        public void onClick(View v) {
            // Реакция на клик по строке номенклатуры в заявке

            if (mOrder.status != null && !mOrder.status.isEmpty())
                return;

            //Получаем вид с файла prompt.xml, который применим для диалогового окна:
            LayoutInflater li = LayoutInflater.from(getActivity());
            View promptsView = li.inflate(R.layout.prompt, null);

            //Создаем AlertDialog
            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

            //Настраиваем prompt.xml для нашего AlertDialog:
            mDialogBuilder.setView(promptsView);

            //Настраиваем отображение поля для ввода текста в открытом диалоге:
            final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
            userInput.setText(mProduct.quantity);

            //Настраиваем сообщение в диалоговом окне:
            mDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            (dialog, id) -> {
                                //Вводим текст и отображаем в строке ввода на основном экране:
                                mProduct.quantity = userInput.getText().toString();
                                updateUI();
                            })
                    .setNegativeButton("Отмена",
                            (dialog, id) -> dialog.cancel());

            //Создаем AlertDialog:
            AlertDialog alertDialog = mDialogBuilder.create();

            //и отображаем его:
            alertDialog.show();
        }
    }

    private class ProductListAdapter extends RecyclerView.Adapter<OrderFragment.ProductHolder> {

        List<ProductItem> data;

        public ProductListAdapter(List<ProductItem> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public OrderFragment.ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.table_simple_row, parent, false);
            return new OrderFragment.ProductHolder(itemView);
        }

        @Override
        public void onBindViewHolder(OrderFragment.ProductHolder holder, int position) {
            ProductItem product = data.get(position);
            holder.bind(product);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void removeItem(int position) {
            data.remove(position);
            notifyItemRemoved(position);
        }

        public void restoreItem(ProductItem item, int position) {
            data.add(position, item);
            notifyItemInserted(position);
        }

        public List<ProductItem> getData() {
            return data;
        }
    }
}
