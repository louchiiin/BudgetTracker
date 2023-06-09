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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker2.Model.AccountsList;
import com.example.budgettracker2.Model.CategoryList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.ViewHolder>{

    private final ArrayList<?> mDataList;
    private final Activity mActivity;
    private int mEditedPosition = RecyclerView.NO_POSITION;
    public interface OnUpdateListener {
        void onLoadingShow();
        void onUpdate();
    }
    private OnUpdateListener mOnUpdateListener;

    public EditAdapter(Activity activity, ArrayList<?> list, OnUpdateListener listener) {
        this.mActivity = activity;
        this.mDataList = list;
        this.mOnUpdateListener = listener;
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
        Object item = mDataList.get(position);
        boolean isAccountsList = false;

        if (item instanceof AccountsList) {
            AccountsList accountsList = (AccountsList) item;
            holder.mItemName.setText(accountsList.getAccountName());
            holder.mEditBtn.setTag(accountsList.getId());
            isAccountsList = true;
        } else if (item instanceof CategoryList) {
            CategoryList categoryList = (CategoryList) item;
            holder.mItemName.setText(categoryList.getCategoryName());
            holder.mEditBtn.setTag(categoryList.getId());
            isAccountsList = false;
        }

        if (position == mEditedPosition) {
            isUpdating(true, holder);
        } else {
            isUpdating(false, holder);
        }

        holder.mEditBtn.setOnClickListener(new View.OnClickListener() {
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
                closeKeyboard(false, null, holder);
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

        boolean finalIsAccountsList = isAccountsList;
        holder.mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnUpdateListener != null) {
                    mOnUpdateListener.onLoadingShow();
                }
                closeKeyboard(true, view, null);

                holder.mItemName.clearFocus();
                String itemId = (String) holder.mEditBtn.getTag();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(finalIsAccountsList ? "AccountsCategory" : "Category").child(itemId);
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            ds.getRef().setValue(holder.mItemName.getText().toString());
                        }

                        if (mOnUpdateListener != null) {
                            mOnUpdateListener.onUpdate();
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
        holder.mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(MY_TAG, "cancel position: " + holder.getAdapterPosition());
                isUpdating(false, holder);
            }
        });
        boolean finalIsAccountsList1 = isAccountsList;
        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Delete button", "onClick: ");
                if (mOnUpdateListener != null) {
                    mOnUpdateListener.onLoadingShow();
                }

                String itemId = (String) holder.mEditBtn.getTag();
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(finalIsAccountsList1 ? "AccountsCategory" : "Category").child(itemId);

                // Delete the value
                databaseRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("LOUCHIIIN", "onComplete: ");
                            if (mOnUpdateListener != null) {
                                mOnUpdateListener.onUpdate();
                            }
                        } else {
                            Log.d("LOUCHIIIN", "unsuccessful: ");
                        }
                        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                });

            }
        });
    }

    private void closeKeyboard(boolean isClose, View view, ViewHolder holder) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(isClose) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } else {
            imm.showSoftInput(holder.mItemName, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void isUpdating(boolean status, ViewHolder holder) {
        if(status) {
            holder.mEditBtn.setVisibility(View.GONE);
            holder.mCancelBtn.setVisibility(View.VISIBLE);
            holder.mSaveBtn.setVisibility(View.VISIBLE);
            holder.mDeleteBtn.setVisibility(View.VISIBLE);
            holder.mItemName.setEnabled(true);
        } else {
            holder.mEditBtn.setVisibility(View.VISIBLE);
            holder.mCancelBtn.setVisibility(View.GONE);
            holder.mSaveBtn.setVisibility(View.GONE);
            holder.mDeleteBtn.setVisibility(View.GONE);
            holder.mItemName.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return (mDataList == null) ? 0 : mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final EditText mItemName;
        private final View mEditBtn;
        private final View mSaveBtn;
        private final View mCancelBtn;
        private final View mDeleteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemName = itemView.findViewById(R.id.item_name);
            mEditBtn = itemView.findViewById(R.id.edit_button);
            mSaveBtn = itemView.findViewById(R.id.save_button);
            mCancelBtn = itemView.findViewById(R.id.cancel_button);
            mDeleteBtn = itemView.findViewById(R.id.delete_button);
        }
    }
}
