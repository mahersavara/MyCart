package hanu.a2_1901040122;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import hanu.a2_1901040122.Adapter.OrderAdapter;
import hanu.a2_1901040122.Adapter.ProductAdapter;
import hanu.a2_1901040122.MainActivity;
import hanu.a2_1901040122.data.OrderHelper;
import hanu.a2_1901040122.models.Order.Order;
import hanu.a2_1901040122.models.Product.Product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    OrderAdapter adapter;
    List<Order> orders = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String value = intent.getStringExtra("key"); //if it's a string you stored.


        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);
        /**
         * !Implement this product system
         * */
        OrderHelper orderHelper = new OrderHelper(CartActivity.this);
        SQLiteDatabase db = orderHelper.getWritableDatabase();

        // manipulate db
        Cursor result = db.rawQuery("SELECT product_id,image,name,price,quantity FROM orders", null);

        int idIndex = result.getColumnIndex("product_id");
        int nameIndex = result.getColumnIndex("name");
        int imageIndex = result.getColumnIndex("image");
        int priceIndex = result.getColumnIndex("price");
        int quantityIndex = result.getColumnIndex("quantity");

        while(result.moveToNext()) {
            int id = result.getInt(idIndex);
            String name = result.getString(nameIndex);
            String image = result.getString(imageIndex);
            Double price = result.getDouble(priceIndex);
            int quantity = result.getInt(quantityIndex);
            Order order = new Order(id,image,name,price,quantity);
            orders.add(order);
        }






//        Log.i("hello",products.toString());
        recyclerView = (RecyclerView) findViewById(R.id.cartList);




        adapter = new OrderAdapter(orders, CartActivity.this);
//        Log.i("aDAPTER",adapter.toString());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CartActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }



    // !Insist the main menu
    // method to inflate the options menu when
    // the user opens the menu for the first time
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //! Get Back to Main ActivityButton
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
//! Get Back to Main ActivityButton
        //noinspection SimplifiableIfStatement

//        if (id == R.id.) {
////            Log.i("linhhh","Clicked");
//            Intent myIntent = new Intent(CartActivity.this, MainActivity.class);
//            myIntent.putExtra("key", "BackToMain");
//            CartActivity.this.startActivity(myIntent);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Double Total_Price =(Double) 0.0;
        TextView total_Price_view = findViewById(R.id.txtTotalPrice);
        for (int i=0;i<orders.size();i++) {
            Total_Price=Total_Price + orders.get(i).getPrice()*orders.get(i).getQuantity();
        }
        NumberFormat formatter = new DecimalFormat("#,###");
        String formattedNumber = formatter.format(Total_Price);
        total_Price_view.setText("đ̳ "+formattedNumber);
    }
}




