package ru.nedovizin.vvorders.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.Inflater;

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

public class OrderFragment extends Fragment {
    public static final String ARG_ORDER_ID = "order_id";
    public static final String EXTRA_DATE = "Date";

    private ClientLab mClientLab;
    private RecyclerView mProductReclerView;
    private OrderFragment.ProductListAdapter mAdapter;
    private List<ProductItem> mProducts;
    private View mOrderActivityBase;
    private Order mOrder;
    private Button mSaveOrderButton;
    private final String ARG_PRODUCT_POSITION = "ru.nedovizin.criminalintent.CrimeListFragment.ARG_PRODUCT_POSITION";
    private final String TAG = ".OrderFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String orderId = (String) getArguments().getSerializable(ARG_ORDER_ID);
        mOrder = ClientLab.get(getActivity()).getOrder(orderId);
    }

    public static  OrderFragment newInstance(String orderId) {
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
        mProductReclerView = view.findViewById(R.id.table_products);
        mProductReclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mClientLab = ClientLab.get(getActivity());
        mSaveOrderButton = view.findViewById(R.id.save_order_button);

        DelayAutoCompleteTextView addressTitle = (DelayAutoCompleteTextView) view.findViewById(R.id.address);
        DelayAutoCompleteTextView clientTitle = (DelayAutoCompleteTextView) view.findViewById(R.id.client);
        DelayAutoCompleteTextView productTitle = (DelayAutoCompleteTextView) view.findViewById(R.id.product_input);

        if (mOrder != null) {
            clientTitle.setText(mOrder.client);
            addressTitle.setText(mOrder.address);

            // Блокируем элементы, если заявка имеет статус (значит она уже отправлена на сервер)
            if (mOrder.status != null && !mOrder.status.isEmpty()) {
                mProductReclerView.setEnabled(false);
                addressTitle.setEnabled(false);
                clientTitle.setEnabled(false);
                productTitle.setEnabled(false);
            }
        } else {
            mOrder = new Order();
            mOrder.client = clientTitle.getText().toString();
            Date date = (Date) getArguments().getSerializable(EXTRA_DATE);
            Log.d(TAG, "Date = " + date);
            mOrder.date = mClientLab.DateToString(date);
            mOrder.code = UUID.randomUUID().toString();
            mOrder.address = addressTitle.getText().toString();
        }

        mProducts = mClientLab.getProductsByOrderId(mOrder.code);
        updateUI();

        mSaveOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrder.client = clientTitle.getText().toString();
                mOrder.address = addressTitle.getText().toString();
                mClientLab.clearProducts(mOrder);
                mClientLab.addOrder(mOrder, mProducts);
                getActivity().finish();
//                getActivity().onBackPressed();
            }
        });

        clientTitle.setThreshold(4);
        clientTitle.setAdapter(new ClientAutoCompleteAdapter(getActivity()));
        clientTitle.setLoadingIndicator((ProgressBar) view.findViewById(R.id.progress_bar));
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
        addressTitle.setAdapter(new AddressAutoCompleteAdapter(getContext()));
        addressTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Address address = (Address) adapterView.getItemAtPosition(position);
                addressTitle.setText(address.name);
                productTitle.requestFocus();
            }
        });

        productTitle.setThreshold(5);
        productTitle.setAdapter(new ProductAutoCompleteAdapter(getContext()));
        productTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ProductItem productItem = (ProductItem) adapterView.getItemAtPosition(position);
                mProducts.add(productItem);
                productTitle.setText("");
                updateUI();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (mAdapter == null) {
            Log.d(TAG, "updateUI mAdapter == null");
            mAdapter = new OrderFragment.ProductListAdapter(mProducts);
            mProductReclerView.setAdapter(mAdapter);
            enableSwipeToDeleteAndUndo();
        } else {
            Log.d(TAG, "updateUI mAdapter != null");
            mAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "quantity products: " + Integer.toString(mProducts.size()));
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
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

            if (mOrder.status != null && !mOrder.status.isEmpty())
                return;

            //Получаем вид с файла prompt.xml, который применим для диалогового окна:
            LayoutInflater li = LayoutInflater.from(getActivity());
            View promptsView = li.inflate(R.layout.prompt, null);

            //Создаем AlertDialog
            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getActivity());

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

    private class ProductListAdapter extends RecyclerView.Adapter<OrderFragment.ProductHolder> {

        List<ProductItem> data;

        public ProductListAdapter(List<ProductItem> data) {
            this.data = data;
        }

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
