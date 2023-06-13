package com.example.budgettracker2.Model;

public class TransactionList {
    private int mTransactionType;

    private String mTransactionId;
    private String mTransactionDate;
    private String mTransactionAmount;
    private String mTransactionAccountType;
    private String mTransactionCategoryType;
    private String mTransactionDescription;
    private String mTransactionNote;

    public int getTransactionType() {
        return mTransactionType;
    }

    public void setTransactionType(int transactionType) {
        mTransactionType = transactionType;
    }

    public String getTransactionId() {
        return mTransactionId;
    }

    public void setTransactionId(String mTransactionId) {
        this.mTransactionId = mTransactionId;
    }

    public String getTransactionDate() {
        return mTransactionDate;
    }

    public void setTransactionDate(String mTransactionDate) {
        this.mTransactionDate = mTransactionDate;
    }

    public String getTransactionAmount() {
        return mTransactionAmount;
    }

    public void setTransactionAmount(String mTransactionAmount) {
        this.mTransactionAmount = mTransactionAmount;
    }

    public String getTransactionAccountType() {
        return mTransactionAccountType;
    }

    public void setTransactionAccountType(String mTransactionAccountType) {
        this.mTransactionAccountType = mTransactionAccountType;
    }

    public String getTransactionCategoryType() {
        return mTransactionCategoryType;
    }

    public void setTransactionCategoryType(String mTransactionCategoryType) {
        this.mTransactionCategoryType = mTransactionCategoryType;
    }

    public String getTransactionDescription() {
        return mTransactionDescription;
    }

    public void setTransactionDescription(String mTransactionDescription) {
        this.mTransactionDescription = mTransactionDescription;
    }

    public String getTransactionNote() {
        return mTransactionNote;
    }

    public void setTransactionNote(String mTransactionNote) {
        this.mTransactionNote = mTransactionNote;
    }
}
