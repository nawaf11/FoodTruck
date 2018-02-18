package com.example.z7n.foodtruck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.LoginState;
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.SHP;
import com.example.z7n.foodtruck.Truck;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by z7n on 2/15/2018.

 */

public class RegisterFragment extends Fragment {

    private  View parentView;
    private RadioButton radioButton_asCustomer;
    private RadioButton radioButton_asTruck;
    private EditText editText_username;
    private EditText editText_email;
    private EditText editText_password;
    private EditText editText_passwordConfirm;
    private EditText editText_phoneNumber;
    private EditText editText_truckName;
    private EditText editText_truckDescription;
    private Button buttonRegister;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

         parentView = inflater.inflate(R.layout.truck_register_fragment,container,false);
         initViews();

         return parentView;
    }

    private void initViews() {
        showTruckForm(false); // Default is customer
        buttonRegister = parentView.findViewById(R.id.button_register);
        radioButton_asCustomer = parentView.findViewById(R.id.radioButton_asCustomer);
        radioButton_asCustomer.setChecked(true);
        radioButton_asTruck = parentView.findViewById(R.id.radioButton_asTruck);
        editText_username = parentView.findViewById(R.id.editText_username);
        editText_email = parentView.findViewById(R.id.editText_email);
        editText_password = parentView.findViewById(R.id.editText_password);
        editText_passwordConfirm = parentView.findViewById(R.id.editText_passwordConfirm);
        editText_phoneNumber = parentView.findViewById(R.id.editText_phoneNumber);
        editText_truckName = parentView.findViewById(R.id.editText_truckName);
        editText_truckDescription = parentView.findViewById(R.id.editText_truckDescription);


        radioButton_asTruck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioButton_asCustomer.setChecked(false);
                    showTruckForm(true);
                }
            }
        });

        radioButton_asCustomer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioButton_asTruck.setChecked(false);
                    showTruckForm(false);
            }
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioButton_asTruck.isChecked())
                    submitTruck();
                else if(radioButton_asCustomer.isChecked())
                    submitCustomer();
            }
        });

    }

    private void submitCustomer() {
        if(isThereError())
            return;


        AndroidNetworking.post(PHPHelper.Customer.insert)
                .addBodyParameter("username",editText_username.getText().toString())
                .addBodyParameter("password",editText_password.getText().toString())
                .addBodyParameter("phone",editText_phoneNumber.getText().toString())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("state")!=null &&
                                    response.getString("state").equals("success")) {
                                responseSuccess();
                            } else // state: error
                                responseError(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getContext(), R.string.serverNotResponse,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitTruck(){

        if(isThereError())
            return;

        AndroidNetworking.post(PHPHelper.Truck.insert)
                .addBodyParameter("username",editText_username.getText().toString())
                .addBodyParameter("email",editText_email.getText().toString())
                .addBodyParameter("password",editText_password.getText().toString())
                .addBodyParameter("phone",editText_phoneNumber.getText().toString())
                .addBodyParameter("truckName",editText_truckName.getText().toString())
                .addBodyParameter("truckDescription", editText_truckDescription.getText().toString())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("state")!=null &&
                                    response.getString("state").equals("success")) {
                                responseSuccess();
                            } else // state: error
                                responseError(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("server-response",anError.getErrorDetail());
                        Toast.makeText(getContext(), R.string.serverNotResponse,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseError(JSONObject response) throws JSONException {
        if (response == null)
            return;

        String err_msg = response.getString("error_msg");

        try {
            if (err_msg.contains("Duplicate") && err_msg.contains("email"))
                Toast.makeText(getContext(), R.string.email_isUsed, Toast.LENGTH_LONG).show();

            else if (err_msg.contains("Duplicate") && err_msg.contains("username"))
                Toast.makeText(getContext(), R.string.username_isUsed, Toast.LENGTH_LONG).show();
        } catch (NullPointerException e){
            /*
             java.lang.NullPointerException: Attempt to invoke virtual method 'android.content.res.Resources android.content.Context.getResources()' on a null object reference
             at android.widget.Toast.makeText(Toast.java:531)
             at com.example.z7n.foodtruck.Fragments.RegisterFragment.responseError(RegisterFragment.java:189
             */
        }
    }

    private void responseSuccess(){
        Toast.makeText(getContext(), R.string.register_completed, Toast.LENGTH_LONG).show();
        LoginState loginState = new LoginState();
        Truck truck = new Truck();
        truck.setTruckName(editText_truckName.getText().toString());
        truck.setUserName(editText_username.getText().toString());
        truck.setDescription(editText_truckDescription.getText().toString());
        truck.setEmail(editText_email.getText().toString());
        truck.setPhoneNumber(editText_phoneNumber.getText().toString());


        loginState.setTruck(truck);
        if(getActivity() == null)
            return;

        MainActivity activity = (MainActivity) getActivity();
        activity.setLoginState(loginState);
        rememberUser();
        activity.setFragment(new TruckListFragment());
    }

    private void rememberUser(){
        SHP.Login.setUsernameOrEmail(getContext(),
                editText_username.getText().toString(),radioButton_asTruck.isChecked());

        SHP.Login.setPassword(getContext(), editText_password.getText().toString());

    }
    private boolean isThereError(){
        // =========== Error Check ==================
        if(isInputEmpty()) {
            Toast.makeText(getContext(),getResources().getString(R.string.register_inputEmpty),Toast.LENGTH_LONG).show();
            return true;
        }

        char userFirst = editText_username.getText().toString().toUpperCase().charAt(0);
        if(!(userFirst >= 'A' && userFirst <= 'Z')) {
            Toast.makeText(getContext(),getResources().getString(R.string.register_userInvalid),Toast.LENGTH_LONG).show();
            return true;
        }

        if( editText_password.getText().length() < 6){
            Toast.makeText(getContext(),getResources().getString(R.string.register_passwordLength),Toast.LENGTH_LONG).show();
            return true;
        }

        if(! editText_password.getText().toString().equals(editText_passwordConfirm.getText().toString())){
            Toast.makeText(getContext(),getResources().getString(R.string.register_notSamePassword),Toast.LENGTH_LONG).show();
            return true;
        }

        if(radioButton_asTruck.isChecked() &&
                !Patterns.EMAIL_ADDRESS.matcher(editText_email.getText().toString()).matches()){
            Toast.makeText(getContext(),getResources().getString(R.string.register_invalidEmail),Toast.LENGTH_LONG).show();
            return true;
        }

        if(!Patterns.PHONE.matcher(editText_phoneNumber.getText().toString()).matches()){
            Toast.makeText(getContext(),getResources().getString(R.string.register_invalidPhone),Toast.LENGTH_LONG).show();
            return true;
        }

        if(radioButton_asCustomer.isChecked())
            return false; // No  error for customer.

        // =========== For truck ==================


        return false;
    }

    private boolean isInputEmpty(){

        if(radioButton_asCustomer.isChecked()){
            return     TextUtils.isEmpty(editText_username.getText())
                    || TextUtils.isEmpty(editText_password.getText())
                    || TextUtils.isEmpty(editText_passwordConfirm.getText())
                    || TextUtils.isEmpty(editText_phoneNumber.getText());
        }

        return     TextUtils.isEmpty(editText_username.getText())
                || TextUtils.isEmpty(editText_email.getText())
                || TextUtils.isEmpty(editText_password.getText())
                || TextUtils.isEmpty(editText_passwordConfirm.getText())
                || TextUtils.isEmpty(editText_phoneNumber.getText())
                || TextUtils.isEmpty(editText_truckName.getText())
                || TextUtils.isEmpty(editText_truckDescription.getText());

    }

    private void showTruckForm(boolean isTruck) {
        int action = (isTruck) ? View.VISIBLE : View.GONE;

            parentView.findViewById(R.id.editText_email).setVisibility(action);
            parentView.findViewById(R.id.editText_truckName).setVisibility(action);
            parentView.findViewById(R.id.editText_truckDescription).setVisibility(action);

    }
}
