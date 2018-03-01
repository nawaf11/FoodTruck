package com.example.z7n.foodtruck.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
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
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.Product;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;
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
                addProductDialog = new AddProductDialog(getContext(), -1);
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
        productAdapter = new ProductAdapter(new ArrayList<Product>());
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
        int w = addProductDialog.productImage.getWidth();
        int h = addProductDialog.productImage.getHeight();
        Picasso.with(getContext()).load(uri)
                .resize(w, h).
                into(addProductDialog.productImage);

        // Temp
                AndroidNetworking.upload("http://foodtruck.mywebcommunity.org/php/upload_productImage.php")
                            .addMultipartFile("file",new File(uri.getPath()))
                            .build().getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("fgsgst",response.toString());
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
        private ArrayList<Product> products;

        public ProductAdapter(ArrayList<Product> list) {
            products = list;
        }

        @Override
        public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.product_item,parent,false);
            return new ProductHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductHolder holder, final int i) {

            Picasso.with(getContext()).load(R.drawable.product_food)
                    .into(holder.productImage);

            holder.productName.setText(products.get(i).getName());
            holder.productPrice.setText(products.get(i).getPrice()+" "+ getResources().getString(R.string.price_currency));
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
                    new AddProductDialog(getContext(), i);
                }
            });

        }

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

            public ProductHolder(View itemView) {
                super(itemView);
                productImage = itemView.findViewById(R.id.product_image);
                productName = itemView.findViewById(R.id.product_name);
                productPrice = itemView.findViewById(R.id.product_price);
                productDescription = itemView.findViewById(R.id.product_description);
                deleteProduct = itemView.findViewById(R.id.delete_product);
                editProduct = itemView.findViewById(R.id.edit_product);
            }
        }
    }

    private class AddProductDialog extends Dialog {
        private EditText productName, productPrice, productDescription;
        private ImageView productImage, productAdderIcon;
        private Uri imageUri;
        int productIndex;

         AddProductDialog(Context context, int productToUpdateIndex) {
            super(context);
            this.productIndex = productToUpdateIndex;
             setTitle(R.string.add_product);
             setContentView( setupViews() );
             setupWindowSize();
             if(isUpdateMode())
                 updateMode();
             show();
         }

        private void updateMode(){
             Product product = productAdapter.products.get(productIndex);
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
                    if(getActivity() != null)
                        CropImage.activity().start(getActivity());
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
            p.setProductId(productAdapter.products.get(productIndex).getProductId());

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
                            Toast.makeText(getContext(),R.string.productUpdate,Toast.LENGTH_SHORT).show();
                            productAdapter.updateProduct(p,productIndex);
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
                    .addBodyParameter("truckID",String.valueOf(truck.getTruckId()))
                    .addBodyParameter("name",p.getName())
                    .addBodyParameter("price",String.valueOf(p.getPrice()))
                    .addBodyParameter("description",p.getDescription())
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    setCancelable(true);
                    try {
                        if(response.getString("state").equals("success")){
                            Toast.makeText(getContext(),R.string.productAdded,Toast.LENGTH_SHORT).show();
                            productAdapter.addProduct(p);
                            productAdapter.notifyItemInserted(productAdapter.getItemCount() - 1);
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

        private boolean isInputEmpty(){
             return TextUtils.isEmpty(productName.getText()) ||
                     TextUtils.isEmpty(productPrice.getText());
            }

        private void setupWindowSize() {
            if(getActivity() == null || getWindow() == null)
                return;

            Log.d("sdfs","ddddd");
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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
