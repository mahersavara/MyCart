package hanu.a2_1901040122;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
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

import hanu.a2_1901040122.models.Constants.Constants;
import hanu.a2_1901040122.models.Product.Product;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        //?Load Product api
        String url = "https://mpr-cart-api.herokuapp.com/products";
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
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
                            List<Product> products = new ArrayList<>();
                            Product product;



//                            Log.i("rootttt", root.toString());
//                            Toast.makeText(MainActivity.this, root.toString(), Toast.LENGTH_SHORT).show();

                            for (int i = 0; i < root.length(); i++) {
                                JSONObject jsonProduct = root.getJSONObject(i);
                                int id = jsonProduct.getInt("id");
                                String thumbnail = jsonProduct.getString("thumbnail");
                                String name = jsonProduct.getString("name");
                                double unitPrice = jsonProduct.getDouble("unitPrice");
                                product = new Product(id, thumbnail, name, unitPrice);
//                                Toast.makeText(MainActivity.this, product.toString(), Toast.LENGTH_SHORT).show();
                                products.add(product);
                            }

                            for(int i= 0; i< products.size();i++){
                                Log.i("AAA",products.get(i).toString());
                            }
                            //
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                });

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


}

