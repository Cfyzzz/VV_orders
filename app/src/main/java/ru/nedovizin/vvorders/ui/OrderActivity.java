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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

/** Окно для ввода новой заявки (Активность)
 *
 */
public class OrderActivity extends MenuActivity {

    private ClientLab mClientLab;
    private RecyclerView mProductRecyclerView;
    private ProductListAdapter mAdapter;
    private List<ProductItem> mProducts;
    private View mOrderActivityBase;
    private Button mSaveOrderButton;
    private final String TAG = ".OrderActivity";

    public static final String EXTRA_DATE = "Date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mOrderActivityBase = findViewById(R.id.order_activity_base);
        mProductRecyclerView = findViewById(R.id.table_products);
        mProductRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mClientLab = ClientLab.get(getBaseContext());
        mSaveOrderButton = findViewById(R.id.save_order_button);

        DelayAutoCompleteTextView addressTitle = (DelayAutoCompleteTextView) findViewById(R.id.address);
        DelayAutoCompleteTextView clientTitle = (DelayAutoCompleteTextView) findViewById(R.id.client);
        DelayAutoCompleteTextView productTitle = (DelayAutoCompleteTextView) findViewById(R.id.product_input);

        mProducts = new ArrayList<>();
        updateUI();

        mSaveOrderButton.setOnClickListener(v -> {
            // Сохранить заявку и закрыть окно
            Order order = new Order();
            order.client = clientTitle.getText().toString();
            order.address = addressTitle.getText().toString();
            Date date = (Date) getIntent().getSerializableExtra(EXTRA_DATE);
            order.date = mClientLab.DateToString(date);
            order.code = UUID.randomUUID().toString();
            mClientLab.addOrder(order, mProducts);
            finish();
        });

        clientTitle.setThreshold(4);
        clientTitle.setAdapter(new ClientAutoCompleteAdapter(getBaseContext()));
        clientTitle.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar));
        clientTitle.setOnItemClickListener((adapterView, view, position, id) -> {
            // Выбор варианта из клиентов
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
        addressTitle.setAdapter(new AddressAutoCompleteAdapter(getBaseContext()));
        addressTitle.setOnItemClickListener((adapterView, view, position, id) -> {
            // Выбор варианта из адресов
            Address address = (Address) adapterView.getItemAtPosition(position);
            addressTitle.setText(address.name);
            productTitle.requestFocus();
        });

        productTitle.setThreshold(5);
        productTitle.setAdapter(new ProductAutoCompleteAdapter(getBaseContext()));
        productTitle.setOnItemClickListener((adapterView, view, position, id) -> {
            // Выбор варианта из продукции
            ProductItem productItem = (ProductItem) adapterView.getItemAtPosition(position);
            mProducts.add(productItem);
            productTitle.setText("");
            updateUI();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    /** Обновить UI заявки
     *
     */
    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new ProductListAdapter(mProducts);
            mProductRecyclerView.setAdapter(mAdapter);
            enableSwipeToDeleteAndUndo();
        } else {
            mAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "quantity products: " + mProducts.size());
    }

    /** Подключить интерфейс удаления свайпом влево
     *
     */
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final ProductItem item = mAdapter.getData().get(position);

                mAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(mOrderActivityBase, "Элемент был удалён из списка.", Snackbar.LENGTH_LONG);
                snackbar.setAction("ОТМЕНА", view -> {
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

        public void bind(ProductItem productItem, int position) {
            mProduct = productItem;
            mNameProduct.setText(productItem.product.name);
            mQuantityProducts.setText(productItem.quantity);
        }

        @Override
        public void onClick(View v) {
            // Реакция на клик по строке номенклатуры в заявке

            //Получаем вид с файла prompt.xml, который применим для диалогового окна:
            LayoutInflater li = LayoutInflater.from(OrderActivity.this);
            View promptsView = li.inflate(R.layout.prompt, null);

            //Создаем AlertDialog
            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(OrderActivity.this);

            //Настраиваем prompt.xml для нашего AlertDialog:
            mDialogBuilder.setView(promptsView);

            //Настраиваем отображение поля для ввода текста в открытом диалоге:
            final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
            userInput.setText(mProduct.quantity);
//            userInput.setSelection(0, mProduct.quantity.length());

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

    private class ProductListAdapter extends RecyclerView.Adapter<ProductHolder> {

        List<ProductItem> data;

        public ProductListAdapter(List<ProductItem> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.table_simple_row, parent, false);
            return new ProductHolder(itemView);
        }

        @Override
        public void onBindViewHolder(OrderActivity.ProductHolder holder, int position) {
            ProductItem product = data.get(position);
            holder.bind(product, position);
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