package hanu.a2_1901040122;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hanu.a2_1901040122.Adapter.ProductAdapter;
import hanu.a2_1901040122.data.OrderHelper;
import hanu.a2_1901040122.models.Constants.Constants;
import hanu.a2_1901040122.models.Product.Product;



public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProductAdapter adapter;
    List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        recyclerView = findViewById(R.id.ProductList);




        //?Load Product api
        String url = "https://mpr-cart-api.herokuapp.com/products";

        //?Task Chung
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        //Getting api
        Constants.executor.execute(new Runnable() {
            @Override
            public void run() {
                //connect api
                /* ?Loading the api*/
                String json = loadJSON(url);


                //?Manipulate Ui with object from api
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (json == null) {
                            Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //!read file as json
                        //parse json ->  {Product}
                        try {
                            // ! this will detail what subject will get into!
                            // #JSONARRAY FOR ARRAY TYPE AND JSON OBJECT FOR OBJECT TYPE.
                            JSONArray root = new JSONArray(json);


//                            Log.i("rootttt", root.toString());
//                            Toast.makeText(MainActivity.this, root.toString(), Toast.LENGTH_SHORT).show();

                            for (int i = 0; i < root.length(); i++) {
                                JSONObject jsonProduct = root.getJSONObject(i);
                                int id = jsonProduct.getInt("id");
                                String thumbnail = jsonProduct.getString("thumbnail");
                                String name = jsonProduct.getString("name");
                                double unitPrice = jsonProduct.getDouble("unitPrice");
                                Product product = new Product(id, thumbnail, name, unitPrice);
//                                Toast.makeText(MainActivity.this, product.toString(), Toast.LENGTH_SHORT).show();
                                products.add(product);
                            }
//                            Log.i("AAA",products.toString());
//                            for(int i= 0; i< products.size();i++){
//                                Log.i("AAA",products.get(i).toString());
//                            }
                            //#Oke

                            adapter = new ProductAdapter(products, MainActivity.this);

//                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this,2);

                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(gridLayoutManager);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                });

            }
        });


        SearchView searchView = findViewById(R.id.searchProduct);
        ImageButton searchBtn = findViewById(R.id.search_button);
//        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Toast.makeText(MainActivity.this,  searchView.getQuery().toString(), Toast.LENGTH_SHORT).show();
                adapter.getFilter().filter(searchView.getQuery().toString());
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                try {
//                    Thread.sleep(5000);
//                    adapter.getFilter().filter(newText);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                return false;
            }
        });

    }

    //Method to load restApi

    public String loadJSON(String link) {
        URL url;
        HttpURLConnection urlConnection;
        try {
            url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream is = urlConnection.getInputStream();
            Scanner sc = new Scanner(is);
            StringBuilder result = new StringBuilder();
            String line;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                result.append(line);
            }
            return result.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




    // !Insist the main menu
    // method to inflate the options menu when
    // the user opens the menu for the first time
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnCart) {
//            Log.i("linhhh","Clicked");
            Intent myIntent = new Intent(MainActivity.this,CartActivity.class);
            myIntent.putExtra("key", products.toString());
            MainActivity.this.startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }






}

