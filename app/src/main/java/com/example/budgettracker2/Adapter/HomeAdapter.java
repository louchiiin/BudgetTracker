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
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_TITLE = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private Activity mActivity;
    private ArrayList<TransactionList> mDataList;

    public HomeAdapter(Activity activity, ArrayList<TransactionList> list) {
        this.mActivity = activity;
        this.mDataList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_TITLE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_header_layout, parent, false);
            return new TitleViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_selection_layout, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TitleViewHolder) {
            TransactionList item = mDataList.get(position);
            ((TitleViewHolder) holder).titleTextView.setText(item.getTransactionDate());
        } else if (holder instanceof ItemViewHolder) {
            TransactionList item = mDataList.get(position - 1); // Subtract 1 to account for the title view
            ((ItemViewHolder) holder).mCategoryView.setText(item.getTransactionCategoryType());
            ((ItemViewHolder) holder).mAccountView.setText(item.getTransactionAccountType());
            ((ItemViewHolder) holder).mAmountView.setText(item.getTransactionAmount());
        }
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TITLE : VIEW_TYPE_ITEM;
    }


    public static class TitleViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public TitleViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.date_textView);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView mCategoryView;
        public TextView mAccountView;
        public TextView mAmountView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCategoryView = itemView.findViewById(R.id.category_textView);
            mAccountView = itemView.findViewById(R.id.account_textView);
            mAmountView = itemView.findViewById(R.id.amount_textView);
        }
    }
}

