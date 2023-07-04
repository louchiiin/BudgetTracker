package com.example.budgettracker2;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.budgettracker2.Interfaces.ManagerCallback;
import com.example.budgettracker2.Model.AccountsList;
import com.example.budgettracker2.Model.CategoryList;
import com.example.budgettracker2.Model.TransactionList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CategoryOptionsManager {
    private static CategoryOptionsManager manager = new CategoryOptionsManager();

    public ArrayList<CategoryList> mCategoriesList;
    public ArrayList<AccountsList> mAccountsList;
    public ArrayList<TransactionList> mTransactionList;
    public String mCurrency;

    private CategoryOptionsManager() {
        mCategoriesList = null;
        mAccountsList = null;
        mTransactionList = null;
        mCurrency = "";
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

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String place) {
        if(place.equals("PH")){
            this.mCurrency = "₱";
        }else{
            this.mCurrency = "$";
        }
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

    public void requestFetchTransaction(int sort, String type, String startDate, String endDate, ManagerCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Transaction/" + type);

        Query query = databaseReference.orderByChild("transaction_date").startAt(startDate).endAt(endDate);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTransactionList = new ArrayList<>();

                for (DataSnapshot transactionSnapshot : dataSnapshot.getChildren()) {
                    String childKey = transactionSnapshot.getKey();

                    // Create a new Transaction object and set its values
                    TransactionList transaction = new TransactionList();
                    transaction.setTransactionId(childKey);
                    transaction.setTransactionDate(transactionSnapshot.child("transaction_date").getValue(String.class));
                    transaction.setTransactionAmount(transactionSnapshot.child("transaction_amount").getValue(String.class));
                    transaction.setTransactionAccountType(transactionSnapshot.child("transaction_account_type").getValue(String.class));
                    transaction.setTransactionCategoryType(transactionSnapshot.child("transaction_category_type").getValue(String.class));
                    transaction.setTransactionDescription(transactionSnapshot.child("transaction_description").getValue(String.class));
                    transaction.setTransactionNote(transactionSnapshot.child("transaction_note").getValue(String.class));

                    // Add the transaction to the ArrayList
                    mTransactionList.add(transaction);

                    //sorting for mTransactionList
                    if(sort == Constants.AMOUNT_ONLY_SORT) {
                        Collections.sort(mTransactionList, new Comparator<TransactionList>() {
                            @Override
                            public int compare(TransactionList t1, TransactionList t2) {
                                //sorting from highest to lowest
                                return Double.compare(Double.parseDouble(t2.getTransactionAmount()), Double.parseDouble(t1.getTransactionAmount()));
                            }

                        });
                    } else if(sort == Constants.DATE_ONLY_SORT) {
                        Collections.sort(mTransactionList, new Comparator<TransactionList>() {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");

                            @Override
                            public int compare(TransactionList item1, TransactionList item2) {
                                try {
                                    Date date1 = dateFormat.parse(item1.getTransactionDate());
                                    Date date2 = dateFormat.parse(item2.getTransactionDate());
                                    return date2.compareTo(date1); // Descending order
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 0;
                            }
                        });
                    }
                }


                if (callback != null) {
                    callback.onFinish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (callback != null) {
                    callback.onError(databaseError.getMessage());
                }
            }
        });
    }
}
