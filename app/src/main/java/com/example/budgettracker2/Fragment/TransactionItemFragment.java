package com.example.budgettracker2.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgettracker2.Adapter.TransactionItemAdapter;
import com.example.budgettracker2.Constants;
import com.example.budgettracker2.Model.TransactionList;
import com.example.budgettracker2.R;
import com.google.gson.Gson;

import java.util.ArrayList;

public class TransactionItemFragment extends Fragment implements AddItemFragment.OnItemUpdateListener{

    public interface OnRefreshCallback {
        void onRefreshPage();
    }

    public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
        mOnRefreshCallback = refreshCallback;
    }

    private OnRefreshCallback mOnRefreshCallback;
    private TransactionItemAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<TransactionList> mTransactionArrayList;
    private ArrayList<String> mTransactionListCombinedIds;
    private ArrayList<TransactionList> mTransactionArrayListCombined;

    private TransactionList mTransactionList;
    private String mTransactionType;
    private boolean mIsUpdate = false;

    public TransactionItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_transaction_item, container, false);
        
        mRecyclerView = view.findViewById(R.id.transaction_item_view);

        getArgs();
        checkArgs();
        return view;
    }

    private void checkArgs() {
        mTransactionArrayListCombined = new ArrayList<TransactionList>();
        for (String id : mTransactionListCombinedIds) {
            TransactionList transaction = new TransactionList();
            transaction.setTransactionId(id);

            // Check if the id exists in the transactionList
            for (TransactionList existingTransaction : mTransactionArrayList) {
                if (existingTransaction.getTransactionId().equals(id)) {
                    // The id already exists in transactionList
                    existingTransaction.setTransactionCombinedAmount(null); //set to null since we don't need it
                    mTransactionArrayListCombined.add(existingTransaction);
                    break;
                }
            }
        }

        initializeRecyclerView();
    }

    private void getArgs() {
        if(getArguments() != null) {
            mTransactionList = (TransactionList) getArguments().getParcelable("transaction_list");
            mTransactionListCombinedIds = new ArrayList<String>();
            mTransactionListCombinedIds = mTransactionList.getCombinedIds();
            mTransactionArrayList = getArguments().getParcelableArrayList("transaction_array_list");
            mTransactionType = getArguments().getString(Constants.TRANSACTION_TYPE);
        }
    }



    private void initializeRecyclerView() {
        mAdapter = new TransactionItemAdapter(getActivity(), mTransactionArrayListCombined, mTransactionType, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemUpdate(int position, String amount, String account, String category, String note, String description) {
        mTransactionArrayListCombined.get(position).setTransactionAmount(amount);
        mTransactionArrayListCombined.get(position).setTransactionAccountType(account);
        mTransactionArrayListCombined.get(position).setTransactionCategoryType(category);
        mTransactionArrayListCombined.get(position).setTransactionNote(note);
        mTransactionArrayListCombined.get(position).setTransactionDescription(description);
        mAdapter.notifyItemChanged(position);

        if(mOnRefreshCallback != null) {
            mOnRefreshCallback.onRefreshPage();
        }

        if(getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}