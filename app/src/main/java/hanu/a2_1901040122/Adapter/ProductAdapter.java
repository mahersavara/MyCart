package hanu.a2_1901040122.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import hanu.a2_1901040122.MainActivity;
import hanu.a2_1901040122.R;
import hanu.a2_1901040122.data.OrderHelper;
import hanu.a2_1901040122.models.Constants.Constants;
import hanu.a2_1901040122.models.Order.Order;
import hanu.a2_1901040122.models.Product.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {
    //save product
    List<Product> products = new ArrayList<>();
    List<Product> fullProducts = new ArrayList<>();
    List<Order> orders= new ArrayList<>();
    // Lưu Context để dễ dàng truy cập
    private Context mContext;

    /**
     * Lớp nắm giữ cấu trúc view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View itemview;
        public ImageView productImageView;
        public TextView productTitle;
        public TextView productPrice;
        public ImageButton btnAdd;


        public ViewHolder(View itemView) {
            super(itemView);
            itemview = itemView;
            productImageView = itemView.findViewById(R.id.ProductImageView);
            productTitle = itemView.findViewById(R.id.txtTitle);
            productPrice = itemView.findViewById(R.id.txtPrice);
            btnAdd = itemView.findViewById(R.id.btnCart);
            int pos = itemview.getId();


            //Xử lý khi nút Chi tiết được bấm
            //! co the implement Recycle View them de them function
//            btnAdd.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(view.getContext(),
//                            pos +" | "
//                                    + " Demo function", Toast.LENGTH_SHORT)
//                            .show();
//
//
//
//                }
//            });
        }
    }


    public ProductAdapter(List _products, Context mContext) {
        this.products = _products;
        fullProducts = new ArrayList<>(_products);
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Nạp layout cho View biểu diễn phần tử sinh viên
        View productView =
                inflater.inflate(R.layout.product_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(productView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i("AAA",products.get(position).toString());
        Product product = products.get(position);

        holder.productTitle.setText(product.getName());
        NumberFormat formatter = new DecimalFormat("#,###");
        String formattedNumber = formatter.format(product.getPrice());
        holder.productPrice.setText("đ̳ "+formattedNumber);
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addprotuctToCart(product,mContext);
//             holder.btnAdd.start
                holder.btnAdd.animate().setDuration(500).rotationBy(360f).start();
                Toast.makeText(mContext, "Product added",Toast.LENGTH_SHORT).show();
//                Snackbar.make(mContext,"Addedd",Snackbar.LENGTH_SHORT).setDuration(1000).show();
            }
        });
//        holder.productImageView.setImageResource(R.drawable.image);

//            URL url = new URL(product.getThumbnail());
//            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            holder.productImageView.setImageBitmap(bmp);
//        holder.productImageView.setImageBitmap(product.loadImage(product.getThumbnail()));

        //?Task Chung
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        //Showing Image
        Constants.executor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImage(product.getThumbnail());
                if (bitmap !=null ) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.productImageView.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        });



    }
    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullProducts);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Product product : fullProducts) {
                    if (product.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(product);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            products.clear();
            products.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };






    //!Method to Download Image
    private Bitmap downloadImage(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); connection.connect();
            InputStream is = connection.getInputStream(); Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } catch (MalformedURLException e) { e.printStackTrace();
        } catch (IOException e) { e.printStackTrace();
        }
        return null;
    }


    //!TODO:Additem to cart
    private boolean addprotuctToCart(Product product, Context mContext) {
        boolean isAdded = false;
        int pos = -1;
        //save in db
        //connect db
        OrderHelper orderHelper = new OrderHelper(mContext);
        SQLiteDatabase db = orderHelper.getWritableDatabase();
        // manipulate db

        String Query = "Select * from orders where product_id = " + product.getId();
        Cursor cursor = db.rawQuery(Query,null);
        if(cursor.getCount() <= 0) {
            cursor.close();
        } else {
            isAdded=true;
            cursor.close();
        }

//        for( int i =0; i< orders.size();i++){
//            if(orders.get(i).getId()== product.getId()) {
//                pos=i;
//                Log.i("Trung`","Trung`");
//                isAdded = true;
//                //thuc hien update
//            }
//        }
        if (!isAdded) {
            Order order = new Order(product.getId(),product.getThumbnail(),product.getName(),product.getPrice(),1);
            orders.add(order);

            String sql = "INSERT INTO orders(name,image, price, quantity,product_id) VALUES (?,?, ?, ?, ?)";
            SQLiteStatement statement = db.compileStatement(sql);
            // bind params
            statement.bindString(1, product.getName());
            statement.bindString(2, product.getThumbnail());
            //!TODO : CHECK LAI CAI PRICE NAY
            statement.bindDouble(3,product.getPrice());
//     !TODO   if() -> chinh lai cai quantity nay ( ca 3 cai tren)
            statement.bindLong(4,1);
            statement.bindLong(5,product.getId());

            // run query
            long id = statement.executeInsert(); // auto generated id
            // close connection
            return id > 0;
        } else {
            String strUpdate = "UPDATE orders SET quantity = quantity +1 WHERE product_id = "+ product.getId();
            db.execSQL(strUpdate);
        }

        db.close();

        // create statement

        return true;
    }

//   private void Onclick(View v,int pos) {
//       Product product = products.get(pos);
////       check if exit
//       Order order = new Order(product.getId(),product.getThumbnail(),product.getName(),product.getPrice(),"1")
//
//
//   }



}
