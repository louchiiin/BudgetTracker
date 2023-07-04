package com.example.budgettracker2.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker2.Model.Group;
import com.example.budgettracker2.Model.GroupedValueItem;
import com.example.budgettracker2.Model.HeaderItem;
import com.example.budgettracker2.Model.TransactionList;
import com.example.budgettracker2.R;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_GROUPED_VALUE = 1;

    private List<Group> groups;  // List of groups including header and grouped values

    public HomeAdapter(List<Group> groups) {
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
            headerViewHolder.bind(groups.get(getGroupPosition(position)).getGroupName());
        } else if (holder instanceof GroupedValueViewHolder) {
            GroupedValueViewHolder groupedValueViewHolder = (GroupedValueViewHolder) holder;
            int[] positions = getValuePosition(position);
            int groupPosition = positions[0];
            int valuePosition = positions[1];

            // Check if the positions are valid before accessing the list
            if (groupPosition >= 0 && valuePosition >= 0) {
                groupedValueViewHolder.bind(groups.get(groupPosition).getGroupValues().get(valuePosition));
            }
        }
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
        for (int i = 0; i < groups.size(); i++) {
            count++; // Increment count for the header
            if (position < count) {
                return i;
            }
            count += groups.get(i).getGroupValues().size(); // Add the size of the group values
        }
        return -1;
    }

    private int[] getValuePosition(int position) {
        int count = 0;
        for (int i = 0; i < groups.size(); i++) {
            if (position == count) {
                return new int[]{i, -1}; // Return -1 as value position for the header
            }
            count++; // Increment count for the header

            int groupSize = groups.get(i).getGroupValues().size();
            if (position < count + groupSize) {
                return new int[]{i, position - count}; // Subtract count to get the value position
            }
            count += groupSize; // Add the group size
        }
        return new int[]{-1, -1}; // Return -1 for invalid positions
    }

    // ViewHolder for the header item
    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView headerTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.date_textView);
        }

        public void bind(String groupName) {
            headerTextView.setText(groupName);
        }
    }

    // ViewHolder for the grouped value item
    private static class GroupedValueViewHolder extends RecyclerView.ViewHolder {
        private TextView valueTextView;

        public GroupedValueViewHolder(View itemView) {
            super(itemView);
            valueTextView = itemView.findViewById(R.id.account_textView);
        }

        public void bind(String value) {
            valueTextView.setText(value);
        }
    }
}

