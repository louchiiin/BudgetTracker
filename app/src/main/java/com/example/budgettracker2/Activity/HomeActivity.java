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
import com.example.budgettracker2.Fragment.HomeFragment;
import com.example.budgettracker2.Fragment.HomeBaseFragment;
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
    private HomeBaseFragment mHomeBaseFragment;
    private boolean mIsMenuTapped = false;
    private FragmentUtils mFragmentUtils;
    private boolean mIsStatusFragmentVisible = false;
    private boolean mIsHomeFragmentVisible = false;

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
            mFragmentUtils.showFragment(new HomeBaseFragment(), R.id.home_layout, Constants.HOME_BASE_FRAGMENT, false);
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
                            mFragmentUtils.replaceFragment(new HomeFragment(), R.id.home_content, Constants.HOME_FRAGMENT, false);
                        }
                        mIsMenuTapped = false;
                        mIsHomeFragmentVisible = true;
                        mIsStatusFragmentVisible = false;
                        checkIfStatusFragmentIsVisible();
                        return true;
                    case R.id.navigation_item_2:
                        mIsMenuTapped = true;
                        onBackPressed();
                        if(mFragmentUtils != null) {
                            mFragmentUtils.replaceFragment(new StatsFragment(), R.id.home_content, Constants.STATUS_FRAGMENT, false);
                        }
                        mIsMenuTapped = false;
                        mIsHomeFragmentVisible = false;
                        mIsStatusFragmentVisible = true;
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
        HomeBaseFragment homeBaseFragment = (HomeBaseFragment) getSupportFragmentManager().findFragmentByTag(Constants.HOME_BASE_FRAGMENT);
        if(homeBaseFragment != null && homeBaseFragment.getView() != null) {
            if (mIsStatusFragmentVisible) {
                homeBaseFragment.getView().findViewById(R.id.actionBar_add).setVisibility(View.GONE);
                TextView textView = homeBaseFragment.getView().findViewById(R.id.actionBar_title);
                textView.setText(getString(R.string.statistics_title));
            } else {
                homeBaseFragment.getView().findViewById(R.id.actionBar_add).setVisibility(View.VISIBLE);
                TextView textView = homeBaseFragment.getView().findViewById(R.id.actionBar_title);
                textView.setText(getString(R.string.home_title));
            }
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        Fragment homeBaseFragment = getSupportFragmentManager().findFragmentByTag(Constants.HOME_BASE_FRAGMENT);
        Fragment statsFragment = getSupportFragmentManager().findFragmentByTag(Constants.STATUS_FRAGMENT);
        Fragment homeFragment = getSupportFragmentManager().findFragmentByTag(Constants.HOME_FRAGMENT);
        if(homeBaseFragment instanceof HomeBaseFragment) {
            mHomeBaseFragment = (HomeBaseFragment) homeBaseFragment;
        }

        if (count == 0 && mHomeBaseFragment != null && !mHomeBaseFragment.isSideMenuOpen()) {
            if(!mIsMenuTapped) {
                createDialogExit();
            }
        } else if(mHomeBaseFragment != null && mHomeBaseFragment.isSideMenuOpen()) {
            mHomeBaseFragment.closeSideMenu();
        } else {
            getSupportFragmentManager().popBackStack();

            if (homeBaseFragment instanceof TextUpdateListener) {
                // Call the onUpdateText() method in the current fragment via the listener
                TextUpdateListener listener = (TextUpdateListener) homeBaseFragment;
                if(homeFragment != null && homeFragment.isVisible()) {
                    listener.onUpdateText(getString(R.string.home_title));
                    mHomeBaseFragment.onBack();
                }
                if(statsFragment != null && statsFragment.isVisible()) {
                    listener.onUpdateText(getString(R.string.statistics_title));
                }
            }
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