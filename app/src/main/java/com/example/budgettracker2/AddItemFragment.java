package com.example.budgettracker2;

import static com.example.budgettracker2.MainActivity.MY_TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.budgettracker2.Model.AccountsList;
import com.example.budgettracker2.Model.CategoryList;
import com.example.budgettracker2.Model.Transactions;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

import org.json.JSONObject;

public class AddItemFragment extends Fragment implements DatePickerDialogFragment.OnSelectedDate{
    public static String ACCOUNTS_TYPE = "accounts_type";
    public static String CATEGORY_TYPE = "category_type";
    private View mConvertView;
    private TextView mDatePickerTextView;
    private TextView mSelectAccount;
    private TextView mSelectCategory;
    private CardView mAccountsView;
    private CustomMaxWidthFrameLayout mAccountsChildView;
    private CardView mCategoryView;
    private CustomMaxWidthFrameLayout mCategoryChildView;
    private View mCloseAccount;
    private View mEditAccounts;
    private View mCloseCategory;
    private View mEditCategory;
    private Button mSave;
    private Button mContinue;
    private EditText mAmountView;
    private EditText mNoteView;
    private TextView mDescriptionView;
    private ProgressDialog mProgress;
    private Button mIncomeBtn;
    private Button mExpenseBtn;
    private Button mTransferBtn;
    private CategoryOptionsManager mCategoryOptionsManager;
    private int mOriginalDrawable;
    private int mClickedDrawable;
    private int mSelectedTransaction;
    private Transactions mTransactions;
    private ActivityResultLauncher<Intent> mLauncher;
    private SpinKitView mAccountLoadingView;
    private SpinKitView mCategoryLoadingView;
    private boolean mIsAccountLoading = false;
    private boolean mIsCategoryLoading = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mConvertView = inflater.inflate(R.layout.fragment_add_item, container, false);

        mCategoryOptionsManager = CategoryOptionsManager.getInstance();

        mAccountLoadingView = mConvertView.findViewById(R.id.loading_spin_kit_account);
        mCategoryLoadingView = mConvertView.findViewById(R.id.loading_spin_kit_category);
        mDatePickerTextView = mConvertView.findViewById(R.id.date_picker);
        mSelectAccount = mConvertView.findViewById(R.id.select_account);
        mSelectCategory = mConvertView.findViewById(R.id.select_category);

        mAccountsView = mConvertView.findViewById(R.id.bottom_layout_addItem_account);
        mAccountsChildView = mConvertView.findViewById(R.id.account_child_layout_account);

        mCategoryView = mConvertView.findViewById(R.id.bottom_layout_addItem_category);
        mCategoryChildView = mConvertView.findViewById(R.id.account_child_layout_category);

        //accounts view
        mEditAccounts = mConvertView.findViewById(R.id.edit_accounts_list);
        mCloseAccount = mConvertView.findViewById(R.id.close_accounts_list);
        //category view
        mEditCategory = mConvertView.findViewById(R.id.edit_category_list);
        mCloseCategory = mConvertView.findViewById(R.id.close_category_list);
        mSave = mConvertView.findViewById(R.id.save_button);
        mContinue = mConvertView.findViewById(R.id.continue_button);

        mAmountView = mConvertView.findViewById(R.id.amount_textView);
        mNoteView = mConvertView.findViewById(R.id.note_textView);
        mDescriptionView = mConvertView.findViewById(R.id.description_textView);
        mIncomeBtn = mConvertView.findViewById(R.id.income_button);
        mExpenseBtn = mConvertView.findViewById(R.id.expense_button);
        mTransferBtn = mConvertView.findViewById(R.id.transfer_button);

        //select account and select category is clickable only
        mSelectAccount.setFocusable(false);
        mSelectAccount.setClickable(true);

        mSelectCategory.setFocusable(false);
        mSelectCategory.setClickable(false);

        mOriginalDrawable = R.drawable.custom_button_black_stroke_white_fill;
        mClickedDrawable = R.drawable.custom_button_black_stroke_red_fill;

        mIncomeBtn.setOnClickListener(mOnClickListener);
        mExpenseBtn.setOnClickListener(mOnClickListener);
        mTransferBtn.setOnClickListener(mOnClickListener);
        mEditAccounts.setOnClickListener(mOnClickListener);
        mCloseAccount.setOnClickListener(mOnClickListener);
        mEditCategory.setOnClickListener(mOnClickListener);
        mCloseCategory.setOnClickListener(mOnClickListener);
        mSelectAccount.setOnClickListener(mOnClickListener);
        mSelectCategory.setOnClickListener(mOnClickListener);
        mDatePickerTextView.setOnClickListener(mOnClickListener);
        mSave.setOnClickListener(mOnClickListener);
        mContinue.setOnClickListener(mOnClickListener);
        mAmountView.setOnFocusChangeListener(mOnFocusChangeListener);
        mNoteView.setOnFocusChangeListener(mOnFocusChangeListener);

        mTransactions = new Transactions();
        Log.d(MY_TAG, "onCreateView: ");
        mLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Log.d(MY_TAG, "registerForActivityResult: result ok");
                fetchAccount();
                fetchCategory();
            }
        });
        return mConvertView;
    }

    private final View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onFocusChange(View view, boolean b) {
            switch (view.getId()) {
                case R.id.note_textView:
                case R.id.amount_textView: {
                    mAccountsView.setVisibility(View.GONE);
                    mCategoryView.setVisibility(View.GONE);
                    if(R.id.amount_textView == view.getId()) {
                        mAmountView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_border_bottom, null));
                    }
                    break;
                }
            }
        }
    };

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.income_button: {
                    mIncomeBtn.setBackgroundResource(mClickedDrawable);
                    mExpenseBtn.setBackgroundResource(mOriginalDrawable);
                    mTransferBtn.setBackgroundResource(mOriginalDrawable);
                    mIncomeBtn.setTextColor(getResources().getColor(R.color.white));
                    mExpenseBtn.setTextColor(getResources().getColor(R.color.black));
                    mTransferBtn.setTextColor(getResources().getColor(R.color.black));
                    mSelectedTransaction = EnumDeclarations.INCOME.getValue();
                    clearFields(false);
                    mTransactions.setTransactionType(EnumDeclarations.INCOME.getValue());
                    break;
                }
                case R.id.expense_button: {
                    mIncomeBtn.setBackgroundResource(mOriginalDrawable);
                    mExpenseBtn.setBackgroundResource(mClickedDrawable);
                    mTransferBtn.setBackgroundResource(mOriginalDrawable);
                    mIncomeBtn.setTextColor(getResources().getColor(R.color.black));
                    mExpenseBtn.setTextColor(getResources().getColor(R.color.white));
                    mTransferBtn.setTextColor(getResources().getColor(R.color.black));
                    mSelectedTransaction = EnumDeclarations.EXPENSE.getValue();
                    clearFields(false);
                    mTransactions.setTransactionType(EnumDeclarations.EXPENSE.getValue());
                    break;
                }
                case R.id.transfer_button: {
                    mIncomeBtn.setBackgroundResource(mOriginalDrawable);
                    mExpenseBtn.setBackgroundResource(mOriginalDrawable);
                    mTransferBtn.setBackgroundResource(mClickedDrawable);
                    mIncomeBtn.setTextColor(getResources().getColor(R.color.black));
                    mExpenseBtn.setTextColor(getResources().getColor(R.color.black));
                    mTransferBtn.setTextColor(getResources().getColor(R.color.white));
                    mSelectedTransaction = EnumDeclarations.TRANSFER.getValue();
                    clearFields(false);
                    mTransactions.setTransactionType(EnumDeclarations.TRANSFER.getValue());
                    break;
                }
                case R.id.date_picker:{
                    if(getActivity() != null) {
                        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
                        fragment.setOnSelectedDateCallback(AddItemFragment.this);
                        fragment.show(getActivity().getSupportFragmentManager(), "date_picker");
                    }
                    break;
                }
                case R.id.select_account:{
                    Log.d(MY_TAG, "select account");
                    mAccountsView.setVisibility(View.VISIBLE);
                    mCategoryView.setVisibility(View.GONE);
                    mAmountView.clearFocus();
                    mNoteView.clearFocus();
                    mSelectAccount.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_border_bottom, null));
                    fetchAccount();
                    closeKeyboard();
                    break;
                }
                case R.id.select_category:{
                    mAccountsView.setVisibility(View.GONE);
                    mCategoryView.setVisibility(View.VISIBLE);
                    mAmountView.clearFocus();
                    mNoteView.clearFocus();
                    mSelectCategory.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_border_bottom, null));
                    Log.d(MY_TAG, "select category");
                    fetchCategory();
                    closeKeyboard();
                    break;
                }
                case R.id.close_accounts_list: {
                    mAccountsView.setVisibility(View.GONE);
                    closeKeyboard();
                    break;
                }
                case R.id.edit_category_list: {
                    Log.d(MY_TAG, "edit category");
                    if(getActivity() != null) {
                        Intent intent = new Intent(getActivity(), EditActivity.class);
                        intent.putExtra("header_title", "Edit Category");
                        intent.putExtra("transaction_type", CATEGORY_TYPE);
                        mLauncher.launch(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
                    }
                    break;
                }
                case R.id.edit_accounts_list: {
                    Log.d(MY_TAG, "edit accounts");
                    //create Intent to EditActivity
                    if(getActivity() != null) {
                        Intent intent = new Intent(getActivity(), EditActivity.class);
                        intent.putExtra("header_title", "Edit Accounts");
                        intent.putExtra("transaction_type", ACCOUNTS_TYPE);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
                    }
                    break;
                }
                case R.id.close_category_list: {
                    mCategoryView.setVisibility(View.GONE);
                    closeKeyboard();
                    break;
                }
                case R.id.save_button: {
                    mProgress = new ProgressDialog(getContext());
                    mProgress.setMessage("Loading..."); // Set the message to be displayed
                    mProgress.setCancelable(false);
                    mProgress.show();

                    String datePickerText = mDatePickerTextView.getText().toString().trim();
                    String selectAccountText = mSelectAccount.getText().toString().trim();
                    String selectCategoryText = mSelectCategory.getText().toString().trim();
                    String amountText = mAmountView.getText().toString().trim();

                    if(datePickerText.equals("mm/dd/yyyy")) {
                        Log.d(MY_TAG, "date is blank");
                        mProgress.dismiss();
                    }
                    if (selectAccountText.equals("")) {
                        Log.d(MY_TAG, "account is required");
                        mSelectAccount.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_border_focused, null));
                        mProgress.dismiss();
                    }
                    if (selectCategoryText.equals("")) {
                        Log.d(MY_TAG, "category is required");
                        mSelectCategory.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_border_focused, null));
                        mProgress.dismiss();
                    }
                    if (amountText.equals("")) {
                        Log.d(MY_TAG, "amount is required");
                        mAmountView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_border_focused, null));
                        mProgress.dismiss();
                    }
                    if(mSelectedTransaction == 0) {
                        Log.d(MY_TAG, "select a transaction type");
                    }
                    if(mSelectedTransaction != 0 && !datePickerText.equals("mm/dd/yyyy") && !selectAccountText.equals("")
                            && !selectCategoryText.equals("") && !amountText.equals("")) {
                        saveTransaction();
                    }
                    break;
                }
                case R.id.continue_button: {
                    //should next
                    Log.d(MY_TAG, "continue");
                    break;
                }
            }
        }
    };

    private void saveTransaction() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        // Create a data object or a HashMap with the data to be saved
        String dateOfTransaction = mDatePickerTextView.getText().toString();
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String nowDate = dateFormat.format(currentDate);

        HashMap<String, Object>
        userData = new HashMap<>();
        userData.put("date_of_transaction", dateOfTransaction != null && !dateOfTransaction.equals("mm/dd/yyyy") ? dateOfTransaction : nowDate);
        userData.put("account_type", mSelectAccount.getText().toString());
        userData.put("category_type", mSelectCategory.getText().toString());
        userData.put("amount", mAmountView.getText().toString());
        userData.put("note", mNoteView.getText().toString());
        userData.put("transaction_description", mDescriptionView.getText().toString());

        String transactionType = "";
        if(mSelectedTransaction == EnumDeclarations.INCOME.getValue()) {
            transactionType = "Income";
        } else if(mSelectedTransaction == EnumDeclarations.EXPENSE.getValue()) {
            transactionType = "Expenses";
        } else if(mSelectedTransaction == EnumDeclarations.TRANSFER.getValue()) {
            transactionType = "Transfer";
        }

        //insert unique ID
        String newUserId = databaseRef.push().getKey();
        // Save the data to the database
        databaseRef.child("Transactions")
                .child(transactionType).child(newUserId != null ? newUserId : "").setValue(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data successfully saved
                        // Perform any additional operations if needed
                        mProgress.dismiss();
                        clearFields(true);
                        Log.d(MY_TAG, "Successfully Saved");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // An error occurred while saving the data
                        // Handle the error appropriately
                        mProgress.dismiss();
                        Log.d(MY_TAG, "Failure! Did not saved successfully");
                    }
                });
    }

    private void clearFields(boolean isClearAll) {
        if(mSelectedTransaction != mTransactions.getTransactionType()) {
            mDatePickerTextView.setText("");
            mSelectAccount.setText("");
            mSelectCategory.setText("");
            mAmountView.setText("");
            mNoteView.setText("");
            mDescriptionView.setText("");
        }

        mSelectAccount.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_border_bottom, null));
        mSelectCategory.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_border_bottom, null));
        mAmountView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_border_bottom, null));

        if(isClearAll) {
            mSelectedTransaction = 0;
            mIncomeBtn.setBackgroundResource(mOriginalDrawable);
            mExpenseBtn.setBackgroundResource(mOriginalDrawable);
            mTransferBtn.setBackgroundResource(mOriginalDrawable);
            mIncomeBtn.setTextColor(getResources().getColor(R.color.black));
            mExpenseBtn.setTextColor(getResources().getColor(R.color.black));
            mTransferBtn.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void fetchCategory() {
        if(mIsCategoryLoading)  {
            return;
        }
        ArrayList<CategoryList> categoryLists = new ArrayList<CategoryList>();
        categoryLists = mCategoryOptionsManager.mCategoryList;
        mCategoryChildView.removeAllViews();
        mIsCategoryLoading = true;
        // Fetch list account if null or empty
        if (categoryLists == null || categoryLists.isEmpty()) {
            mCategoryLoadingView.setVisibility(View.VISIBLE);
            mCategoryOptionsManager.requestFetchCategory(new ManagerCallback() {
                @Override
                public void onFinish() {
                    populateCategoryButtons(mCategoryOptionsManager.mCategoryList);
                    mCategoryLoadingView.setVisibility(View.GONE);
                    mIsCategoryLoading = false;
                }

                @Override
                public void onError(String error) {
                    // Handle error
                }
            });
        } else {
            populateCategoryButtons(categoryLists);
        }
    }

    private void fetchAccount() {
        if(mIsAccountLoading) {
            return;
        }
        ArrayList<AccountsList> listAccounts = new ArrayList<AccountsList>();
        listAccounts = mCategoryOptionsManager.mListAccounts;
        mAccountsChildView.removeAllViews();
        mIsAccountLoading = true;

        // Fetch list account if null or empty
        if (listAccounts == null || listAccounts.isEmpty()) {
            mAccountLoadingView.setVisibility(View.VISIBLE);
            mCategoryOptionsManager.requestFetchAccount(new ManagerCallback() {
                @Override
                public void onFinish() {
                    populateAccountButtons(mCategoryOptionsManager.mListAccounts);
                    mAccountLoadingView.setVisibility(View.GONE);
                    mIsAccountLoading = false;
                }

                @Override
                public void onError(String error) {
                    // Handle error
                }
            });
        } else {
            populateAccountButtons(listAccounts);
        }
    }

    private void populateAccountButtons(ArrayList<AccountsList> accounts) {
        for (AccountsList accountList : accounts) {
            Button button = createButton(accountList.getAccountName(), true);
            mAccountsChildView.addView(button);
        }
    }

    private void populateCategoryButtons(ArrayList<CategoryList> categoryLists) {
        for (CategoryList list : categoryLists) {
            Button button = createButton(list.getCategoryName(), false);
            mCategoryChildView.addView(button);
        }
    }

    private Button createButton(String buttonText, boolean isAccount) {
        Button button = new Button(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 10); // Set margins (left, top, right, bottom)
        button.setLayoutParams(params);
        button.setText(buttonText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAccount) {
                    mSelectAccount.setText(buttonText);
                } else {
                    mSelectCategory.setText(buttonText);
                }
            }
        });
        return button;
    }

    private void closeKeyboard() {
        if(getActivity() == null) {
            return;
        }

        View view = getActivity().getWindow().getDecorView().getRootView();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onSelect(String date) {
        if(mDatePickerTextView != null) {
            mDatePickerTextView.setText(date);
        }
    }
}