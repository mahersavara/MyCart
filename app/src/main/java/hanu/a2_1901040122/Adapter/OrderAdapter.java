package hanu.a2_1901040122.Adapter;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import hanu.a2_1901040122.CartActivity;
import hanu.a2_1901040122.data.OrderHelper;
import hanu.a2_1901040122.models.Constants.Constants;
import hanu.a2_1901040122.models.Order.Order;
import hanu.a2_1901040122.models.Product.Product;
import hanu.a2_1901040122.R;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    //save product
    List<Order> orders = new ArrayList<>();
    // Lưu Context để dễ dàng truy cập
    private Context mContext;

    /**
     * Lớp nắm giữ cấu trúc view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View cartitem;
        public ImageView OrderImageView;
        public TextView orderTitle;
        public TextView orderPrice;
        public TextView orderSumPrice;
        public TextView orderQuantity;
        public ImageButton btnAdd;
        public ImageButton btnSub;


        public ViewHolder(View cartItem) {
            super(cartItem);
            cartitem = cartItem;
            OrderImageView = cartItem.findViewById(R.id.OrderImageView);
            orderTitle = cartItem.findViewById(R.id.txtOrderTitle);
            orderPrice = cartItem.findViewById(R.id.txtOrderPrice);
            orderQuantity= cartItem.findViewById(R.id.txtOrderQuantity);
            orderSumPrice = cartItem.findViewById(R.id.txtOrderSumPrice);
            btnAdd = cartItem.findViewById(R.id.btnOrderAdd);
            btnSub = cartItem.findViewById(R.id.btnOrderSub);

        }
    }

//Tao object OrderAddapter
    public OrderAdapter(List _orders, Context mContext) {
        this.orders = _orders;
        this.mContext = mContext;
    }



    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        // Nạp layout cho View biểu diễn phần tử Cart_Item
        View orderView =
                inflater.inflate(R.layout.cart_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(orderView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i("OrderApdapter",orders.get(position).toString());
        Order order = orders.get(position);

//        !TODO: IMPLEMENT THIS orders SYSTEM

        holder.orderTitle.setText(order.getName());
        NumberFormat formatter = new DecimalFormat("#,###");
        String formattedNumber = formatter.format(order.getPrice());
        holder.orderPrice.setText("đ̳ "+formattedNumber);
        holder.orderQuantity.setText(order.getQuantity().toString());
        formattedNumber=formatter.format((order.getPrice()*order.getQuantity()));
        holder.orderSumPrice.setText("đ̳ "+formattedNumber);
//        Total_Price=Total_Price + orders.get(i).getPrice()*orders.get(i).getQuantity();
//        formattedNumber = formatter.format(Total_Price);
//        total_Price_view.setText("đ̳ "+formattedNumber);
//        holder.OrderImageView.setImageResource(R.drawable.image);
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
                Bitmap bitmap = downloadImage(order.getThumbnail());
                if (bitmap !=null ) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.OrderImageView.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        });

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseOrderItem(order,mContext);
                TextView sum =(( CartActivity) mContext).findViewById(R.id.txtTotalPrice);
                Double Total_Price =(Double) 0.0;
                for (int i=0;i<orders.size();i++) {
                    Total_Price=Total_Price + orders.get(i).getPrice()*orders.get(i).getQuantity();
                }
                NumberFormat formatter = new DecimalFormat("#,###");
                String formattedNumber = formatter.format(Total_Price);
                sum.setText("đ̳ "+formattedNumber);
                notifyItemChanged(position);
            }
        });
        holder.btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseOrderItem(order,mContext);
                TextView sum =(( CartActivity) mContext).findViewById(R.id.txtTotalPrice);
                Double Total_Price =(Double) 0.0;
                for (int i=0;i<orders.size();i++) {
                    Total_Price=Total_Price + orders.get(i).getPrice()*orders.get(i).getQuantity();
                }
                NumberFormat formatter = new DecimalFormat("#,###");
                String formattedNumber = formatter.format(Total_Price);
                sum.setText("đ̳ "+formattedNumber);
                notifyItemChanged(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return orders.size();
    }


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



    private boolean increaseOrderItem(Order order, Context mContext) {
        OrderHelper orderHelper = new OrderHelper(mContext);
        SQLiteDatabase db = orderHelper.getWritableDatabase();
        order.setQuantity(order.getQuantity()+1);
        String strUpdate = "UPDATE orders SET quantity = quantity +1 WHERE product_id = "+ order.getId();
        db.execSQL(strUpdate);
        db.close();
        return true;
    }


private boolean decreaseOrderItem(Order order,Context mContext) {
    OrderHelper orderHelper = new OrderHelper(mContext);
    SQLiteDatabase db = orderHelper.getWritableDatabase();
    if(order.getQuantity()==1) {
        //quantity =1 -> xoa
        String strDelete ="DELETE from orders WHERE product_id= "+order.getId();
        db.execSQL(strDelete);
        orders.remove(order);
    } else {
        order.setQuantity(order.getQuantity()-1);
        String strUpdate = "UPDATE orders SET quantity = quantity -1 WHERE product_id = "+ order.getId();
        db.execSQL(strUpdate);
    }
    db.close();
    return true;
}



}
