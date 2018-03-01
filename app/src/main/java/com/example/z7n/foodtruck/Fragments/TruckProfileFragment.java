package com.example.z7n.foodtruck.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.LoginState;
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;

import org.json.JSONException;
import org.json.JSONObject;


/**
 *  // This fragment when the truck want to update his Account info like Edit the Menu, password.
 */
public class TruckProfileFragment extends Fragment {

    private Truck truck;

    private View parentView;

    private TextView headerTruckName;
    private TextView changePassword;
    private EditText userName_editText;
    private EditText email_editText;
    private EditText phone_editText;
    private EditText truckName_editText;
    private EditText truckDescription_editText;
    private ImageView truckImageView;
    private Button editMenu_button;

    public TruckProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // ========== MainActivity needs part ====================
        if(getActivity() != null) {
            MainActivity mc = (MainActivity) getActivity();
            mc.setToolbarTitle(R.string.toolbar_profileTitle);
            mc.hideMenuItems();
            mc.getMenu().findItem(R.id.menuItem_editProfile).setVisible(true);
            mc.getMenu().findItem(R.id.menuItem_saveProfileUpdate).setVisible(true);
        }
        // ========== MainActivity needs part ====================

        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_truck_profile, container, false);
        initViews();
        initTruck(); // set truck obj from MainActivity and show the data.


        return parentView;
    }

    private void initViews() {
        headerTruckName = parentView.findViewById(R.id.truckProfile_truckHeaderName);
        userName_editText = parentView.findViewById(R.id.truckProfile_truckUsernameEdit);
        email_editText = parentView.findViewById(R.id.truckProfile_truckEmailEdit);
        phone_editText = parentView.findViewById(R.id.truckProfile_truckPhoneEdit);
        truckName_editText = parentView.findViewById(R.id.truckProfile_truckNameEdit);
        truckDescription_editText = parentView.findViewById(R.id.truckProfile_truckDescriptionEdit);
        truckImageView = parentView.findViewById(R.id.truckProfile_truckImage);
        changePassword = parentView.findViewById(R.id.truckProfile_changePassword);
        editMenu_button = parentView.findViewById(R.id.truckProfile_editMenu_button);

        editMenu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity()!= null && getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).setFragment(new MenuEditorFragment(), true);
            }
        });

    }

    private void initTruck() {

        if(getActivity() == null)
            return;

        LoginState loginState = ((MainActivity)getActivity()).getLoginState();

        if( !loginState.isTruck() || loginState.isVisitor())
            return;

        truck = loginState.getTruck();
        if(truck == null)
            return;

        headerTruckName.setText(truck.getTruckName());
        userName_editText.setText(truck.getUserName());
        email_editText.setText(truck.getEmail());
        phone_editText.setText(truck.getPhoneNumber());
        truckName_editText.setText(truck.getTruckName());
        truckDescription_editText.setText(truck.getDescription());

    }

    public void editClicked(){ // menuItem in toolbar
        truckName_editText.setBackgroundResource(R.drawable.edit_text_border);
        truckDescription_editText.setBackgroundResource(R.drawable.edit_text_border);
        truckName_editText.setFocusable(true);
        truckName_editText.setFocusableInTouchMode(true);
        truckDescription_editText.setFocusable(true);
        truckDescription_editText.setFocusableInTouchMode(true);

    }

    public void saveClicked(){  // menuItem in toolbar
        truckName_editText.setBackgroundResource(R.drawable.edit_text_border_closed);
        truckDescription_editText.setBackgroundResource(R.drawable.edit_text_border_closed);
        truckName_editText.setFocusable(false);
        truckDescription_editText.setFocusable(false);
        truckDescription_editText.setFocusable(false);
        truckDescription_editText.setFocusableInTouchMode(false);

        if(! truckName_editText.getText().toString().equals(truck.getTruckName()) ||
                ! truckDescription_editText.getText().toString().equals(truck.getDescription()))
            startUpdateTask();
    }

    private void startUpdateTask() {

        AndroidNetworking.post(PHPHelper.Truck.update_profile)
                .addBodyParameter("TruckID",truck.getTruckId()+"")
                .addBodyParameter("name",truckName_editText.getText().toString())
                .addBodyParameter("description",truckDescription_editText.getText().toString())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("takotako",response.toString());
                            int msg = (response.getString("state").equals("success")) ?
                                    R.string.data_updated_success : R.string.unknownError;

                            Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();

                            if(msg == R.string.data_updated_success && getActivity() != null){
                                truck.setTruckName(truckName_editText.getText().toString());
                                truck.setDescription(truckDescription_editText.getText().toString());
                                MainActivity mc = (MainActivity) getActivity();
                                mc.setLoginState(mc.getLoginState()); // To update NavigationHeader With NewData
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("server-response",anError.getErrorDetail());
                        Toast.makeText(getContext(),R.string.serverNotResponse,Toast.LENGTH_LONG).show();

                    }
                });
    }

}
