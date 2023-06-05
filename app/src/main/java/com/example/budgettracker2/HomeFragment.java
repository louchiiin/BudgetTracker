package com.example.budgettracker2;

import static com.example.budgettracker2.MainActivity.MY_TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment implements HomeActivity.TextUpdateListener{
    private FirebaseAuth mAuth;
    private DrawerLayout mSideMenu;
    private NavigationView mNavigationView;
    private View mConvertView;
    private ImageView mOpenSideMenu;
    private ImageView mAddButton;
    private TextView mHeaderTitle;
    private String ADD_ITEM_FRAGMENT = "add_item_fragment";
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mConvertView = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        mSideMenu = (DrawerLayout) mConvertView.findViewById(R.id.home_fragment_layout);
        mNavigationView = mConvertView.findViewById(R.id.navigation_view);
        mOpenSideMenu = mConvertView.findViewById(R.id.actionBar_open_sideMenu);
        mAddButton = mConvertView.findViewById(R.id.actionBar_add);
        mHeaderTitle = mConvertView.findViewById(R.id.actionBar_title);

        mHeaderTitle.setText("Home");
        mOpenSideMenu.setOnClickListener(mListener);
        mAddButton.setOnClickListener(mListener);

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
                            mAuth.signOut();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                            CacheManager.getInstance(getActivity()).removeCurrentId();
                            CacheManager.getInstance(getActivity()).removeCurrentEmail();
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
                    if(getActivity() != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        Fragment addItemFrag = fragmentManager.findFragmentByTag(ADD_ITEM_FRAGMENT);
                        if(addItemFrag == null) {
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            AddItemFragment addItemFragment = new AddItemFragment();
                            fragmentTransaction.replace(R.id.home_content, addItemFragment, ADD_ITEM_FRAGMENT);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
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
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof HomeActivity) {
            HomeActivity activity = (HomeActivity) context;
            activity.setTextUpdateListener(this);
        }
    }

    @Override
    public void onUpdateText(String newText) {
        mHeaderTitle.setText(newText);
    }

    public void onBack(){
        Log.d(MY_TAG, "onBack: ");
        mAddButton.setVisibility(View.VISIBLE);
    }
}