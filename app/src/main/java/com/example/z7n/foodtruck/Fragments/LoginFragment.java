package com.example.z7n.foodtruck.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.LoginState;
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.SHP;
import com.example.z7n.foodtruck.Truck;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by toshiba on 2/11/2018.

 */

public class LoginFragment extends Fragment{
    private View parentView;
    private EditText editText_emailUsername;
    private EditText editText_password;
    private RadioButton radioButton_asTruck;
    private RadioButton radioButton_asCustomer;
    private Button loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // ========== MainActivity needs part ====================
        if(getActivity() != null) {
            MainActivity mc = (MainActivity) getActivity();
            mc.setToolbarTitle(R.string.navigationBar_signIn);
            mc.hideMenuItems();
        }
        // ========== MainActivity needs part ====================

        parentView = inflater.inflate(R.layout.login_fragment,container,false);
        initViews();

        return parentView;
    }

    private void initViews() {
        editText_emailUsername = parentView.findViewById(R.id.login_emailOrUsername_editText);
        editText_password = parentView.findViewById(R.id.login_password);
        radioButton_asCustomer = parentView.findViewById(R.id.radioButton_asCustomer);
        radioButton_asTruck = parentView.findViewById(R.id.radioButton_asTruck);

        radioButton_asTruck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioButton_asCustomer.setChecked(false);
                }
            }
        });

        radioButton_asCustomer.setChecked(true);
        radioButton_asCustomer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioButton_asTruck.setChecked(false);
                }
            }
        });

        loginButton = parentView.findViewById(R.id.login_signIn_button);

        //نواف غيرت هنا شيء بالغلط شييك عليه
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(getActivity() == null || isThereError())
                        return;

                    checkLogin(); // check login info from the server.

            }
        });
    }

    private void checkLogin() {
        String link = (radioButton_asTruck.isChecked()) ? PHPHelper.Truck.login : PHPHelper.Customer.login;

        AndroidNetworking.post(link)
                .addBodyParameter("uid", editText_emailUsername.getText().toString())
                .addBodyParameter("password", editText_password.getText().toString())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("login", response.toString());
                        try {
                            boolean isLoginSuccess = response.getString("state").equals("success");
                            if(!isLoginSuccess){ // Error: username/password not match.
                                Toast.makeText(getContext(),R.string.login_failed,Toast.LENGTH_LONG).show();
                                return;
                            }
                            responseSuccess(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("login",anError.getErrorDetail());
                        Toast.makeText(getContext(), R.string.serverNotResponse,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isInputEmpty(){
        return TextUtils.isEmpty(editText_emailUsername.getText()) ||
                TextUtils.isEmpty(editText_password.getText());
    }

    private void responseSuccess(JSONObject response) throws JSONException {

        if(getActivity() == null)
            return;

        MainActivity activity = ((MainActivity)getActivity());
        LoginState loginState = LoginState.CreateLogin(response,radioButton_asTruck.isChecked());
        activity.setLoginState(loginState);

        Toast.makeText(getContext(),R.string.login_success,Toast.LENGTH_LONG).show();
        rememberUser();
        activity.setFragment(new TruckListFragment());
    }

    private void rememberUser(){
        SHP.Login.setUsernameOrEmail(getContext(),
                editText_emailUsername.getText().toString(),radioButton_asTruck.isChecked());

        SHP.Login.setPassword(getContext(), editText_password.getText().toString());

    }

    private boolean isThereError() {
        if(isInputEmpty()){
            Toast.makeText(getContext(), R.string.register_inputEmpty,Toast.LENGTH_LONG).show();
            return true;
        }

        return false;
    }
}
