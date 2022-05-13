package hanu.a2_1901040122.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class OrderHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "orders.db";
    private static final int DB_VERSION = 1;

    public OrderHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
// create table: friends
        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "image TEXT, " +
                "name TEXT, " +
                "price REAL, " +
                "product_id INTEGER UNIQUE, " +
                "quantity INTEGER)");
// other tables here
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.w("My Orders", "My Orders: upgrading DB; dropping/recreating tables.");
    // drop old tables
        db.execSQL("DROP TABLE IF EXISTS friends");
    // other tables here
    // create new tables
        onCreate(db);
    }
}
