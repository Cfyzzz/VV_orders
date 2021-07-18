package ru.nedovizin.vvorders.database;

/** Вспомогательный класс для описания структуры полей БД
 *
 */
public class ClientDbSchema {
    public static final class ClientTable {
        public static final String NAME = "clients";
        public static final class Cols {
            public static final String CODE = "code";
            public static final String NAME = "name";
            public static final String ACTIVITY = "activity";
        }
    }

    public static final class AddressTable {
        public static final String NAME = "addresses";
        public static final class Cols {
            public static final String CODE = "code";
            public static final String NAME = "name";
            public static final String ACTIVITY = "activity";
        }
    }

    public static final class ProductTable {
        public static final String NAME = "products";
        public static final class Cols {
            public static final String CODE = "code";
            public static final String NAME = "name";
            public static final String WEIGHT = "weight";
            public static final String ACTIVITY = "activity";
        }
    }

    public static final class OrderTable {
        public static final String NAME = "orders";
        public static final class Cols {
            public static final String CODE = "code";
            public static final String DATE = "date";
            public static final String ACTIVITY = "activity";
            public static final String CLIENT = "client_name";
            public static final String ADDRESS = "address";
            public static final String STATUS = "status";
            public static final class Products {
                public static final String NAME = "order_products";
                public static final class pCols {
                    public static final String CODE = "code_order"; // код заявки
                    public static final String PRODUCT = "product_name";
                    public static final String QUANTITY = "quantity";
                }
            }
        }
    }

    public static final class SettingsTable {
        public static final String NAME = "settings";
        public static final class Cols {
            public static final String HOST = "host";
            public static final String LOGIN = "login";
            public static final String PASSWORD = "password";
        }
    }
}
