package ru.nedovizin.vvorders.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import ru.nedovizin.vvorders.database.ClientDbSchema;
import ru.nedovizin.vvorders.models.Address;
import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Contragent;
import ru.nedovizin.vvorders.models.Order;
import ru.nedovizin.vvorders.models.Product;

public class OrderActivity extends MenuActivity {

    private ClientLab mClientLab;
    private RecyclerView mProductReclerView;
    private ProductListAdapter mAdapter;
    private List<ProductItem> mProducts;
    private View mOrderActivityBase;
    private Button mSaveOrderButton;
    private final String ARG_PRODUCT_POSITION = "ru.nedovizin.criminalintent.CrimeListFragment.ARG_PRODUCT_POSITION";
    private final String TAG = ".OrderActivity";

    public static final String EXTRA_DATE = "Date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mOrderActivityBase = findViewById(R.id.order_activity_base);
        mProductReclerView = findViewById(R.id.table_products);
        mProductReclerView.setLayoutManager(new LinearLayoutManager(this));
        mClientLab = ClientLab.get(getBaseContext());
        mSaveOrderButton = findViewById(R.id.save_order_button);

        DelayAutoCompleteTextView addressTitle = (DelayAutoCompleteTextView) findViewById(R.id.address);
        DelayAutoCompleteTextView clientTitle = (DelayAutoCompleteTextView) findViewById(R.id.client);
        DelayAutoCompleteTextView productTitle = (DelayAutoCompleteTextView) findViewById(R.id.product_input);

        mProducts = new ArrayList<>();
        updateUI();

        mSaveOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order order = new Order();
                order.client = clientTitle.toString();
                Date date = (Date) getIntent().getSerializableExtra(EXTRA_DATE);
                order.date = DateFormat.format("yyyy.MM.dd", date).toString();
                order.code = order.client + order.date;
                mClientLab.addOrder(order, mProducts);
            }
        });

        clientTitle.setThreshold(4);
        clientTitle.setAdapter(new ClientAutoCompleteAdapter(getBaseContext()));
        clientTitle.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar));
        clientTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
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
            }
        });

        addressTitle.setThreshold(1);
        addressTitle.setAdapter(new AddressAutoCompleteAdapter(getBaseContext()));
        addressTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Address address = (Address) adapterView.getItemAtPosition(position);
                addressTitle.setText(address.name);
                productTitle.requestFocus();
            }
        });

        productTitle.setThreshold(5);
        productTitle.setAdapter(new ProductAutoCompleteAdapter(getBaseContext()));
        productTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ProductItem productItem = (ProductItem) adapterView.getItemAtPosition(position);
                mProducts.add(productItem);
                productTitle.setText("");
                updateUI();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (mAdapter == null) {
            Log.d(TAG, "updateUI mAdapter == null");
            mAdapter = new ProductListAdapter(mProducts);
            mProductReclerView.setAdapter(mAdapter);
            enableSwipeToDeleteAndUndo();
        } else {
            Log.d(TAG, "updateUI mAdapter != null");
            mAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "quantity products: " + Integer.toString(mProducts.size()));
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final ProductItem item = mAdapter.getData().get(position);

                mAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(mOrderActivityBase, "Элемент был удалён из списка.", Snackbar.LENGTH_LONG);
                snackbar.setAction("ОТМЕНА", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAdapter.restoreItem(item, position);
                        mProductReclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mProductReclerView);
    }

    private class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNameProduct;
        private TextView mQuantityProducts;
        private ProductItem mProduct;

        public ProductHolder(View view) {
            super(view);

            mNameProduct = itemView.findViewById(R.id.col3);
            mQuantityProducts = itemView.findViewById(R.id.col4);
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
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    //Вводим текст и отображаем в строке ввода на основном экране:
                                    mProduct.quantity = userInput.getText().toString();
                                    updateUI();
                                }
                            })
                    .setNegativeButton("Отмена",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

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

        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.table_row, parent, false);
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