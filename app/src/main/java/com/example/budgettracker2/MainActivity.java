package com.example.budgettracker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static String FIREBASE_USER_ID = "firebase_user_id";
    public static String FIREBASE_USER_EMAIL = "firebase_user_email";
    public static String MY_TAG = "LOUCHIIIN";
    private FirebaseAuth mFirebaseAuth;
    private TextView mEmailTextView;
    private TextView mPasswordTextView;
    private Button mSignIn;
    private ConstraintLayout mLoadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize firebase auth
        mFirebaseAuth = FirebaseAuth.getInstance();

        mLoadingView = findViewById(R.id.loading_view);
        mEmailTextView = findViewById(R.id.email_address_textView);
        mPasswordTextView = findViewById(R.id.password_textView);
        mSignIn = findViewById(R.id.sign_in_button);

        mSignIn.setOnClickListener(mListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //set values of account and category on start of the app
        CategoryOptionsManager.getInstance().requestFetchAccount(null);
        CategoryOptionsManager.getInstance().requestFetchCategory(null);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            Log.d(MY_TAG, "test");
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra(FIREBASE_USER_ID, currentUser.getUid());
            intent.putExtra(FIREBASE_USER_EMAIL, currentUser.getEmail());
            startActivity(intent);
            finish();
        }
        Log.d(MY_TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(MY_TAG, "onResume");
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.sign_in_button:{
                    mLoadingView.setVisibility(View.VISIBLE);
                    signInUser();
                    break;
                }
            }
        }
    };

    private void signInUser() {
        if(mEmailTextView != null && mPasswordTextView != null) {
            String email = mEmailTextView.getText().toString().trim();
            String password = mPasswordTextView.getText().toString().trim();
            Log.d(MY_TAG, "email and pass " + email + " " + password);
            String emailRegex = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,})+$";

            // Create a Pattern object with the emailRegex
            Pattern pattern = Pattern.compile(emailRegex);

            // Create a Matcher object with the email and the pattern
            Matcher matcher = pattern.matcher(email);

            // Check if the email matches the pattern
            if (!matcher.matches()){
                // The text in the EditText is not a valid email address
                Log.d(MY_TAG, "email is not a valid email address");
                mLoadingView.setVisibility(View.GONE);
            } else if(email.isEmpty()){
                Log.d(MY_TAG, "email is blank ");
                mLoadingView.setVisibility(View.GONE);
            } else if (password.isEmpty()) {
                Log.d(MY_TAG, "password is blank ");
                mLoadingView.setVisibility(View.GONE);
            } else {
                mFirebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                mLoadingView.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(MY_TAG, "signInWithEmail:success");
                                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                    if(user != null) {
                                        Log.d(MY_TAG, "user " + user);

                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        intent.putExtra(FIREBASE_USER_ID, user.getUid());
                                        intent.putExtra(FIREBASE_USER_EMAIL, user.getEmail());
                                        startActivity(intent);
                                    }

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(MY_TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
            closeKeyboard();
        }
    }

    private void closeKeyboard() {
        View view = getWindow().getDecorView().getRootView();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}