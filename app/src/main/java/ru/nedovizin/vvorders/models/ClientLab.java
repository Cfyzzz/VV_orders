package ru.nedovizin.vvorders.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.nedovizin.vvorders.ProductItem;
import ru.nedovizin.vvorders.database.AddressCursorWrapper;
import ru.nedovizin.vvorders.database.ClientBaseHelper;
import ru.nedovizin.vvorders.database.ClientCursorWrapper;
import ru.nedovizin.vvorders.database.ClientDbSchema;
import ru.nedovizin.vvorders.database.ClientDbSchema.ClientTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.AddressTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.ProductTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.OrderTable;
import ru.nedovizin.vvorders.database.ProductCursorWrapper;

public class ClientLab {

    private static ClientLab sClientLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private String TAG = ".ClientLab";
    private Contragent mCurrentClient;

    public Contragent getCurrentClient() {
        return mCurrentClient;
    }

    public void setCurrentClient(Contragent currentClient) {
        mCurrentClient = currentClient;
    }

    public static ClientLab get(Context context) {
        if (sClientLab == null) {
            sClientLab = new ClientLab(context);
        }
        return sClientLab;
    }

    private ClientLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ClientBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addClient(Contragent c) {
        ContentValues values = getClientValues(c);
        mDatabase.insert(ClientTable.NAME, null, values);
    }

    public void addAddress(Address address) {
        ContentValues values = getAddressValues(address);
        mDatabase.insert(AddressTable.NAME, null, values);
    }

    public void addProduct(Product product) {
        ContentValues values = getProductValues(product);
        mDatabase.insert(ProductTable.NAME, null, values);
    }

    public void addOrder(Order order, List<ProductItem> productItems) {
        ContentValues values = getOrderValues(order);
        mDatabase.insert(OrderTable.NAME, null, values);

        for (ProductItem item: productItems) {
            ContentValues itemValuesalues = getOrderProductItemsValues(order.code, item);
            mDatabase.insert(OrderTable.Cols.Products.NAME, null, itemValuesalues);
        }
    }

    public List<Contragent> getClientsByLikeName(String word) {
        List<Contragent> clients = new ArrayList<>();
        try (ClientCursorWrapper cursor = queryClients(ClientTable.Cols.NAME + " LIKE \'%" + word + "%\'" +
                " AND " + ClientTable.Cols.ACTIVITY + "=\'true\'", null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                clients.add(cursor.getClient());
                cursor.moveToNext();
            }
        }
        return clients;
    }

    public List<Contragent> getClients() {
        List<Contragent> client = new ArrayList<>();
        try (ClientCursorWrapper cursor = queryClients(ClientTable.Cols.ACTIVITY + "=\'true\'", null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Log.d(TAG, "Client.acivity = " + cursor.getClient().activity);
                client.add(cursor.getClient());
                cursor.moveToNext();
            }
        }
        return client;
    }

    public Contragent getClient(String code) {
        try (ClientCursorWrapper cursor = queryClients(ClientTable.Cols.CODE + " = ?", new String[]{code})) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getClient();
        }
    }

    public void updateClient(Contragent client) {
        String code = client.code;
        ContentValues values = getClientValues(client);
        mDatabase.update(ClientTable.NAME, values, ClientTable.Cols.CODE + " = ?", new String[]{code});
    }

    public void updateActivityToFalseAllClients() {
        ContentValues values = new ContentValues();
        values.put(ClientTable.Cols.ACTIVITY, "false");
        mDatabase.update(ClientTable.NAME, values,null, null);
    }

    public void updateActivityToFalseAllProducts() {
        ContentValues values = new ContentValues();
        values.put(ProductTable.Cols.ACTIVITY, "false");
        mDatabase.update(ProductTable.NAME, values,null, null);
    }

    public void updateActivityToFalseAllAddresses() {
        ContentValues values = new ContentValues();
        values.put(AddressTable.Cols.ACTIVITY, "false");
        mDatabase.update(AddressTable.NAME, values,null, null);
    }

    private ClientCursorWrapper queryClients(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ClientTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new ClientCursorWrapper(cursor);
    }

    private AddressCursorWrapper queryAddresses(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                AddressTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new AddressCursorWrapper(cursor);
    }

    private ProductCursorWrapper queryProducts(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ProductTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                "length(name) asc"
        );
        return new ProductCursorWrapper(cursor);
    }

    public List<Address> getAddressesByLikeName(String word) {
        List<Address> addresses = new ArrayList<>();
        Log.d(TAG, "current client: " + getCurrentClient());
        try (AddressCursorWrapper cursor = queryAddresses(AddressTable.Cols.NAME +
                " LIKE \'%" + word + "%\' AND " +
                AddressTable.Cols.CODE + "=\'" + getCurrentClient().code + "\'" +
                " AND " + AddressTable.Cols.ACTIVITY + "=\'true\'", null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Log.d(TAG, "Address.acivity = " + cursor.getAddress().activity);
                addresses.add(cursor.getAddress());
                cursor.moveToNext();
            }
        }
        return addresses;
    }

    public List<Address> getAddressesByClient(Contragent client) {
        List<Address> addresses = new ArrayList<>();
        Log.d(TAG, "current client: " + getCurrentClient());
        try (AddressCursorWrapper cursor = queryAddresses(
                AddressTable.Cols.CODE + "=\'" + client.code + "\'" +
                        " AND " + AddressTable.Cols.ACTIVITY + "=\'true\'",
                null)
        ) {
            Log.d(TAG, "Entry in cycle...");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Log.d(TAG, "Address.acivity = " + cursor.getAddress().activity);
                addresses.add(cursor.getAddress());
                cursor.moveToNext();
            }
        }
        Log.d(TAG, "count adresses: " + addresses.size());
        return addresses;
    }

    public List<Product> getProductsByLikeWords(String word) {
        String[] words = word.trim().split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = "%" + words[i] + "%";
        }
        word = TextUtils.join("", words);
        List<Product> products = new ArrayList<>();
        Log.d(TAG, "input words: " + word);
        try (ProductCursorWrapper cursor = queryProducts (
                ProductTable.Cols.NAME + " LIKE \'" + word + "\'" +
                        " AND " + ProductTable.Cols.ACTIVITY + "=\'true\'",
                null
            )
        ) {
            cursor.moveToFirst();
            int count = 0;
            while (!cursor.isAfterLast()) {
                Log.d(TAG, "Product.acivity = " + cursor.getProduct().activity);
                products.add(cursor.getProduct());
                cursor.moveToNext();
                count++;
                if (count > 2) break;
            }
        }
        Log.d(TAG, "count products: " + products.size());
        return products;
    }

    private static ContentValues getClientValues(Contragent client) {
        ContentValues values = new ContentValues();
        client.activity = "true";
        values.put(ClientTable.Cols.NAME, client.name);
        values.put(ClientTable.Cols.CODE, client.code);
        values.put(ClientTable.Cols.ACTIVITY, client.activity);
        return values;
    }

    private static ContentValues getAddressValues(Address address) {
        ContentValues values = new ContentValues();
        address.activity = "true";
        values.put(AddressTable.Cols.NAME, address.name);
        values.put(AddressTable.Cols.CODE, address.code);
        values.put(AddressTable.Cols.ACTIVITY, address.activity);
        return values;
    }

    private static ContentValues getProductValues(Product product) {
        ContentValues values = new ContentValues();
        product.activity = "true";
        values.put(ProductTable.Cols.NAME, product.name);
        values.put(ProductTable.Cols.CODE, product.code);
        values.put(ProductTable.Cols.WEIGHT, product.weight);
        values.put(ProductTable.Cols.ACTIVITY, product.activity);
        return values;
    }

    private static ContentValues getOrderValues(Order order) {
        ContentValues values = new ContentValues();
        order.activity = "true";
        values.put(OrderTable.Cols.CODE, order.code);
        values.put(OrderTable.Cols.CLIENT, order.client);
        values.put(OrderTable.Cols.DATE, order.date);
        values.put(OrderTable.Cols.ACTIVITY, order.activity);
        return values;
    }

    private static ContentValues getOrderProductItemsValues(String code, ProductItem productItem) {
        ContentValues values = new ContentValues();
        values.put(OrderTable.Cols.Products.CODE, code);
        values.put(OrderTable.Cols.Products.PRODUCT, productItem.product.name);
        values.put(OrderTable.Cols.Products.QUANTITY, productItem.quantity);
        return values;
    }
}
