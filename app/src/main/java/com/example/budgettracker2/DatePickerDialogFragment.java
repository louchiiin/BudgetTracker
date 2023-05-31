package com.example.budgettracker2;

import static com.example.budgettracker2.MainActivity.MY_TAG;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerDialogFragment extends DialogFragment {

    public interface OnSelectedDate {
        void onSelect(String date);
    }

    private OnSelectedDate mCallback;

    public void setOnSelectedDateCallback(OnSelectedDate callback){
        this.mCallback = callback;
    }
    private Dialog mDialog;
    private DatePicker mDatePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_AlertDialog);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_date_picker_dialog, null);

        mDatePicker = view.findViewById(R.id.date_picker);

        builder.setView(view)
                .setTitle("Select Date")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // handle OK button click
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);

                        Date date = calendar.getTime();
                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                        String formattedDate = formatter.format(date);
                        Log.d(MY_TAG, "formattedDate " + formattedDate);
                        if(mCallback != null) {
                            mCallback.onSelect(formattedDate);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // handle Cancel button click
                    }
                });
        mDialog = builder.create();
        return mDialog;
    }
}