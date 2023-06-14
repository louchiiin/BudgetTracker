package com.example.budgettracker2.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker2.CategoryOptionsManager;
import com.example.budgettracker2.Model.AccountsList;
import com.example.budgettracker2.Model.TransactionList;
import com.example.budgettracker2.R;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private Activity mActivity;
    private ArrayList<?> mDataList;
    int mTotal = 0;
    public TransactionAdapter(Activity activity, ArrayList<?> list) {
        this.mActivity = activity;
        this.mDataList = list;
        calculateTotal();
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.stats_list_layout, parent, false);
        return new ViewHolder(view);
    }

    private void calculateTotal() {
        for (Object obj : mDataList) {
            if (obj instanceof TransactionList) {
                TransactionList transaction = (TransactionList) obj;
                int amount = Integer.parseInt(transaction.getTransactionAmount());
                mTotal += amount;
            } else if (obj instanceof AccountsList) {
                AccountsList account = (AccountsList) obj;
                // Perform operations specific to AccountList
                // ...
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        TransactionList transactionList = (TransactionList) mDataList.get(position);
        String amountWithCurrency = CategoryOptionsManager.getInstance().getCurrency().concat(" ").concat(transactionList.getTransactionAmount());
        holder.mCategoryName.setText(transactionList.getTransactionCategoryType());
        holder.mAmountView.setText(amountWithCurrency);
        int amount = Integer.parseInt(transactionList.getTransactionAmount());
        double percentage = ((double) amount / mTotal) * 100;
        long roundedPercentage = Math.round(percentage);
        String percentageText = roundedPercentage + "%";

        holder.mPercentage.setText(percentageText);
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mPercentage;
        private TextView mCategoryName;
        private TextView mAmountView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //initialize all textview
            mPercentage = itemView.findViewById(R.id.percentage_indicator);
            mCategoryName = itemView.findViewById(R.id.category_textView);
            mAmountView = itemView.findViewById(R.id.amount_textView);
        }
    }
}
