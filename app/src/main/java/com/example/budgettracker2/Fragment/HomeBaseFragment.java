package com.example.budgettracker2.Fragment;

import static com.example.budgettracker2.Activity.MainActivity.MY_TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.budgettracker2.CacheManager;
import com.example.budgettracker2.Activity.HomeActivity;
import com.example.budgettracker2.Activity.MainActivity;
import com.example.budgettracker2.Constants;
import com.example.budgettracker2.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeBaseFragment extends Fragment implements HomeActivity.TextUpdateListener {
    private FirebaseAuth mAuth;
    private DrawerLayout mSideMenu;
    private NavigationView mNavigationView;
    private View mConvertView;
    public ImageView mOpenSideMenu;
    public ImageView mAddButton;
    public TextView mHeaderTitle;
    private FragmentUtils mFragmentUtils;
    public HomeBaseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mConvertView = inflater.inflate(R.layout.fragment_home_base, container, false);

        mAuth = FirebaseAuth.getInstance();
        mSideMenu = (DrawerLayout) mConvertView.findViewById(R.id.home_fragment_layout);
        mNavigationView = mConvertView.findViewById(R.id.navigation_view);
        mOpenSideMenu = mConvertView.findViewById(R.id.actionBar_open_sideMenu);
        mAddButton = mConvertView.findViewById(R.id.actionBar_add);
        mHeaderTitle = mConvertView.findViewById(R.id.actionBar_title);

        mHeaderTitle.setText(getString(R.string.home_title));
        mOpenSideMenu.setOnClickListener(mListener);
        mAddButton.setOnClickListener(mListener);
        if(getActivity() != null) {
            mFragmentUtils = FragmentUtils.getInstance(getActivity().getSupportFragmentManager());
        }
        initializedHomeFragment();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.menu_random_1:
                        // Handle click on menu item 1
                        break;
                    case R.id.menu_random_2:
                        // Handle click on menu item 2
                        break;
                    case R.id.menu_logout:
                        // Handle click on menu item 3
                        Log.d(MY_TAG, "menu_logout");
                        if(getActivity() != null) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Alert")
                                    .setMessage("Are you sure you want to exit?")
                                    .setNegativeButton(android.R.string.no, null)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface arg0, int arg1) {
                                            mAuth.signOut();
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            startActivity(intent);
                                            getActivity().finish();
                                            CacheManager.getInstance(getActivity()).removeCurrentId();
                                            CacheManager.getInstance(getActivity()).removeCurrentEmail();
                                        }
                                    }).create().show();
                        }
                        break;
                    default:
                        return false;
                }
                // Highlight the selected menu item
                item.setChecked(true);
                // Close the navigation drawer
                mSideMenu.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        return mConvertView;
    }

    private void initializedHomeFragment() {
        if(mFragmentUtils != null) {
            mFragmentUtils.replaceFragment(new HomeFragment(), R.id.home_content, Constants.HOME_FRAGMENT, false);
        }
    }

    private void checkFirebaseAuth() {
        if (mAuth != null && mAuth.getCurrentUser() == null && getActivity() != null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
            CacheManager.getInstance(getActivity()).removeCurrentId();
            CacheManager.getInstance(getActivity()).removeCurrentEmail();
        }
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.actionBar_open_sideMenu: {
                    Log.d(MY_TAG, "sidemenu");
                    if(mSideMenu != null && mNavigationView != null) {
                        mSideMenu.openDrawer(mNavigationView);
                    }
                    break;
                }
                case R.id.actionBar_add: {
                    Log.d(MY_TAG, "add");
                    if(mFragmentUtils != null) {
                        mFragmentUtils.showFragment(new AddItemFragment(), R.id.home_content, Constants.ADD_ITEM_FRAGMENT, true);
                    }
                    mAddButton.setVisibility(View.GONE);
                    mHeaderTitle.setText(getString(R.string.transactions_title));
                    break;
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        checkFirebaseAuth();
    }
    @Override
    public void onUpdateText(String newText) {
        mHeaderTitle.setText(newText);
    }

    public void onBack(){
        Log.d(MY_TAG, "onBack: ");
        mAddButton.setVisibility(View.VISIBLE);
    }

    public boolean isSideMenuOpen(){
        return mSideMenu.isDrawerOpen(GravityCompat.START);
    }

    public void closeSideMenu(){
        mSideMenu.closeDrawer(GravityCompat.START);
    }
}