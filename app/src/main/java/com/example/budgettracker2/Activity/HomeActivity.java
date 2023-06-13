package com.example.budgettracker2.Activity;

import static com.example.budgettracker2.Activity.MainActivity.MY_TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.budgettracker2.CacheManager;
import com.example.budgettracker2.Fragment.HomeFragment;
import com.example.budgettracker2.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    public interface TextUpdateListener {
        void onUpdateText(String newText);
    }
    private String HOME_FRAGMENT = "home_fragment";
    private HomeFragment mHomeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getArgs();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        fragmentTransaction.add(R.id.home_layout, homeFragment, HOME_FRAGMENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void getArgs() {
        if(getIntent() != null) {
            String currentIdUser = getIntent().getStringExtra((MainActivity.FIREBASE_USER_ID));
            String currentEmailUser = getIntent().getStringExtra((MainActivity.FIREBASE_USER_EMAIL));
            Log.d(MY_TAG, "firebaseName " + currentIdUser + " " + currentEmailUser);
            //set on shared preference
            CacheManager.getInstance(getApplicationContext()).setCurrentId(currentIdUser);
            CacheManager.getInstance(getApplicationContext()).setCurrentEmail(currentEmailUser);
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT);
        if(currentFragment instanceof HomeFragment) {
            mHomeFragment = (HomeFragment) currentFragment;
        }

        if (count == 1 && !mHomeFragment.isSideMenuOpen()) {
            createDialogExit();
        } else if(mHomeFragment.isSideMenuOpen()) {
            mHomeFragment.closeSideMenu();
        } else {
            getSupportFragmentManager().popBackStack();

            if (currentFragment instanceof TextUpdateListener) {
                // Call the onUpdateText() method in the current fragment via the listener
                TextUpdateListener listener = (TextUpdateListener) currentFragment;
                listener.onUpdateText(getString(R.string.home_title));
            }
            mHomeFragment.onBack();
        }
    }

    private void createDialogExit() {
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Alert")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        FirebaseAuth.getInstance().signOut();
                        Intent loggedOut = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(loggedOut);
                        setResult(RESULT_OK, new Intent().putExtra("EXIT", true));
                        finish();
                    }
                }).create().show();
    }
}