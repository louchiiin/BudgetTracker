package com.example.budgettracker2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.budgettracker2.Model.AccountsList;
import com.example.budgettracker2.Model.CategoryList;
import com.example.budgettracker2.Model.Transactions;
import com.google.gson.Gson;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    private ImageView mCloseButton;
    private ImageView mAddButton;
    private TextView mHeaderTitle;
    private String mTitle;
    private String mTransactionType;
    private RecyclerView mRecyclerView;
    private EditAdapter mEditAdapter;
    private CategoryOptionsManager mCategoryOptionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mCategoryOptionsManager = CategoryOptionsManager.getInstance();

        mCloseButton = findViewById(R.id.actionBar_open_sideMenu);
        mAddButton = findViewById(R.id.actionBar_add);
        mHeaderTitle = findViewById(R.id.actionBar_title);
        mRecyclerView = findViewById(R.id.edit_recyclerView);

        //get intent
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            mTitle = bundle.getString("header_title");
            mTransactionType = bundle.getString("transaction_type");
        }

        mCloseButton.setOnClickListener(mOnClickListener);
        mAddButton.setOnClickListener(mOnClickListener);
        //initialize header
        initializeHeader();
        initializeRecyclerViewAdapter();

    }

    private void initializeRecyclerViewAdapter() {
        ArrayList<AccountsList> accountsLists = mCategoryOptionsManager.mListAccounts;
        ArrayList<CategoryList> categoryLists = mCategoryOptionsManager.mCategoryList;

        Log.d(MainActivity.MY_TAG, "test " + mTransactionType);
        mEditAdapter = new EditAdapter(this, mTransactionType.equals(AddItemFragment.ACCOUNTS_TYPE) ? accountsLists : categoryLists);
        mRecyclerView.setAdapter(mEditAdapter);
    }

    private void initializeHeader() {
        mHeaderTitle.setText(mTitle);
        mCloseButton.setImageResource(R.drawable.ic_back_black);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.actionBar_open_sideMenu: {
                    Intent resultIntent = new Intent();
                    // Set any result data you want to pass back to the calling fragment
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                    overridePendingTransition(R.anim.slide_out, 0);
                    break;
                }
                case R.id.actionBar_add: {
                    //create intent to edit activity
                    Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
                    break;
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        // Set any result data you want to pass back to the calling fragment
        setResult(Activity.RESULT_OK, resultIntent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out, 0);
    }
}