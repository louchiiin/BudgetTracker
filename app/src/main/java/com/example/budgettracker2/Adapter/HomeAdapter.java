package com.example.budgettracker2.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker2.Model.Group;
import com.example.budgettracker2.Model.TransactionList;
import com.example.budgettracker2.R;
import com.google.gson.Gson;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_GROUPED_VALUE = 1;

    private List<Group> groups;  // List of groups including header and grouped values

    public HomeAdapter(List<Group> groups) {
        Log.d("LOUCHIIIN", new Gson().toJson(groups));
        this.groups = groups;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate the appropriate layout based on the view type
        if (viewType == TYPE_HEADER) {
            View headerView = inflater.inflate(R.layout.group_header_layout, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View groupedValueView = inflater.inflate(R.layout.home_selection_layout, parent, false);
            return new GroupedValueViewHolder(groupedValueView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            String currentHeader = groups.get(getGroupPosition(position)).getGroupName();
            String previousHeader = getPreviousHeader(position);

            if (position == 0 || !currentHeader.equals(previousHeader)) {
                headerViewHolder.bind(currentHeader);
                headerViewHolder.showHeader(); // Implement a showHeader() method in your HeaderViewHolder to show the header view
            } else {
                headerViewHolder.hideHeader();
            }
        } else if (holder instanceof GroupedValueViewHolder) {
            GroupedValueViewHolder groupedValueViewHolder = (GroupedValueViewHolder) holder;
            int[] positions = getValuePosition(position);
            int groupPosition = positions[0];
            int valuePosition = positions[1];

            if (groupPosition >= 0 && valuePosition >= 0) {
                TransactionList transactionList = groups.get(groupPosition).getGroupValues().get(valuePosition);
                groupedValueViewHolder.bind(transactionList.getTransactionCategoryType(), transactionList.getTransactionAccountType(), transactionList.getTransactionAmount());
            }
        }
    }


    private String getPreviousHeader(int position) {
        int previousPosition = position - 1;

        while (previousPosition >= 0) {
            if (isHeaderPosition(previousPosition)) {
                return groups.get(getGroupPosition(previousPosition)).getGroupName();
            }
            previousPosition--;
        }

        return null;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (Group group : groups) {
            count += group.getGroupValues().size() + 1; // Add 1 for the header
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_GROUPED_VALUE;
        }
    }

    private boolean isHeaderPosition(int position) {
        int count = 0;
        for (Group group : groups) {
            if (position == count) {
                return true;
            }
            count += group.getGroupValues().size() + 1; // Add 1 for the header
        }
        return false;
    }

    private int getGroupPosition(int position) {
        int count = 0;
        int groupIndex = 0;
        while (groupIndex < groups.size()) {
            int groupSize = groups.get(groupIndex).getGroupValues().size() + 1; // Add 1 for the header
            if (position >= count && position < count + groupSize) {
                return groupIndex;
            }
            count += groupSize;
            groupIndex++;
        }
        return -1;
    }

    private int[] getValuePosition(int position) {
        int count = 0;
        int groupIndex = 0;
        while (groupIndex < groups.size()) {
            int groupSize = groups.get(groupIndex).getGroupValues().size() + 1; // Add 1 for the header
            if (position >= count && position < count + groupSize) {
                int valuePosition = position - count - 1; // Subtract 1 for the header
                return new int[]{groupIndex, valuePosition};
            }
            count += groupSize;
            groupIndex++;
        }
        return new int[]{-1, -1}; // Return -1 for invalid positions
    }

    // ViewHolder for the header item
    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView headerTextView;
        private ConstraintLayout headerContainer;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.date_textView);
            headerContainer = itemView.findViewById(R.id.header_container);
        }

        public void bind(String groupName) {
            headerTextView.setText(groupName);
        }
        public void showHeader() {
            headerContainer.setVisibility(View.VISIBLE);
        }

        public void hideHeader() {
            headerContainer.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    // ViewHolder for the grouped value item
    private static class GroupedValueViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryView;
        private TextView accountView;
        private TextView amountView;

        public GroupedValueViewHolder(View itemView) {
            super(itemView);
            categoryView = itemView.findViewById(R.id.category_textView);
            accountView = itemView.findViewById(R.id.account_textView);
            amountView = itemView.findViewById(R.id.amount_textView);
        }

        public void bind(String category, String account, String amount) {
            categoryView.setText(category);
            accountView.setText(account);
            amountView.setText(amount);
        }
    }
}

