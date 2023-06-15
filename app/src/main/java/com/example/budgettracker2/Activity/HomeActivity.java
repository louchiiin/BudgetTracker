package com.example.budgettracker2.Activity;

import static com.example.budgettracker2.Activity.MainActivity.MY_TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.budgettracker2.CacheManager;
import com.example.budgettracker2.Constants;
import com.example.budgettracker2.Fragment.FragmentUtils;
import com.example.budgettracker2.Fragment.HomeBaseFragment;
import com.example.budgettracker2.Fragment.HomeFragment;
import com.example.budgettracker2.Fragment.StatsFragment;
import com.example.budgettracker2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    public interface TextUpdateListener {
        void onUpdateText(String newText);
    }
    private BottomNavigationView mBottomNavigationView;
    private HomeFragment mHomeFragment;
    private boolean mIsMenuTapped = false;
    private FragmentUtils mFragmentUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getArgs();
        mFragmentUtils = FragmentUtils.getInstance(getSupportFragmentManager());
        initializeBottomNavigation();
        initializeBaseFragment();
    }

    private void initializeBaseFragment() {
        if(mFragmentUtils != null) {
            mFragmentUtils.showFragment(new HomeFragment(), R.id.home_layout, Constants.HOME_FRAGMENT, false);
        }
    }

    private void initializeBottomNavigation() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setItemIconTintList(null);
        mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_item_1:
                        mIsMenuTapped = true;
                        onBackPressed();
                        if(mFragmentUtils != null) {
                            mFragmentUtils.replaceFragment(new HomeBaseFragment(), R.id.home_content, "home_base_fragment", false);
                        }
                        mIsMenuTapped = false;
                        return true;
                    case R.id.navigation_item_2:
                        mIsMenuTapped = true;
                        onBackPressed();
                        initializeStatsFragment();
                        mIsMenuTapped = false;
                        checkIfStatusFragmentIsVisible();
                        return true;
                    case R.id.navigation_item_3:
                        // Handle item 3 selection
                        return true;
                }
                return false;
            }
        });
    }

    private void initializeStatsFragment() {
        if(mFragmentUtils != null) {
            mFragmentUtils.replaceFragment(new StatsFragment(), R.id.home_content, Constants.STATUS_FRAGMENT, false);
        }
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
    protected void onResume() {
        super.onResume();

        checkIfStatusFragmentIsVisible();
    }

    private void checkIfStatusFragmentIsVisible() {
        StatsFragment statsFragment = (StatsFragment) getSupportFragmentManager().findFragmentByTag(Constants.STATUS_FRAGMENT);
        boolean isStatusFragmentVisible = (statsFragment != null && statsFragment.isVisible());
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(Constants.HOME_FRAGMENT);
        boolean isHomeFragmentVisible = (homeFragment != null && homeFragment.isVisible());
        if (isStatusFragmentVisible && isHomeFragmentVisible) {
            if(homeFragment.getView() != null) {
                homeFragment.getView().findViewById(R.id.actionBar_add).setVisibility(View.GONE);
                TextView textView = homeFragment.getView().findViewById(R.id.actionBar_title);
                textView.setText("Statistics");
            }
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(Constants.HOME_FRAGMENT);
        if(currentFragment instanceof HomeFragment) {
            mHomeFragment = (HomeFragment) currentFragment;
        }

        if (count == 1 && !mHomeFragment.isSideMenuOpen()) {
            if(!mIsMenuTapped) {
                createDialogExit();
            }
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