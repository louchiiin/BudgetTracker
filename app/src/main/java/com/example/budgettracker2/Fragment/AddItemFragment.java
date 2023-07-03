package com.example.budgettracker2.Fragment;

import static com.example.budgettracker2.Activity.MainActivity.MY_TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.budgettracker2.CategoryOptionsManager;
import com.example.budgettracker2.Constants;
import com.example.budgettracker2.CustomMaxWidthFrameLayout;
import com.example.budgettracker2.Activity.EditActivity;
import com.example.budgettracker2.EnumDeclarations;
import com.example.budgettracker2.Interfaces.ManagerCallback;
import com.example.budgettracker2.Model.AccountsList;
import com.example.budgettracker2.Model.CategoryList;
import com.example.budgettracker2.Model.TransactionList;
import com.example.budgettracker2.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddItemFragment extends Fragment implements DatePickerDialogFragment.OnSelectedDate{
    private View mConvertView;
    private TextView mDatePickerTextView;
    private TextView mSelectAccount;
    private TextView mSelectCategory;
    private CardView mAccountsView;
    private CustomMaxWidthFrameLayout mAccountsChildView;
    private CardView mCategoryView;
    private CustomMaxWidthFrameLayout mCategoryChildView;
    private CardView mSaveView;
    private View mCloseAccount;
    private View mEditAccounts;
    private View mCloseCategory;
    private View mEditCategory;
    private Button mSave;
    private Button mContinue;
    private EditText mAmountView;
    private EditText mNoteView;
    private TextView mDescriptionView;
    private View mIncomeBtn;
    private View mExpenseBtn;
    private View mTransferBtn;
    private CategoryOptionsManager mCategoryOptionsManager;
    private int mOriginalDrawable;
    private int mClickedDrawable;
    private int mSelectedTransaction;
    private TransactionList mTransactions;
    private ActivityResultLauncher<Intent> mLauncher;
    private SpinKitView mAccountLoadingView;
    private SpinKitView mCategoryLoadingView;
    private ConstraintLayout mFullLoadingView;
    private boolean mIsAccountLoading = false;
    private boolean mIsCategoryLoading = false;
    private TextView mAccountTxtView;
    private TextView mCategoryTxtView;
    private int mSelectedAccount; //0 - From , 1 - To
    private boolean mIsUpdate = false;
    private int mListPosition;
    private String mTransactionType;
    private String mTransactionDate;
    private String mTransactionId;
    private String mTransactionAccountType;
    private String mTransactionCategoryType;
    private String mTransactionAmount;
    private String mTransactionNote;
    private String mTransactionDescription;

    public interface OnItemUpdateListener {
        void onItemUpdate(int position, String amount, String account, String category, String note, String description);
    }

    public OnItemUpdateListener mOnItemUpdateCallback;

    public void setOnItemUpdateListener(OnItemUpdateListener onItemUpdateListener) {
        this.mOnItemUpdateCallback = onItemUpdateListener;
    }

    public static Bundle createArguments(int position, boolean isUpdate, String transactionType, String transactionDate,
                                         String transactionId, String transactionAccountType,
                                         String transactionCategoryType, String transactionAmount,
                                         String transactionNote, String transactionDescription) {
        Bundle args = new Bundle();
        args.putInt("list_position", position);
        args.putBoolean(Constants.TRANSACTION_UPDATE, isUpdate);
        args.putString(Constants.TRANSACTION_TYPE, transactionType);
        args.putString(Constants.DATE_TRANSACTION_TYPE, transactionDate);
        args.putString(Constants.TRANSACTION_ID, transactionId);
        args.putString(Constants.TRANSACTION_ACCOUNT_TYPE, transactionAccountType);
        args.putString(Constants.TRANSACTION_CATEGORY_TYPE, transactionCategoryType);
        args.putString(Constants.TRANSACTION_AMOUNT, transactionAmount);
        args.putString(Constants.TRANSACTION_NOTE, transactionNote);
        args.putString(Constants.TRANSACTION_DESCRIPTION, transactionDescription);
        return args;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mConvertView = inflater.inflate(R.layout.fragment_add_item, container, false);

        mCategoryOptionsManager = CategoryOptionsManager.getInstance();

        mAccountLoadingView = mConvertView.findViewById(R.id.loading_spin_kit_account);
        mCategoryLoadingView = mConvertView.findViewById(R.id.loading_spin_kit_category);
        mFullLoadingView = mConvertView.findViewById(R.id.full_loading_view);
        mDatePickerTextView = mConvertView.findViewById(R.id.date_picker);
        mSelectAccount = mConvertView.findViewById(R.id.select_account);
        mSelectCategory = mConvertView.findViewById(R.id.select_category);

        mAccountsView = mConvertView.findViewById(R.id.bottom_layout_addItem_account);
        mAccountsChildView = mConvertView.findViewById(R.id.account_child_layout_account);

        mCategoryView = mConvertView.findViewById(R.id.bottom_layout_addItem_category);
        mCategoryChildView = mConvertView.findViewById(R.id.account_child_layout_category);

        mSaveView = mConvertView.findViewById(R.id.bottom_layout_saveItem);

        //accounts view
        mEditAccounts = mConvertView.findViewById(R.id.edit_accounts_list);
        mCloseAccount = mConvertView.findViewById(R.id.close_accounts_list);
        mAccountTxtView = mConvertView.findViewById(R.id.account_textView);
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
        mCategoryTxtView = mConvertView.findViewById(R.id.category_textView);

        //select account and select category is clickable only
        mSelectAccount.setFocusable(false);
        mSelectAccount.setClickable(true);

        mSelectCategory.setFocusable(false);
        mSelectCategory.setClickable(false);

        mSelectedTransaction = EnumDeclarations.EXPENSE.getValue();
        mOriginalDrawable = R.drawable.custom_button_black_stroke_white_fill;
        mClickedDrawable = R.drawable.custom_button_black_stroke_red_fill;
        updateButtonBackground(); //set default button background

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

        mTransactions = new TransactionList();
        Log.d(MY_TAG, "onCreateView: ");
        mLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Log.d(MY_TAG, "registerForActivityResult: result ok");
                fetchAccount();
                fetchCategory();
            }
        });

        getArgs();
        return mConvertView;
    }

    private void getArgs() {
        if(getArguments() != null) {
            mListPosition = getArguments().getInt("list_position", 0);
            mIsUpdate = getArguments().getBoolean(Constants.TRANSACTION_UPDATE);
            mTransactionType = getArguments().getString(Constants.TRANSACTION_TYPE);
            mTransactionDate = getArguments().getString(Constants.DATE_TRANSACTION_TYPE);
            mTransactionId = getArguments().getString(Constants.TRANSACTION_ID);
            mTransactionAccountType = getArguments().getString(Constants.TRANSACTION_ACCOUNT_TYPE);
            mTransactionCategoryType = getArguments().getString(Constants.TRANSACTION_CATEGORY_TYPE);
            mTransactionAmount = getArguments().getString(Constants.TRANSACTION_AMOUNT);
            mTransactionNote = getArguments().getString(Constants.TRANSACTION_NOTE);
            mTransactionDescription = getArguments().getString(Constants.TRANSACTION_DESCRIPTION);

            mSave.setText(getString(R.string.update));
            mDatePickerTextView.setText(mTransactionDate);
            mSelectAccount.setText(mTransactionAccountType);
            mSelectCategory.setText(mTransactionCategoryType);
            mAmountView.setText(mTransactionAmount);
            mNoteView.setText(mTransactionNote);
            mDescriptionView.setText(mTransactionDescription);
            if(mTransactionType.equals(StatsFragment.EXPENSES)) {
                mSelectedTransaction = EnumDeclarations.EXPENSE.getValue();
            } else if(mTransactionType.equals(StatsFragment.INCOME)) {
                mSelectedTransaction = EnumDeclarations.INCOME.getValue();
            } else {
                mSelectedTransaction = EnumDeclarations.TRANSFER.getValue();
            }
            updateButtonBackground();
        }
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
                    mSaveView.setVisibility(View.VISIBLE);
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
                    mSelectedTransaction = EnumDeclarations.INCOME.getValue();
                    updateButtonBackground();
                    updateAccountAndCategoryText(false);
                    clearFields(false);
                    mTransactions.setTransactionType(EnumDeclarations.INCOME.getValue());
                    break;
                }
                case R.id.expense_button: {
                    mSelectedTransaction = EnumDeclarations.EXPENSE.getValue();
                    updateButtonBackground();
                    updateAccountAndCategoryText(false);
                    clearFields(false);
                    mTransactions.setTransactionType(EnumDeclarations.EXPENSE.getValue());
                    break;
                }
                case R.id.transfer_button: {
                    mSelectedTransaction = EnumDeclarations.TRANSFER.getValue();
                    updateButtonBackground();
                    updateAccountAndCategoryText(true);
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
                    mAccountsView.setVisibility(View.VISIBLE);
                    mCategoryView.setVisibility(View.GONE);
                    mSaveView.setVisibility(View.INVISIBLE);
                    if(mSelectedTransaction == EnumDeclarations.TRANSFER.getValue()) {
                        mSelectedAccount = 0;
                    }
                    mAmountView.clearFocus();
                    mNoteView.clearFocus();
                    mSelectAccount.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_border_bottom, null));
                    fetchAccount();
                    closeKeyboard();
                    break;
                }
                case R.id.select_category:{
                    mSaveView.setVisibility(View.INVISIBLE);
                    if(mSelectedTransaction == EnumDeclarations.TRANSFER.getValue()) {
                        mSelectedAccount = 1;
                        mAccountsView.setVisibility(View.VISIBLE);
                        mCategoryView.setVisibility(View.GONE);
                    } else {
                        mAccountsView.setVisibility(View.GONE);
                        mCategoryView.setVisibility(View.VISIBLE);
                    }
                    mAmountView.clearFocus();
                    mNoteView.clearFocus();
                    mSelectCategory.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_border_bottom, null));
                    if(mSelectedAccount == 1) {
                        fetchAccount();
                    } else {
                        fetchCategory();
                    }
                    closeKeyboard();
                    break;
                }
                case R.id.close_accounts_list: {
                    mAccountsView.setVisibility(View.GONE);
                    mSaveView.setVisibility(View.VISIBLE);
                    closeKeyboard();
                    break;
                }
                case R.id.edit_category_list: {
                    if(getActivity() != null) {
                        Intent intent = new Intent(getActivity(), EditActivity.class);
                        intent.putExtra("header_title", "Edit Category");
                        intent.putExtra("transaction_type", Constants.CATEGORY_TYPE);
                        mLauncher.launch(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
                    }
                    break;
                }
                case R.id.edit_accounts_list: {
                    //create Intent to EditActivity
                    if(getActivity() != null) {
                        Intent intent = new Intent(getActivity(), EditActivity.class);
                        intent.putExtra("header_title", "Edit Accounts");
                        intent.putExtra("transaction_type", Constants.ACCOUNTS_TYPE);
                        mLauncher.launch(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
                    }
                    break;
                }
                case R.id.close_category_list: {
                    mCategoryView.setVisibility(View.GONE);
                    mSaveView.setVisibility(View.VISIBLE);
                    closeKeyboard();
                    break;
                }
                case R.id.save_button: {
                    mFullLoadingView.setVisibility(View.VISIBLE);

                    String datePickerText = mDatePickerTextView.getText().toString().trim();
                    String selectAccountText = mSelectAccount.getText().toString().trim();
                    String selectCategoryText = mSelectCategory.getText().toString().trim();
                    String amountText = mAmountView.getText().toString().trim();

                    if(datePickerText.equals("mm/dd/yyyy")) {
                        Log.d(MY_TAG, "date is blank");
                        mFullLoadingView.setVisibility(View.GONE);
                    }
                    if (selectAccountText.equals("")) {
                        Log.d(MY_TAG, "account is required");
                        mSelectAccount.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_border_focused, null));
                        mFullLoadingView.setVisibility(View.GONE);
                    }
                    if (selectCategoryText.equals("")) {
                        Log.d(MY_TAG, "category is required");
                        mSelectCategory.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_border_focused, null));
                        mFullLoadingView.setVisibility(View.GONE);
                    }
                    if (amountText.equals("")) {
                        Log.d(MY_TAG, "amount is required");
                        mAmountView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_border_focused, null));
                        mFullLoadingView.setVisibility(View.GONE);
                    }
                    if(mSelectedTransaction == 0) {
                        Log.d(MY_TAG, "select a transaction type");
                        mFullLoadingView.setVisibility(View.GONE);
                    }
                    if(mSelectedTransaction != 0 && !datePickerText.equals("mm/dd/yyyy") && !selectAccountText.equals("")
                            && !selectCategoryText.equals("") && !amountText.equals("")) {
                        if(mIsUpdate) {
                            updateTransaction();
                        } else {
                            saveTransaction();
                        }
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

    private void updateTransaction() {
        DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference();

        String amount = mAmountView.getText().toString();
        String account = mSelectAccount.getText().toString();
        String category = mSelectCategory.getText().toString();
        String note = mNoteView.getText().toString();
        String description = mDescriptionView.getText().toString();

        Map<String, Object> updates = new HashMap<>();
        updates.put(Constants.TRANSACTION_AMOUNT, amount);
        updates.put(Constants.TRANSACTION_ACCOUNT_TYPE, account);
        updates.put(Constants.TRANSACTION_CATEGORY_TYPE, category);
        updates.put(Constants.DATE_TRANSACTION_TYPE, mTransactionDate);
        updates.put(Constants.TRANSACTION_NOTE, note);
        updates.put(Constants.TRANSACTION_DESCRIPTION, description);

        transactionRef.child(getString(R.string.transactions_title))
                .child(mTransactionType)
                .child(mTransactionId)
                .updateChildren(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Update successful
                            if (mOnItemUpdateCallback != null) {
                                mOnItemUpdateCallback.onItemUpdate(mListPosition, amount, account, category, note, description);
                            }
                            mFullLoadingView.setVisibility(View.GONE);
                        } else {
                            // Update failed
                            mFullLoadingView.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void updateButtonBackground() {
        if(mSelectedTransaction == EnumDeclarations.EXPENSE.getValue()) {
            mIncomeBtn.setBackgroundResource(mOriginalDrawable);
            mExpenseBtn.setBackgroundResource(mClickedDrawable);
            mTransferBtn.setBackgroundResource(mOriginalDrawable);
        } else if(mSelectedTransaction == EnumDeclarations.INCOME.getValue()) {
            mIncomeBtn.setBackgroundResource(mClickedDrawable);
            mExpenseBtn.setBackgroundResource(mOriginalDrawable);
            mTransferBtn.setBackgroundResource(mOriginalDrawable);
        } else if(mSelectedTransaction == EnumDeclarations.TRANSFER.getValue()) {
            mIncomeBtn.setBackgroundResource(mOriginalDrawable);
            mExpenseBtn.setBackgroundResource(mOriginalDrawable);
            mTransferBtn.setBackgroundResource(mClickedDrawable);
        }
    }

    private void updateAccountAndCategoryText(boolean isTransferBtnClicked) {
        if(isTransferBtnClicked) {
            mAccountTxtView.setText(getString(R.string.common_from));
            mCategoryTxtView.setText(getString(R.string.common_to));
        } else {
            mAccountTxtView.setText(getString(R.string.accounts));
            mCategoryTxtView.setText(getString(R.string.category));
        }
    }

    private void saveTransaction() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        // Create a data object or a HashMap with the data to be saved
        String dateOfTransaction = mDatePickerTextView.getText().toString();
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String nowDate = dateFormat.format(currentDate);
        String newUserId = databaseRef.push().getKey();

        HashMap<String, Object> userData = new HashMap<>();
        userData.put(Constants.DATE_TRANSACTION_TYPE, dateOfTransaction != null && !dateOfTransaction.equals("mm/dd/yyyy") ? dateOfTransaction : nowDate);
        userData.put(Constants.TRANSACTION_ID, newUserId);
        userData.put(Constants.TRANSACTION_ACCOUNT_TYPE, mSelectAccount.getText().toString());
        userData.put(Constants.TRANSACTION_CATEGORY_TYPE, mSelectCategory.getText().toString());
        userData.put(Constants.TRANSACTION_AMOUNT, mAmountView.getText().toString());
        userData.put(Constants.TRANSACTION_NOTE, mNoteView.getText().toString());
        userData.put(Constants.TRANSACTION_DESCRIPTION, mDescriptionView.getText().toString());

        String transactionType = "";
        if(mSelectedTransaction == EnumDeclarations.INCOME.getValue()) {
            transactionType = getString(R.string.income_title);
        } else if(mSelectedTransaction == EnumDeclarations.EXPENSE.getValue()) {
            transactionType = getString(R.string.expenses_title);
        } else if(mSelectedTransaction == EnumDeclarations.TRANSFER.getValue()) {
            transactionType = getString(R.string.transfer_title);
        }

        // Save the data to the database
        databaseRef.child(getString(R.string.transactions_title))
                .child(transactionType).child(newUserId != null ? newUserId : "").setValue(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data successfully saved
                        mFullLoadingView.setVisibility(View.GONE);
                        clearFields(true);
                        Log.d(MY_TAG, "Successfully Saved");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // An error occurred while saving the data
                        mFullLoadingView.setVisibility(View.GONE);
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
            mSelectedTransaction = EnumDeclarations.EXPENSE.getValue(); //default value is expense
            mIncomeBtn.setBackgroundResource(mOriginalDrawable);
            mExpenseBtn.setBackgroundResource(mClickedDrawable);
            mTransferBtn.setBackgroundResource(mOriginalDrawable);

            mDatePickerTextView.setText("");
            mSelectAccount.setText("");
            mSelectCategory.setText("");
            mAmountView.setText("");
            mNoteView.setText("");
            mDescriptionView.setText("");
        }
    }

    private void fetchCategory() {
        if(mIsCategoryLoading)  {
            return;
        }
        ArrayList<CategoryList> categoryLists = new ArrayList<CategoryList>();
        categoryLists = mCategoryOptionsManager.mCategoriesList;
        mCategoryChildView.removeAllViews();
        mIsCategoryLoading = true;
        // Fetch list account if null or empty
        if (categoryLists == null || categoryLists.isEmpty()) {
            mCategoryLoadingView.setVisibility(View.VISIBLE);
            mCategoryOptionsManager.requestFetchCategory(new ManagerCallback() {
                @Override
                public void onFinish() {
                    populateCategoryButtons(mCategoryOptionsManager.mCategoriesList);
                    mCategoryLoadingView.setVisibility(View.GONE);
                    mIsCategoryLoading = false;
                }

                @Override
                public void onError(String error) {
                    // Handle error
                    mIsCategoryLoading = false;
                }
            });
        } else {
            mIsCategoryLoading = false;
            populateCategoryButtons(categoryLists);
        }
    }

    private void fetchAccount() {
        if(mIsAccountLoading) {
            return;
        }
        ArrayList<AccountsList> listAccounts = new ArrayList<AccountsList>();
        listAccounts = mCategoryOptionsManager.mAccountsList;
        mAccountsChildView.removeAllViews();
        mIsAccountLoading = true;

        // Fetch list account if null or empty
        if (listAccounts == null || listAccounts.isEmpty()) {
            mAccountLoadingView.setVisibility(View.VISIBLE);
            mCategoryOptionsManager.requestFetchAccount(new ManagerCallback() {
                @Override
                public void onFinish() {
                    populateAccountButtons(mCategoryOptionsManager.mAccountsList);
                    mAccountLoadingView.setVisibility(View.GONE);
                    mIsAccountLoading = false;
                }

                @Override
                public void onError(String error) {
                    mIsAccountLoading = false;
                }
            });
        } else {
            mIsAccountLoading = false;
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
                    if(mSelectedAccount == 1) {
                        mSelectCategory.setText(buttonText);
                    } else {
                        mSelectAccount.setText(buttonText);
                    }
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