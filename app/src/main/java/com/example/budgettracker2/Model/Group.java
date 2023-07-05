package com.example.budgettracker2.Model;

import java.util.List;

public class Group extends TransactionList {
    private String groupName;
    private List<TransactionList> groupValues;

    public Group(String groupName, List<TransactionList> groupValues) {
        this.groupName = groupName;
        this.groupValues = groupValues;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<TransactionList> getGroupValues() {
        return groupValues;
    }
}
