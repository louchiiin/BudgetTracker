package com.example.budgettracker2.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.budgettracker2.Adapter.EditAdapter;
import com.example.budgettracker2.CategoryOptionsManager;
import com.example.budgettracker2.Constants;
import com.example.budgettracker2.Interfaces.ManagerCallback;
import com.example.budgettracker2.Model.AccountsList;
import com.example.budgettracker2.Model.CategoryList;
import com.example.budgettracker2.R;
import com.google.gson.Gson;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity implements EditAdapter.OnUpdateListener{

    private ImageView mCloseButton;
    private ImageView mAddButton;
    private TextView mHeaderTitle;
    private String mTitle;
    private String mTransactionType;
    private RecyclerView mRecyclerView;
    private EditAdapter mEditAdapter;
    private CategoryOptionsManager mCategoryOptionsManager;
    private ActivityResultContract<Intent, ActivityResult> mEditActivityContract = new ActivityResultContracts.StartActivityForResult();
    private ConstraintLayout mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mCategoryOptionsManager = CategoryOptionsManager.getInstance();

        mCloseButton = findViewById(R.id.actionBar_open_sideMenu);
        mAddButton = findViewById(R.id.actionBar_add);
        mHeaderTitle = findViewById(R.id.actionBar_title);
        mRecyclerView = findViewById(R.id.edit_recyclerView);
        mLoadingView = findViewById(R.id.full_loading_view);

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
        ArrayList<AccountsList> accountsLists = mCategoryOptionsManager.mAccountsList;
        ArrayList<CategoryList> categoryLists = mCategoryOptionsManager.mCategoriesList;

        mEditAdapter = new EditAdapter(this, mTransactionType.equals(Constants.ACCOUNTS_TYPE) ? accountsLists : categoryLists, this);
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
                    setResult(Activity.RESULT_OK);
                    finish();
                    overridePendingTransition(R.anim.slide_out, 0);
                    break;
                }
                case R.id.actionBar_add: {
                    Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                    intent.putExtra("category_type", mTransactionType);
                    mEditActivityLauncher.launch(intent);
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

    ActivityResultLauncher<Intent> mEditActivityLauncher = registerForActivityResult(mEditActivityContract, result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            // Handle the result here
            Intent data = result.getData();
            // Process the data from EditActivity
            if (data != null) {
                String categoryType = data.getStringExtra("category_type");
                fetchTypeResult(categoryType);
            }
        }
    });

    private void fetchTypeResult(String categoryType) {
        if(categoryType.equals(Constants.CATEGORY_TYPE)) {
            mCategoryOptionsManager.requestFetchCategory(new ManagerCallback() {
                @Override
                public void onFinish() {
                    initializeRecyclerViewAdapter();
                }

                @Override
                public void onError(String error) {
                    Log.d("LOUCHIIIN", "onError");
                }
            });
        } else {
            mCategoryOptionsManager.requestFetchAccount(new ManagerCallback() {
                @Override
                public void onFinish() {
                    initializeRecyclerViewAdapter();
                }

                @Override
                public void onError(String error) {
                    Log.d("LOUCHIIIN", "onError");
                }
            });
        }
    }

    @Override
    public void onUpdate() {
        mLoadingView.setVisibility(View.GONE);
        initializeRecyclerViewAdapter();
    }

    @Override
    public void onLoadingShow() {
        mLoadingView.setVisibility(View.VISIBLE);
    }
}