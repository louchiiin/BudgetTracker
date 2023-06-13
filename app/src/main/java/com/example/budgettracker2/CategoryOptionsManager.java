package com.example.budgettracker2;

import static com.example.budgettracker2.Activity.MainActivity.MY_TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.budgettracker2.Model.AccountsList;
import com.example.budgettracker2.Model.CategoryList;
import com.example.budgettracker2.Model.TransactionList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CategoryOptionsManager {
    private static CategoryOptionsManager manager = new CategoryOptionsManager();

    public ArrayList<CategoryList> mCategoriesList;
    public ArrayList<AccountsList> mAccountsList;
    public ArrayList<TransactionList> mTransactionList;
    private CategoryOptionsManager() {
        mCategoriesList = null;
        mAccountsList = null;
        mTransactionList = null;
    }

    public static CategoryOptionsManager getInstance(){
        return manager;
    }

    //getter and setters


    public ArrayList<CategoryList> getCategoryList() {
        return mCategoriesList;
    }

    public void setCategoryList(ArrayList<CategoryList> categoryLists) {
        this.mCategoriesList = categoryLists;
    }

    public ArrayList<AccountsList> getListAccounts() {
        return mAccountsList;
    }

    public void setListAccounts(ArrayList<AccountsList> listAccounts) {
        this.mAccountsList = listAccounts;
    }

    public ArrayList<TransactionList> getTransactionList() {
        return mTransactionList;
    }

    public void setTransactionList(ArrayList<TransactionList> mTransactionList) {
        this.mTransactionList = mTransactionList;
    }

    public void requestFetchAccount(ManagerCallback callback) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("AccountsCategory");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mAccountsList = new ArrayList<>();
                Gson gson = new Gson();
                Set<String> accountNamesSet = new HashSet<>();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String values = gson.toJson(dataSnapshot.getValue());
                    // Parse the string as a JSON object
                    Map<String, Object> data = gson.fromJson(values, Map.class);
                    // Extract the value of "accountsName"
                    String accountName = (String) data.get("accountsName");
                    String uniqueKey = dataSnapshot.getKey();

                    AccountsList accountsList = dataSnapshot.getValue(AccountsList.class);
                    if(accountsList != null) {
                        accountsList.setId(uniqueKey);
                        accountsList.setAccountName(accountName);

                        // Check if the account name already exists in the set
                        if (!accountNamesSet.contains(accountName)) {
                            // Account name is not in the set, add the account to the list
                            mAccountsList.add(accountsList);
                            accountNamesSet.add(accountName);
                        }
                    }
                }
                if(callback != null) {
                    callback.onFinish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(callback != null) {
                    callback.onError(error.getMessage());
                }
            }
        });
    }

    public void requestFetchCategory(ManagerCallback callback) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Category");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mCategoriesList = new ArrayList<>();
                Gson gson = new Gson();
                Set<String> accountNamesSet = new HashSet<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String values = gson.toJson(dataSnapshot.getValue());
                    // Parse the string as a JSON object
                    Map<String, Object> data = gson.fromJson(values, Map.class);
                    // Extract the value of "accountsName"
                    String categoryName = (String) data.get("categoryName");
                    String uniqueKey = dataSnapshot.getKey();

                    CategoryList categoryList = dataSnapshot.getValue(CategoryList.class);
                    if (categoryList != null) {
                        categoryList.setId(uniqueKey);
                        categoryList.setCategoryName(categoryName);

                        // Check if the account name already exists in the set
                        if (!accountNamesSet.contains(categoryName)) {
                            // Account name is not in the set, add the account to the list
                            mCategoriesList.add(categoryList);
                            accountNamesSet.add(categoryName);
                        }
                    }
                }

                if (callback != null) {
                    callback.onFinish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(callback != null) {
                    callback.onError(error.getMessage());
                }
            }
        });
    }

    public void requestFetchTransaction(ManagerCallback callback) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Transaction");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mTransactionList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String childKey = childSnapshot.getKey();
                        Log.d(MY_TAG, "childKey " + childKey);

                        // Create a new Transaction object and set its values
                        TransactionList transaction = new TransactionList();
                        transaction.setTransactionId(childKey);
                        transaction.setTransactionDate(childSnapshot.child("transaction_date").getValue(String.class));
                        transaction.setTransactionAmount(childSnapshot.child("transaction_amount").getValue(String.class));
                        transaction.setTransactionAccountType(childSnapshot.child("transaction_account_type").getValue(String.class));
                        transaction.setTransactionCategoryType(childSnapshot.child("transaction_category_type").getValue(String.class));
                        transaction.setTransactionDescription(childSnapshot.child("transaction_description").getValue(String.class));
                        transaction.setTransactionNote(childSnapshot.child("transaction_note").getValue(String.class));

                        // Add the transaction to the ArrayList
                        mTransactionList.add(transaction);
                    }
                }

                Log.d(MY_TAG, "onDataChange: " + new Gson().toJson(mTransactionList));

                if (callback != null) {
                    callback.onFinish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(callback != null) {
                    callback.onError(error.getMessage());
                }
            }
        });
    }
}
