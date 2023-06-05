package com.example.budgettracker2;

import static com.example.budgettracker2.MainActivity.MY_TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker2.Model.AccountsList;
import com.example.budgettracker2.Model.CategoryList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.ViewHolder>{

    private final ArrayList<?> dataList;
    private final Activity mActivity;
    private int mEditedPosition = RecyclerView.NO_POSITION;

    public EditAdapter(Activity activity, ArrayList<?> list) {
        this.mActivity = activity;
        this.dataList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.edit_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object item = dataList.get(position);

        if (item instanceof AccountsList) {
            AccountsList accountsList = (AccountsList) item;
            holder.mItemName.setText(accountsList.getAccountName());
            holder.mEditButton.setTag(accountsList.getId());
        } else if (item instanceof CategoryList) {
            CategoryList categoryList = (CategoryList) item;
            holder.mItemName.setText(categoryList.getCategoryName());
            holder.mEditButton.setTag(categoryList.getId());
        }

        if (position == mEditedPosition) {
            isUpdating(true, holder);
        } else {
            isUpdating(false, holder);
        }

        holder.mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int previousEditedPosition = mEditedPosition;
                mEditedPosition = holder.getAdapterPosition();

                // Notify the adapter to update the previous edited position and the current edited position
                if (previousEditedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousEditedPosition);
                }
                notifyItemChanged(mEditedPosition);

                isUpdating(true, holder);

                holder.mItemName.requestFocus();
                // Open the keyboard
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(holder.mItemName, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        holder.mItemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    holder.mItemName.setSelection(holder.mItemName.getText().length());
                }
            }
        });
        holder.mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                holder.mItemName.clearFocus();
                String itemId = (String) holder.mEditButton.getTag();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Category").child(itemId);
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            ds.getRef().setValue(holder.mItemName.getText().toString());
                        }
                        holder.mItemName.clearFocus();
                        isUpdating(false, holder);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if(item instanceof AccountsList) {
                    CategoryOptionsManager.getInstance().requestFetchAccount(null);
                } else if (item instanceof CategoryList) {
                    CategoryOptionsManager.getInstance().requestFetchCategory(null);
                }
            }
        });
        holder.mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(MY_TAG, "cancel position: " + holder.getAdapterPosition());
                isUpdating(false, holder);
            }
        });
    }

    private void isUpdating(boolean status, ViewHolder holder) {
        if(status) {
            holder.mEditButton.setVisibility(View.GONE);
            holder.mCancelButton.setVisibility(View.VISIBLE);
            holder.mSaveButton.setVisibility(View.VISIBLE);
            holder.mItemName.setEnabled(true);
        } else {
            holder.mEditButton.setVisibility(View.VISIBLE);
            holder.mCancelButton.setVisibility(View.GONE);
            holder.mSaveButton.setVisibility(View.GONE);
            holder.mItemName.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return (dataList == null) ? 0 : dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final EditText mItemName;
        private final View mEditButton;
        private final View mSaveButton;
        private final View mCancelButton;
        private final TextView mEditView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemName = itemView.findViewById(R.id.item_name);
            mEditButton = itemView.findViewById(R.id.edit_button);
            mSaveButton = itemView.findViewById(R.id.save_button);
            mCancelButton = itemView.findViewById(R.id.cancel_button);
            mEditView = itemView.findViewById(R.id.edit_icon);
        }
    }
}
