package edu.sjsu.sase.android.spoleralert;

public final class GroceryDBSchema {
    private GroceryDBSchema() {}

    public static class GroceryDBColumns {

        public static final String TABLE_NAME = "groceries";
        public static final String ID = "id";
        public static final String GROCERY_NAME = "name";
        public static final String FOOD_GROUP = "food_group";
        public static final String QUANTITY = "quantity";
        public static final String POUNDS = "pounds";
        public static final String OUNCES = "ounces";
        public static final String PRICE = "price";
        public static final String FREEZER_STATUS = "freezer_status";
        public static final String EXPIRATION_DATE = "expiration_date";
        public static final String EXPIRATION_STATUS = "expiration_status";
    }

}
