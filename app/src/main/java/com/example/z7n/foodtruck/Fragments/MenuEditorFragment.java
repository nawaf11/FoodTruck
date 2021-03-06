package com.example.z7n.foodtruck.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.Order;
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.Product;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by z7n on 2/17/2018.

 */

public class MenuEditorFragment extends Fragment {

    private View parentView;
    private ProductAdapter productAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton addProduct;
    private AddProductDialog addProductDialog;
    private Truck truck;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ========== MainActivity needs part ====================
        if(getActivity() != null) {
            MainActivity mc = (MainActivity) getActivity();
            mc.setToolbarTitle(R.string.menu);
            truck = mc.getLoginState().getTruck();
            mc.hideMenuItems();
        }
        // ========== MainActivity needs part ====================

        parentView = inflater.inflate(R.layout.menu_fragment,container,false);

        initViews();

        initList();

        return parentView;
    }

    private void initViews(){
        recyclerView = parentView.findViewById(R.id.listView_menu);
        addProduct = parentView.findViewById(R.id.truckMenu_addProduct);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductDialog = new AddProductDialog(MenuEditorFragment.this, -1);
            }
        });

   //     recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
//                if (dy > 0)
//                    addProduct.hide();
//                else if (dy < 0)
//                    addProduct.show();
//            }
//        });
    }

    private void initList(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapter(getContext(), new ArrayList<Product>());
        getProductsTask();
        recyclerView.setAdapter(productAdapter);

    }

    private void getProductsTask(){
        AndroidNetworking.post(PHPHelper.Product.getList)
                .addBodyParameter("truckid",String.valueOf(truck.getTruckId())).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("state").equals("success"))
                                startProductsTask(response);
                            else
                                Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getContext(),R.string.serverNotResponse,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startProductsTask(JSONObject response) throws JSONException {
        ArrayList<Product> arrayList = new ArrayList<>();
        JSONArray jsonArray = response.getJSONArray("data");
        for (int i=0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Product p = new Product();
            p.setProductId(jsonObject.getLong("pid"));
            p.setName(jsonObject.getString("name"));
            p.setPrice(jsonObject.getDouble("price"));
            p.setDescription(jsonObject.getString("description"));
            arrayList.add(p);
        }
        productAdapter.products.addAll(arrayList);
        productAdapter.notifyDataSetChanged();
    }

    public void productImageResult(Uri uri){
        if(addProductDialog == null)
            return;

        addProductDialog.productAdderIcon.setImageBitmap(null);
        addProductDialog.imageUri = uri;

        Picasso.with(getContext()).load(uri)
                .fit()
                .into(addProductDialog.productImage);

    }

    public static class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
        private  boolean isOrderForm = false;
        private ArrayList<Product> products;
        private Context context;
        private MenuEditorFragment menuEditorFragment;
        private OrderFragment orderFragment;

        public ProductAdapter(Context context, ArrayList<Product> list) {
            products = list;
            this.context = context;
        }

        public ProductAdapter(OrderFragment orderFragment, ArrayList<Product> list) {
            products = list;
            this.context = orderFragment.getContext();
            this.orderFragment = orderFragment;
        }

        public ProductAdapter(MenuEditorFragment menuEditorFragment, ArrayList<Product> list) {
            products = list;
            this.menuEditorFragment = menuEditorFragment;
            this.context = menuEditorFragment.getContext();
        }

        public ArrayList<Product> getProducts(){return products;}

        private Context getContext(){ return context;}

        @Override
        public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.product_item,parent,false);
            return new ProductHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final ProductHolder holder, final int i) {


            Picasso.with(getContext()).load(products.get(i).getImageLink())
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .fit()
                    .into(holder.productImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getContext()).load(R.drawable.product_food)
                                    .into(holder.productImage);
                        }
                    });

            holder.productName.setText(products.get(i).getName());
            holder.productPrice.setText(products.get(i).getPrice()+" "+ getContext().getResources().getString(R.string.price_currency));
            holder.productDescription.setText(products.get(i).getDescription());

            holder.deleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeProductTask(i);
                }
            });

            holder.editProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    products.get(i).setImage(holder.productImage.getDrawable());
                   menuEditorFragment.addProductDialog =  new AddProductDialog(menuEditorFragment, i);
                }
            });

            holder.plusQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer currentValue = Integer.parseInt(holder.orderQuantity.getText().toString());
                    if(currentValue != 99) {
                        holder.orderQuantity.setText(String.valueOf(currentValue + 1));
                        products.get(i).setOrderQuantity(currentValue+1);
                        orderFragment.updateTotalPrice(getTotalPrice());
                    }
                }
            });

            holder.minusQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer currentValue = Integer.parseInt(holder.orderQuantity.getText().toString());
                    if(currentValue != 0){
                            holder.orderQuantity.setText(String.valueOf(currentValue - 1));
                            products.get(i).setOrderQuantity(currentValue - 1);
                            orderFragment.updateTotalPrice(getTotalPrice());
                    }
                }
            });
        }

        public double getTotalPrice(){
            double totalPrice = 0;
            for (int i =0; i < products.size(); i++){
                totalPrice = totalPrice + (products.get(i).getOrderQuantity() * products.get(i).getPrice());
            }
            return totalPrice;
        }

        public void withOrderForm(){isOrderForm = true;}

        private void removeProductTask(final int i) {
            AndroidNetworking.post(PHPHelper.Product.delete)
                    .addBodyParameter("pid",String.valueOf(products.get(i).getProductId())).build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getString("state").equals("success")){
                                    products.remove(i);
                                    notifyDataSetChanged();
                                    Toast.makeText(getContext(),R.string.productDeleted,Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(getContext(),R.string.serverNotResponse,Toast.LENGTH_SHORT).show();
                        }
                    });


        }

        public void addProduct(Product product){
            products.add(product);
        }

        public void updateProduct(Product product, int i){
            products.set(i, product);
            notifyItemChanged(i);
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        class ProductHolder extends  RecyclerView.ViewHolder {
            private ImageView productImage;
            private ImageView deleteProduct;
            private ImageView editProduct;
            private TextView productName;
            private TextView productPrice;
            private TextView productDescription;

            private TextView orderQuantity;
            private ImageView plusQuantity;
            private ImageView minusQuantity;

            public ProductHolder(View itemView) {
                super(itemView);
                productImage = itemView.findViewById(R.id.product_image);
                productName = itemView.findViewById(R.id.product_name);
                productPrice = itemView.findViewById(R.id.product_price);
                productDescription = itemView.findViewById(R.id.product_description);
                deleteProduct = itemView.findViewById(R.id.delete_product);
                editProduct = itemView.findViewById(R.id.edit_product);
                orderQuantity = itemView.findViewById(R.id.orderCount__quantity);
                plusQuantity = itemView.findViewById(R.id.increment_quantity);
                minusQuantity = itemView.findViewById(R.id.decrement_quantity);

                if(!isOrderForm){
                    orderQuantity.setVisibility(View.GONE);
                    plusQuantity.setVisibility(View.GONE);
                    minusQuantity.setVisibility(View.GONE);
                } else {
                    deleteProduct.setVisibility(View.GONE);
                    editProduct.setVisibility(View.GONE);
                }
            }
        }
    }

    private static class AddProductDialog extends Dialog {
        private EditText productName, productPrice, productDescription;
        private ImageView productImage, productAdderIcon;
        private Uri imageUri;
        int productIndex;
        private MenuEditorFragment menuEditorFragment;

         AddProductDialog(MenuEditorFragment menuEditorFragment, int productToUpdateIndex) {
            super(menuEditorFragment.getContext());
            this.menuEditorFragment = menuEditorFragment;
            this.productIndex = productToUpdateIndex;
             setTitle(R.string.add_product);
             setContentView( setupViews() );
             setupWindowSize();
             if(isUpdateMode())
                 updateMode();
             show();
         }

        private void updateMode(){
             Product product = menuEditorFragment.productAdapter.products.get(productIndex);
             productName.setText(product.getName());
             productPrice.setText(String.valueOf(product.getPrice()));
             productDescription.setText(product.getDescription());
             if(product.getImage() != null)
                productImage.setImageDrawable(product.getImage());
        }

        private View setupViews() {

             View parentView = LayoutInflater.from(getContext()).inflate
                     (R.layout.add_product_dialog,null,false);

             productName =  parentView.findViewById(R.id.productName_editText);
             productPrice =  parentView.findViewById(R.id.productPrice_editText);
            productDescription = parentView.findViewById(R.id.productDescription_editText);
             productImage = parentView.findViewById(R.id.product_image);
             productAdderIcon = parentView.findViewById(R.id.productAddIcon_ImageView);

            Picasso.with(getContext()).load(R.drawable.product_food).into(productImage);

            productImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(menuEditorFragment.getActivity() != null)
                        CropImage.activity().start(menuEditorFragment.getActivity());
                }
            });

            parentView.findViewById(R.id.productDialog_cancel).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     dismiss();
                 }
             });

            parentView.findViewById(R.id.productDialog_add).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if(isInputEmpty()){
                         Toast.makeText(getContext(),R.string.register_inputEmpty,Toast.LENGTH_SHORT).show();
                         return;
                     }

                     Product p = new Product();
                     p.setName(productName.getText().toString().trim());
                     p.setDescription(productDescription.getText().toString().trim());
                     double price = Double.parseDouble(productPrice.getText().toString());
                     p.setPrice(price);
                     p.setImage(productImage.getDrawable());
                     p.setImageUri(imageUri);

                     if(isUpdateMode() )
                         startUpdateTask(p);
                     else
                         startAddTask(p);
                     dismiss();
                 }
             });

            if(isUpdateMode())
                ((TextView)parentView.findViewById(R.id.productDialog_add)).setText(R.string.update);


             return parentView;
            }

        private boolean isUpdateMode(){return  productIndex != -1;}

        private void startUpdateTask(final Product p){
            p.setProductId(menuEditorFragment.productAdapter.products.get(productIndex).getProductId());

            setCancelable(false);

            AndroidNetworking.post(PHPHelper.Product.update)
                    .addBodyParameter("pid",String.valueOf(p.getProductId()))
                    .addBodyParameter("name",p.getName())
                    .addBodyParameter("price",String.valueOf(p.getPrice()))
                    .addBodyParameter("description",p.getDescription())
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    setCancelable(true);
                    try {
                        if(response.getString("state").equals("success")){
                            startUploadImageTask(p.getProductId());
                            Toast.makeText(getContext(),R.string.productUpdate,Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    setCancelable(true);
                    Toast.makeText(getContext(),R.string.serverNotResponse,Toast.LENGTH_SHORT).show();
                }
            });

        }

        private void startAddTask(final Product p){
            setCancelable(false);

            AndroidNetworking.post(PHPHelper.Product.add)
                    .addBodyParameter("truckID",String.valueOf(menuEditorFragment.truck.getTruckId()))
                    .addBodyParameter("name",p.getName())
                    .addBodyParameter("price",String.valueOf(p.getPrice()))
                    .addBodyParameter("description",p.getDescription())
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    setCancelable(true);
                    try {
                        p.setProductId(response.getInt("data"));
                        if(response.getString("state").equals("success")){
                            startUploadImageTask(p.getProductId());

                            Toast.makeText(getContext(),R.string.productAdded,Toast.LENGTH_SHORT).show();
                            menuEditorFragment.productAdapter.addProduct(p);
                            menuEditorFragment.productAdapter.notifyItemInserted(menuEditorFragment.productAdapter.getItemCount() - 1);
                        }
                        else
                            Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    setCancelable(true);
                    Toast.makeText(getContext(),R.string.serverNotResponse,Toast.LENGTH_SHORT).show();
                }
            });

        }

        private void startUploadImageTask(long pid){
            if(imageUri == null) {
                return;
            }

            AndroidNetworking.upload(PHPHelper.Product.upload_image + "?pid=" + pid)
                    .addMultipartFile("file",new File(imageUri.getPath()))
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    if(productIndex != -1)
                        menuEditorFragment.productAdapter.notifyItemChanged(productIndex);
                    menuEditorFragment.productAdapter.notifyItemChanged(menuEditorFragment.productAdapter.getItemCount()-1);
                }

                @Override
                public void onError(ANError anError) {

                }
            });

        }

        private boolean isInputEmpty(){
             return TextUtils.isEmpty(productName.getText()) ||
                     TextUtils.isEmpty(productPrice.getText());
            }

        private void setupWindowSize() {
            if(menuEditorFragment.getActivity() == null || getWindow() == null)
                return;

            Log.d("sdfs","ddddd");
            DisplayMetrics displayMetrics = new DisplayMetrics();
           menuEditorFragment.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int displayWidth = displayMetrics.widthPixels;
           // int displayHeight = displayMetrics.heightPixels;

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            layoutParams.copyFrom(getWindow().getAttributes());

            int dialogWindowWidth = (int) (displayWidth * 0.9f);

            int dialogWindowHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

            layoutParams.width = dialogWindowWidth;
            layoutParams.height = dialogWindowHeight;

            getWindow().setAttributes(layoutParams);
        }

    }

}
