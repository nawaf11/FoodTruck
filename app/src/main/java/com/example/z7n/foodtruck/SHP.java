package com.example.z7n.foodtruck;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/**
 * Created by z7n on 2/18/2018.
   Shared Preferences class to save for ex: Remember UserLogin.
 */

public class SHP {

    public static class Login {
        public static final String SHP_NAME = "loginPref";
        public static final String ATT_UID = "uid"; // username, email.
        public static final String ATT_PASSWORD = "password"; // encrypted password
        public static final String ATT_TYPE = "isTruck"; // encrypted password


        public static void setUsernameOrEmail(Context context, String usernameOrEmail, boolean isTruck){
            SharedPreferences.Editor shpE
                    = context.getSharedPreferences(SHP_NAME,Context.MODE_PRIVATE).edit();
            shpE.putString(ATT_UID, usernameOrEmail);
            shpE.putBoolean(ATT_TYPE, isTruck);
            shpE.apply();
        }

        public static boolean isTruck(Context context){
            return context.getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE)
                    .getBoolean(ATT_TYPE, false);
        }

        public static @Nullable String getUsernameOrEmail(Context context){
            return context.getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE)
                    .getString(ATT_UID, null);
        }

        public static void setPassword(Context context, String password){
            SharedPreferences.Editor shpE
                    = context.getSharedPreferences(SHP_NAME,Context.MODE_PRIVATE).edit();

            String encrypted_password = AES.cipher(password);

            shpE.putString(ATT_PASSWORD, encrypted_password);
            shpE.apply();
        }

        public static @Nullable String getPassword(Context context){
            String encrypted_password = context.getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE)
                    .getString(ATT_PASSWORD, null);

            return AES.decipher(encrypted_password);
        }

        public static void clear(Context context){
            context.getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE).edit().clear().apply();
        }
    }


}
