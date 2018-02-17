package com.example.z7n.foodtruck.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.z7n.foodtruck.Product;
import com.example.z7n.foodtruck.R;

import java.util.ArrayList;

/**
 * Created by z7n on 2/17/2018.

 */

public class MenuFragment extends Fragment {

    private View parentView;
    private ProductAdapter productAdapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.menu_fragment,container,false);

        recyclerView = parentView.findViewById(R.id.listView_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        productAdapter = new ProductAdapter(getFakeProducts());
        recyclerView.setAdapter(productAdapter);

        return parentView;
    }

    private ArrayList<Product> getFakeProducts(){
        ArrayList<Product> products = new ArrayList<>();

        for (int i=0; i < 20; i++) {
            Product p = new Product(1);
            p.setName("Burger 1");
            p.setPrice(12 * i);
            products.add(p);
        }
        return products;

    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
        private ArrayList<Product> products;

        public ProductAdapter(ArrayList<Product> list) {
            products = list;
        }

        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.product_item,parent,false);
            return new ProductHolder(view);
        }

        @Override
        public void onBindViewHolder(ProductHolder holder, int i) {

            holder.productName.setText(products.get(i).getName());
            holder.productPrice.setText(products.get(i).getPrice()+" "+ getResources().getString(R.string.price_currency));

        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        class ProductHolder extends  RecyclerView.ViewHolder {
            private ImageView productImage;
            private TextView productName;
            private TextView productPrice;

            public ProductHolder(View itemView) {
                super(itemView);
                productImage = itemView.findViewById(R.id.product_image);
                productName = itemView.findViewById(R.id.product_name);
                productPrice = itemView.findViewById(R.id.product_price);
            }
        }
    }
}
