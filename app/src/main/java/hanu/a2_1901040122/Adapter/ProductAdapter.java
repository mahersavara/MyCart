package hanu.a2_1901040122.Adapter;

import android.content.Context;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import hanu.a2_1901040122.R;
import hanu.a2_1901040122.models.Constants.Constants;
import hanu.a2_1901040122.models.Product.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {
    //save product
    List<Product> products = new ArrayList<>();
    List<Product> fullProducts = new ArrayList<>();
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

            //Xử lý khi nút Chi tiết được bấm
            //! co the implement Recycle View them de them function
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),
                            productTitle.getText() +" | "
                                    + " Demo function", Toast.LENGTH_SHORT)
                            .show();
                }
            });
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

}
