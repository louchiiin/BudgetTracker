package com.example.budgettracker2.Model;

import java.util.List;

public class Group {
    private String groupName;
    private List<String> groupValues;

    public Group(String groupName, List<String> groupValues) {
        this.groupName = groupName;
        this.groupValues = groupValues;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<String> getGroupValues() {
        return groupValues;
    }
}
