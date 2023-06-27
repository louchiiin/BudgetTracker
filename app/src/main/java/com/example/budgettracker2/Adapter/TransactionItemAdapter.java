package com.example.budgettracker2.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker2.Model.TransactionList;
import com.example.budgettracker2.R;

import java.util.ArrayList;

public class TransactionItemAdapter extends RecyclerView.Adapter<TransactionItemAdapter.TransactionItemHolder>{
    private Activity mActivity;
    private ArrayList<?> mDataList;

    //create a constructor for the adapter
    public TransactionItemAdapter(Activity activity, ArrayList<?> list) {
        this.mActivity = activity;
        this.mDataList = list;
    }

    @NonNull
    @Override
    public TransactionItemAdapter.TransactionItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.stats_item_list_layout, parent, false);
        return new TransactionItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionItemAdapter.TransactionItemHolder holder, int position) {
        TransactionList transactionList = (TransactionList) mDataList.get(position);
        holder.mAccountNameView.setText(transactionList.getTransactionAccountType());
        holder.mCategoryNameView.setText(transactionList.getTransactionCategoryType());
        holder.mAmountView.setText(transactionList.getTransactionCombinedAmount() == null ? transactionList.getTransactionAmount() : transactionList.getTransactionCombinedAmount());
        holder.mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LOUCHIIIN" , transactionList.getTransactionId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    public static class TransactionItemHolder extends RecyclerView.ViewHolder {
        private TextView mAccountNameView;
        private TextView mCategoryNameView;
        private TextView mAmountView;
        private ConstraintLayout mItem;
        public TransactionItemHolder(@NonNull View itemView) {
            super(itemView);

            mAccountNameView = itemView.findViewById(R.id.account_textView);
            mCategoryNameView = itemView.findViewById(R.id.category_textView);
            mAmountView = itemView.findViewById(R.id.amount_textView);
            mItem = itemView.findViewById(R.id.transaction_item_container);
        }
    }
}
