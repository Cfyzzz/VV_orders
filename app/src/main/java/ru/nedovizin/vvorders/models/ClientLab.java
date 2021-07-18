package ru.nedovizin.vvorders.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.nedovizin.vvorders.ProductItem;
import ru.nedovizin.vvorders.database.AddressCursorWrapper;
import ru.nedovizin.vvorders.database.ClientBaseHelper;
import ru.nedovizin.vvorders.database.ClientCursorWrapper;
import ru.nedovizin.vvorders.database.ClientDbSchema;
import ru.nedovizin.vvorders.database.ClientDbSchema.AddressTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.ClientTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.OrderTable;
import ru.nedovizin.vvorders.database.ClientDbSchema.ProductTable;
import ru.nedovizin.vvorders.database.OrderCursorWrapper;
import ru.nedovizin.vvorders.database.ProductCursorWrapper;
import ru.nedovizin.vvorders.database.ProductItemCursorWrapper;
import ru.nedovizin.vvorders.database.SettingsConnectCursorWrapper;

/** Вспомогательный класс для работы с БД
 *
 */
public class ClientLab {

    private static ClientLab sClientLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private String TAG = ".ClientLab";
    private Contragent mCurrentClient;

    /** Получить текущего клиента
     *
     * @return
     */
    public Contragent getCurrentClient() {
        return mCurrentClient;
    }

    /** Установить текущего клиента
     *
     * @param currentClient
     */
    public void setCurrentClient(Contragent currentClient) {
        mCurrentClient = currentClient;
    }

    /** Получить объект {@code ClientLab}
     *
     * @param context
     * @return
     */
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

    /** Добавить контрагента в БД
     *
     * @param c Контрагент
     */
    public void addClient(Contragent c) {
        c.activity = "true";
        ContentValues values = getClientValues(c);
        mDatabase.insert(ClientTable.NAME, null, values);
    }

    /** Добавить адресс в БД
     *
     * @param address
     */
    public void addAddress(Address address) {
        address.activity = "true";
        ContentValues values = getAddressValues(address);
        mDatabase.insert(AddressTable.NAME, null, values);
    }

    /** Добавить продукт в БД
     *
     * @param product
     */
    public void addProduct(Product product) {
        product.activity = "true";
        ContentValues values = getProductValues(product);
        mDatabase.insert(ProductTable.NAME, null, values);
    }

    /** Добавить заявку в БД
     *
     * @param order Объект заявка
     * @param productItems Продукты с количеством в заявке
     */
    public void addOrder(Order order, List<ProductItem> productItems) {
        order.activity = "true";
        ContentValues values = getOrderValues(order);
        mDatabase.insert(OrderTable.NAME, null, values);

        for (ProductItem item : productItems) {
            ContentValues itemValuesalues = getOrderProductItemsValues(order.code, item);
            mDatabase.insert(OrderTable.Cols.Products.NAME, null, itemValuesalues);
        }
    }

    /** Получить действующих контрагентов согласно шаблону
     *
     * @param word Шаблон, по которому ищутся контрагенты
     * @return Список контрагентов
     */
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

    /** Получить всех действующих контрагентов
     *
     * @return
     */
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

    /** Получить список имен контрагентов из заявок на дату {@code d}
     *
     * @param d Рабочая дата
     * @return Список имен контрагентов
     */
    public List<String> getClientsByDate(Date d) {
        List<String> clients = new ArrayList<>();
        String date = DateToString(d);
        try (OrderCursorWrapper cursor = queryOrders(OrderTable.Cols.DATE + "=\'" + date + "\'")) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                clients.add(cursor.getOrder().client);
                cursor.moveToNext();
            }
        }
        return clients;
    }

    /** Получить заявку по коду {@code code}
     *
     * @param code
     * @return
     */
    public Order getOrder(String code) {
        try (OrderCursorWrapper cursor = queryOrders(OrderTable.Cols.CODE + "= \'" + code + "\'")) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getOrder();
        }
    }

    /** Получить активные заявки на дату {@code d}
     *
     * @param d Рабочая дата
     * @return Список заявок
     */
    public List<Order> getOrdersByDate(Date d) {
        List<Order> orders = new ArrayList<>();
        String date = DateToString(d).split("T")[0];
        Log.d(TAG, "date: " + date);
        try (OrderCursorWrapper cursor = queryOrders(OrderTable.Cols.DATE + " LIKE \'" + date + "%\'"
                + " AND " + OrderTable.Cols.ACTIVITY + " = \'true\'")) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                orders.add(cursor.getOrder());
                cursor.moveToNext();
            }
        }
        return orders;
    }

    /** Получить продукты завки по её коду {@code orderId}
     *
     * @param orderId Код заявки
     * @return Список продуктов по заявке
     */
    public List<ProductItem> getProductsByOrderId(String orderId) {
        List<ProductItem> productItems = new ArrayList<>();
        try (ProductItemCursorWrapper cursor = queryProductItems(
                OrderTable.Cols.Products.pCols.CODE + " = \'" + orderId + "\'")) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                // TODO - Оптимизировать через единый запрос
                ProductItem productItem = cursor.getProductItem();
                productItem.product = getProductsByLikeWords(productItem.product.name).get(0);
                productItems.add(productItem);
                cursor.moveToNext();
            }
        }
        return productItems;
    }

    /** Получть контрагента по коду {@code coe}
     *
     * @param code Код контрагента
     * @return Контрагент
     */
    public Contragent getClient(String code) {
        try (ClientCursorWrapper cursor = queryClients(ClientTable.Cols.CODE + " = ?", new String[]{code})) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getClient();
        }
    }

    /** Получить настройки соединения из БД
     *
     * @return Настроки соединения в формате {@code SettingsConnect}
     */
    public SettingsConnect getSettingsConnect() {
        SettingsConnect settingsConnect = null;
        try (SettingsConnectCursorWrapper cursor = querySettingsConnect(null)) {
            if (cursor.moveToFirst()) {
                settingsConnect = cursor.getSettings();
                cursor.moveToNext();
            } else {
                settingsConnect = new SettingsConnect("http://localhost", "Empty", "");
            }
        }
        return settingsConnect;
    }

    /** Заисать настройки соединения в БД
     *
     * @param settingsConnect Настройки соединения
     */
    public void setSettingsConnect(SettingsConnect settingsConnect) {
        ContentValues values = getSettingsConnectValues(settingsConnect);
        mDatabase.delete(ClientDbSchema.SettingsTable.NAME, null, null);
        mDatabase.insert(ClientDbSchema.SettingsTable.NAME, null, values);
    }

    /** Обновить заявку в БД
     *
     * @param order Заявка
     */
    public void updateOrder(Order order) {
        ContentValues values = getOrderValues(order);
        mDatabase.insert(OrderTable.NAME, null, values);
    }

    /** Обновить контрагента в БД
     *
     * @param client Контрагент
     */
    public void updateClient(Contragent client) {
        String code = client.code;
        ContentValues values = getClientValues(client);
        mDatabase.update(ClientTable.NAME, values, ClientTable.Cols.CODE + " = ?", new String[]{code});
    }

    /** Всех контрагентов сделать неактивными в БД
     *
     */
    public void updateActivityToFalseAllClients() {
        ContentValues values = new ContentValues();
        values.put(ClientTable.Cols.ACTIVITY, "false");
        mDatabase.update(ClientTable.NAME, values, null, null);
    }

    /** Все продукты в БД сделать неактивными
     *
     */
    public void updateActivityToFalseAllProducts() {
        ContentValues values = new ContentValues();
        values.put(ProductTable.Cols.ACTIVITY, "false");
        mDatabase.update(ProductTable.NAME, values, null, null);
    }

    /** Все адреса в БД сделать неактивными
     *
     */
    public void updateActivityToFalseAllAddresses() {
        ContentValues values = new ContentValues();
        values.put(AddressTable.Cols.ACTIVITY, "false");
        mDatabase.update(AddressTable.NAME, values, null, null);
    }

    /** Удалить заявку
     *
     * @param order Заявка
     */
    public void deleteOrder(Order order) {
        clearProducts(order);
        mDatabase.delete(OrderTable.NAME,
                OrderTable.Cols.CODE + " = \'" + order.code + "\'",
                null);
    }

    /** Очистить таблицу продуктов по заявке
     *
     * @param order Заявка
     */
    public void clearProducts(Order order) {
        mDatabase.delete(OrderTable.Cols.Products.NAME,
                OrderTable.Cols.Products.pCols.CODE + "=\'" + order.code + "\'",
                null);
    }

    /** Сбросить активность заявки в БД
     *
     * @param order Заявка
     */
    public void inactivateOrder(Order order) {
        order.activity = "false";
        ContentValues values = getOrderValues(order);
        mDatabase.update(OrderTable.NAME,
                values,
                OrderTable.Cols.CODE + " = \'" + order.code + "\'",
                null);
    }

    /** Установить активность заявки в БД
     *
     * @param order
     */
    public void activateOrder(Order order) {
        order.activity = "true";
        ContentValues values = getOrderValues(order);
        mDatabase.update(OrderTable.NAME,
                values,
                OrderTable.Cols.CODE + " = \'" + order.code + "\'",
                null);
    }

    /** Получить адреса по шаблону из БД
     *
     * @param word Шаблон
     * @return Список адресов
     */
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

    /** Получить адреса контрагента из БД
     *
     * @param client Контрагент
     * @return Список адресов
     */
    public List<Address> getAddressesByClient(Contragent client) {
        List<Address> addresses = new ArrayList<>();
        try (AddressCursorWrapper cursor = queryAddresses(
                AddressTable.Cols.CODE + "=\'" + client.code + "\'" +
                        " AND " + AddressTable.Cols.ACTIVITY + "=\'true\'",
                null)
        ) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                addresses.add(cursor.getAddress());
                cursor.moveToNext();
            }
        }
        return addresses;
    }

    /** Получить продукты по шаблону из БД
     *
     * @param word Шаблон для сопоставления наименования продуктов
     * @return Список продуктов
     */
    public List<Product> getProductsByLikeWords(String word) {
        String[] words = word.trim().split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = "%" + words[i] + "%";
        }
        word = TextUtils.join("", words);
        List<Product> products = new ArrayList<>();
        try (ProductCursorWrapper cursor = queryProducts(
                ProductTable.Cols.NAME + " LIKE \'" + word + "\'" +
                        " AND " + ProductTable.Cols.ACTIVITY + "=\'true\'",
                null
        )
        ) {
            cursor.moveToFirst();
            int count = 0;
            while (!cursor.isAfterLast()) {
                products.add(cursor.getProduct());
                cursor.moveToNext();
                count++;
                if (count > 2) break;
            }
        }
        return products;
    }

    /** Преобразовать дату в строку по единому правилу
     *
     * @param date Дата
     * @return Строка с датой в едином формате
     */
    public String DateToString(Date date) {
        return DateFormat.format("yyyy-MM-ddThh:mm:ss", date).toString();
    }

    // БЛОК СЛУЖЕБНЫХ МЕТОДОВ

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

    private OrderCursorWrapper queryOrders(String whereClause) {
        Cursor cursor = mDatabase.query(
                OrderTable.NAME,
                null,
                whereClause,
                null,
                null,
                null,
                null
        );
        return new OrderCursorWrapper(cursor);
    }

    private ProductItemCursorWrapper queryProductItems(String whereClause) {
        Cursor cursor = mDatabase.query(
                OrderTable.Cols.Products.NAME,
                null,
                whereClause,
                null,
                null,
                null,
                null
        );
        return new ProductItemCursorWrapper(cursor);
    }

    public SettingsConnectCursorWrapper querySettingsConnect(String whereClause) {
        Cursor cursor = mDatabase.query(
                ClientDbSchema.SettingsTable.NAME,
                null,
                whereClause,
                null,
                null,
                null,
                null
        );
        return new SettingsConnectCursorWrapper(cursor);
    }

    private static ContentValues getClientValues(Contragent client) {
        ContentValues values = new ContentValues();
        values.put(ClientTable.Cols.NAME, client.name);
        values.put(ClientTable.Cols.CODE, client.code);
        values.put(ClientTable.Cols.ACTIVITY, client.activity);
        return values;
    }

    private static ContentValues getAddressValues(Address address) {
        ContentValues values = new ContentValues();
        values.put(AddressTable.Cols.NAME, address.name);
        values.put(AddressTable.Cols.CODE, address.code);
        values.put(AddressTable.Cols.ACTIVITY, address.activity);
        return values;
    }

    private static ContentValues getProductValues(Product product) {
        ContentValues values = new ContentValues();
        values.put(ProductTable.Cols.NAME, product.name);
        values.put(ProductTable.Cols.CODE, product.code);
        values.put(ProductTable.Cols.WEIGHT, product.weight);
        values.put(ProductTable.Cols.ACTIVITY, product.activity);
        return values;
    }

    private static ContentValues getOrderValues(Order order) {
        ContentValues values = new ContentValues();
        values.put(OrderTable.Cols.CODE, order.code);
        values.put(OrderTable.Cols.CLIENT, order.client);
        values.put(OrderTable.Cols.ADDRESS, order.address);
        values.put(OrderTable.Cols.DATE, order.date);
        values.put(OrderTable.Cols.ACTIVITY, order.activity);
        values.put(OrderTable.Cols.STATUS, order.status);
        return values;
    }

    private static ContentValues getOrderProductItemsValues(String code, ProductItem productItem) {
        ContentValues values = new ContentValues();
        values.put(OrderTable.Cols.Products.pCols.CODE, code);
        values.put(OrderTable.Cols.Products.pCols.PRODUCT, productItem.product.name);
        values.put(OrderTable.Cols.Products.pCols.QUANTITY, productItem.quantity);
        return values;
    }

    private static ContentValues getSettingsConnectValues(SettingsConnect settingsConnect) {
        ContentValues values = new ContentValues();
        values.put(ClientDbSchema.SettingsTable.Cols.HOST, settingsConnect.getHost());
        values.put(ClientDbSchema.SettingsTable.Cols.LOGIN, settingsConnect.getLogin());
        values.put(ClientDbSchema.SettingsTable.Cols.PASSWORD, settingsConnect.getPassword());
        return values;
    }
}
