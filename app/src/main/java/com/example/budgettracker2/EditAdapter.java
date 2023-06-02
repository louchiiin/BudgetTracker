package com.example.budgettracker2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker2.Model.AccountsList;
import com.example.budgettracker2.Model.CategoryList;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.ViewHolder>{

    private final ArrayList<?> dataList;
    private final Activity mActivity;

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
        } else if (item instanceof CategoryList) {
            CategoryList categoryList = (CategoryList) item;
            holder.mItemName.setText(categoryList.getCategoryName());
        }

        holder.mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mEditView.setBackgroundResource(R.drawable.ic_close_black);
                holder.mSaveButton.setVisibility(View.VISIBLE);
            }
        });

        holder.mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LOUCHIIIN", "onClick: Save");
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataList == null) ? 0 : dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final EditText mItemName;
        private final View mEditButton;
        private final View mSaveButton;
        private final TextView mEditView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemName = itemView.findViewById(R.id.item_name);
            mEditButton = itemView.findViewById(R.id.edit_button);
            mSaveButton = itemView.findViewById(R.id.save_button);
            mEditView = itemView.findViewById(R.id.edit_icon);
        }
    }
}
