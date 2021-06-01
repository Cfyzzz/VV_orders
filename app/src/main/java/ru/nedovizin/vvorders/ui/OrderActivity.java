package ru.nedovizin.vvorders.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.nedovizin.vvorders.AddressAutoCompleteAdapter;
import ru.nedovizin.vvorders.ClientAutoCompleteAdapter;
import ru.nedovizin.vvorders.DelayAutoCompleteTextView;
import ru.nedovizin.vvorders.ProductAutoCompleteAdapter;
import ru.nedovizin.vvorders.ProductItem;
import ru.nedovizin.vvorders.R;
import ru.nedovizin.vvorders.models.Address;
import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Contragent;
import ru.nedovizin.vvorders.models.Product;

public class OrderActivity extends MenuActivity {

    private ClientLab mClientLab;
    private RecyclerView mProductReclerView;
    private ProductListAdapter mAdapter;
    private List<ProductItem> mProducts;
    private final String ARG_PRODUCT_POSITION = "ru.nedovizin.criminalintent.CrimeListFragment.ARG_PRODUCT_POSITION";
    private final String TAG = ".OrderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mProductReclerView = findViewById(R.id.table_products);
        mProductReclerView.setLayoutManager(new LinearLayoutManager(this));
        mClientLab = ClientLab.get(getBaseContext());

        DelayAutoCompleteTextView addressTitle = (DelayAutoCompleteTextView) findViewById(R.id.address);
        DelayAutoCompleteTextView clientTitle = (DelayAutoCompleteTextView) findViewById(R.id.client);
        DelayAutoCompleteTextView productTitle = (DelayAutoCompleteTextView) findViewById(R.id.product_input);

        mProducts = new ArrayList<>();
        updateUI();

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
        } else {
            Log.d(TAG, "updateUI mAdapter != null");
            mAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "quantity products: " + Integer.toString(mProducts.size()));
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
            // TODO - Реакция на клик по строке номенклатуры в заявке
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
    }
}