package ru.nedovizin.vvorders.database;

public class ClientDbSchema {
    public static final class ClientTable {
        public static final String NAME = "clients";
        public static final class Cols {
            public static final String CODE = "code";
            public static final String NAME = "name";
        }
    }

    public static final class AddressTable {
        public static final String NAME = "addresses";
        public static final class Cols {
            public static final String CODE = "code";
            public static final String NAME = "name";
        }
    }

    public static final class ProductTable {
        public static final String NAME = "products";
        public static final class Cols {
            public static final String CODE = "code";
            public static final String NAME = "name";
            public static final String WEIGHT = "weight";
        }
    }
}
