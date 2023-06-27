package com.example.budgettracker2.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class TransactionList implements Parcelable {
    private int mTransactionType;

    private String mTransactionId;
    private String mTransactionDate;
    private String mTransactionAmount;
    private String mTransactionAccountType;
    private String mTransactionCategoryType;
    private String mTransactionDescription;
    private String mTransactionNote;
    private String mTransactionCombinedAmount;
    private ArrayList<String> mCombinedTransactionIds = new ArrayList<>();

    public TransactionList() {

    }

    protected TransactionList(Parcel in) {
        mTransactionType = in.readInt();
        mTransactionId = in.readString();
        mTransactionDate = in.readString();
        mTransactionAmount = in.readString();
        mTransactionAccountType = in.readString();
        mTransactionCategoryType = in.readString();
        mTransactionDescription = in.readString();
        mTransactionNote = in.readString();
        mCombinedTransactionIds = in.createStringArrayList();
    }

    public static final Creator<TransactionList> CREATOR = new Creator<TransactionList>() {
        @Override
        public TransactionList createFromParcel(Parcel in) {
            return new TransactionList(in);
        }

        @Override
        public TransactionList[] newArray(int size) {
            return new TransactionList[size];
        }
    };

    public ArrayList<String> getCombinedIds() {
        return mCombinedTransactionIds;
    }

    public void setIds(ArrayList<String> ids) {
        this.mCombinedTransactionIds = ids;
    }

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

    public String getTransactionCombinedAmount() {
        return mTransactionCombinedAmount;
    }

    public void setTransactionCombinedAmount(String transactionAmount) {
        this.mTransactionCombinedAmount = transactionAmount;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
            parcel.writeInt(mTransactionType);
            parcel.writeString(mTransactionId);
            parcel.writeString(mTransactionDate);
            parcel.writeString(mTransactionAmount);
            parcel.writeString(mTransactionAccountType);
            parcel.writeString(mTransactionCategoryType);
            parcel.writeString(mTransactionDescription);
            parcel.writeString(mTransactionNote);
            parcel.writeStringList(mCombinedTransactionIds);
    }
}
