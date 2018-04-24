package com.example.z7n.foodtruck.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.z7n.foodtruck.Order;

import java.util.ArrayList;

/**
 * Created by z7n on 3/26/2018.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "notes_db";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Order.CREATE_SCHEME);
        db.execSQL(Favorite.CREATE_SCHEME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addFavorite(long truckID){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Favorite.COLUMN_TRUCK_ID, truckID);

        // insert row
        long id = db.insert(Favorite.TABLE_NAME, null, values);

        // close db connection
        db.close();
    }

    public void deleteFavorite(long truckID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Favorite.TABLE_NAME, Favorite.COLUMN_TRUCK_ID +" = ?" , new String[]{truckID+""});
        db.close();
    }

    public boolean isFavorite(long truckID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM "+Favorite.TABLE_NAME +" WHERE "+Favorite.COLUMN_TRUCK_ID +" = "+truckID, null);

        boolean flag = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return flag;
    }

    public long[] getFavoriteTrucks(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + Favorite.COLUMN_TRUCK_ID + " FROM "+Favorite.TABLE_NAME, null);
        long [] result = new long[cursor.getCount()];

        int i=0;
        while (cursor.moveToNext())
            result[i++] = cursor.getLong(0);

        cursor.close();
        db.close();
        return result;

    }

    public void insertOrder(long orderID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Order.COLUMN_ORDER_ID, orderID);

        long id = db.insert(Order.TABLE_NAME, null, contentValues);
        Log.d("orderDB", "orderInserted: "+id);
        db.close();
    }

    public ArrayList<com.example.z7n.foodtruck.Order> getOrders(){
        ArrayList<com.example.z7n.foodtruck.Order> orderList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+Order.TABLE_NAME, null);

        while (cursor.moveToNext()) {
            com.example.z7n.foodtruck.Order order = new com.example.z7n.foodtruck.Order();
            order.setOrderID(cursor.getLong(cursor.getColumnIndex(Order.COLUMN_ORDER_ID)));
            order.setStatus(cursor.getString(cursor.getColumnIndex(Order.COLUMN_ORDER_STATUS)));
            orderList.add(order);
        }

        cursor.close();
        db.close();
        return orderList;
    }

    public void deleteOrder(long oid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Order.TABLE_NAME, Order.COLUMN_ORDER_ID +" = ?" , new String[]{oid+""});
        db.close();
    }

    public static class Order {
        public static final String TABLE_NAME = "Orders";
        public static final String COLUMN_ORDER_ID = "orderID";
        public static final String COLUMN_ORDER_STATUS = "status"; // P = pending, accepted, rejected.

        public static final String  STATUS_PENDING = "P";
        public static final String STATUS_ACCEPTED = "A";
        public static final String STATUS_REJECTED = "R";

        public static final String CREATE_SCHEME = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ORDER_ID + " INTEGER PRIMARY KEY ,"
                + COLUMN_ORDER_STATUS + " TEXT DEFAULT 'P'"
                + ")";
    }

    public static class Favorite {
        public static final String TABLE_NAME = "Favorite";
        public static final String COLUMN_TRUCK_ID = "truckID";

        public static final String CREATE_SCHEME = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_TRUCK_ID + " INTEGER PRIMARY KEY "
                + ")";
    }
}
