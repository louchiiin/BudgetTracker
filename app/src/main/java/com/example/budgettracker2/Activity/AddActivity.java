package com.example.budgettracker2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.budgettracker2.Constants;
import com.example.budgettracker2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddActivity extends AppCompatActivity {

    private EditText mEditView;
    private TextView mHeaderTitle;
    private ImageView mCloseBtn;
    private TextView mSaveBtn;
    private String mCategoryType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        if(getIntent() != null) {
            mCategoryType = getIntent().getStringExtra("category_type");
        }

        ImageView addButton = findViewById(R.id.actionBar_add);
        mEditView = findViewById(R.id.add_item_textView);
        addButton.setVisibility(View.GONE);
        //initialize mHeaderTitle and mCloseButton
        mHeaderTitle = findViewById(R.id.actionBar_title);
        mCloseBtn = findViewById(R.id.actionBar_open_sideMenu);
        mSaveBtn = findViewById(R.id.save_button);

        mHeaderTitle.setText(getString(R.string.common_add));
        mCloseBtn.setImageResource(R.drawable.ic_back_black);

        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSaveBtn.setEnabled(false);
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(mCategoryType.equals(Constants.CATEGORY_TYPE) ? "Category" : "AccountsCategory");
                String val = mEditView.getText().toString();
                HashMap<String, Object> userData = new HashMap<>();
                String newUserId = dbRef.push().getKey();
                if(mCategoryType.equals(Constants.CATEGORY_TYPE)) {
                    userData.put("categoryName", val);
                } else {
                    userData.put("accountsName", val);
                }

                dbRef.child(newUserId != null ? newUserId : "").setValue(userData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //create set result
                            mEditView.setText("");
                            mSaveBtn.setEnabled(true);
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("category_type", mCategoryType);
                            setResult(RESULT_OK, resultIntent);
                            onBackPressed();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out, 0);
    }
}