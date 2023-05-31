package com.example.budgettracker2;
import android.content.Context;
import android.content.SharedPreferences;

public class CacheManager {
    private static CacheManager manager = new CacheManager();
    private SharedPreferences mManager = null;
    private CacheManager() {

    }

    public static CacheManager getInstance(Context context){

        if(manager.mManager == null){
            manager.mManager = context.getSharedPreferences("sharedPreference", Context.MODE_PRIVATE);
        }

        return manager;
    }

    public void setCurrentId(String stringValue) {
        SharedPreferences.Editor editor = mManager.edit();
        editor.putString("firebaseId", stringValue);
        editor.apply();
    }

    public String getCurrentId() {
        return mManager.getString("firebaseId", "");
    }

    public void removeCurrentId() {
        SharedPreferences.Editor editor = mManager.edit();
        editor.remove("firebaseId");
        editor.apply();
    }
    public void setCurrentEmail(String stringValue) {
        SharedPreferences.Editor editor = mManager.edit();
        editor.putString("firebaseEmail", stringValue);
        editor.apply();
    }

    public String getCurrentEmail() {
        return mManager.getString("firebaseEmail", "");
    }

    public void removeCurrentEmail() {
        SharedPreferences.Editor editor = mManager.edit();
        editor.remove("firebaseEmail");
        editor.apply();
    }
}
