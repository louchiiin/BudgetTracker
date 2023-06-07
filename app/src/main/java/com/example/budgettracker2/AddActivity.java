package com.example.budgettracker2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AddActivity extends AppCompatActivity {

    private EditText mEditView;
    private TextView mHeaderTitle;
    private ImageView mCloseButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ImageView addButton = findViewById(R.id.actionBar_add);
        mEditView = findViewById(R.id.add_item_textView);
        addButton.setVisibility(View.GONE);
        //initialize mHeaderTitle and mCloseButton
        mHeaderTitle = findViewById(R.id.actionBar_title);
        mCloseButton = findViewById(R.id.actionBar_open_sideMenu);

        mHeaderTitle.setText("Add");
        mCloseButton.setImageResource(R.drawable.ic_back_black);

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out, 0);
    }
}