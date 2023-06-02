package com.example.budgettracker2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

        //initialize header
        initializeHeader();
        initializeRecyclerViewAdapter();

        mCloseButton.setOnClickListener(mOnClickListener);
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
        mAddButton.setVisibility(View.GONE);
        mCloseButton.setImageResource(R.drawable.ic_back_black);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.actionBar_open_sideMenu:
                    finish();
                    overridePendingTransition(R.anim.slide_out, 0);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_out, 0);
    }
}