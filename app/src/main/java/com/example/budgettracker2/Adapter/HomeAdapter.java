package com.example.budgettracker2.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker2.Model.TransactionList;
import com.example.budgettracker2.R;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_GROUP_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private Activity mActivity;
    private ArrayList<TransactionList> mDataList;

    public HomeAdapter(Activity activity, ArrayList<TransactionList> list) {
        this.mActivity = activity;
        this.mDataList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.home_selection_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = mDataList.get(position);

        ViewHolder itemViewHolder = (ViewHolder) holder;
        TransactionList transaction = (TransactionList) item;
        itemViewHolder.mAccountNameView.setText(transaction.getTransactionAccountType());
        itemViewHolder.mAmountView.setText(transaction.getTransactionAmount());
        itemViewHolder.mCategoryView.setText(transaction.getTransactionCategoryType());
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mAccountNameView;
        private TextView mAmountView;
        private TextView mCategoryView;

        public ViewHolder(View itemView) {
            super(itemView);
            mAccountNameView = itemView.findViewById(R.id.account_textView);
            mAmountView = itemView.findViewById(R.id.amount_textView);
            mCategoryView = itemView.findViewById(R.id.category_textView);
        }
    }
}

