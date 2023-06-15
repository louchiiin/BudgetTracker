package com.example.budgettracker2.Fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentUtils extends Fragment{

    private static FragmentUtils instance;
    private FragmentManager fragmentManager;

    private FragmentUtils(FragmentManager manager) {
        // Private constructor to enforce singleton pattern
        fragmentManager = manager;
    }

    public static FragmentUtils getInstance(FragmentManager fragmentManager) {
        if (instance == null) {
            instance = new FragmentUtils(fragmentManager);
        }
        return instance;
    }

    public void replaceFragment(Fragment fragment, int resourceView, String tagName, boolean isAddToBackStack) {
        Fragment replaceFrag = fragmentManager.findFragmentByTag(tagName);
        if(replaceFrag == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(resourceView, fragment, tagName);
            if(isAddToBackStack) {
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.commit();
        }
    }

    public void showFragment(Fragment fragment, int resourceView, String tagName, boolean isAddToBackStack) {
        Fragment showFrag = fragmentManager.findFragmentByTag(tagName);
        if(showFrag == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(resourceView, fragment, tagName);
            if(isAddToBackStack) {
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.commit();
        }
    }
}

