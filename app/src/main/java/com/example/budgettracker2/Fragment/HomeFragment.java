package com.example.budgettracker2.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.budgettracker2.Adapter.HomeAdapter;
import com.example.budgettracker2.CategoryOptionsManager;
import com.example.budgettracker2.Constants;
import com.example.budgettracker2.Interfaces.ManagerCallback;
import com.example.budgettracker2.Model.TransactionList;
import com.example.budgettracker2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private final String DAILY_SELECTION = "Daily";
    private final String WEEKLY_SELECTION = "Weekly";
    private final String MONTHLY_SELECTION = "Monthly";
    private TextView mMonthTitle;
    private View mPreviousMonth;
    private View mNextMonth;
    private Calendar mCalendar;

    private TextView mDailyView;
    private TextView mWeeklyView;
    private TextView mMonthlyView;
    private int mYear;
    private int mMonth;
    private int mFirstDayOfTheMonth;
    private int mLastDayOfTheMonth;
    private String mFormattedFirstDay;
    private String mFormattedLastDay;
    private int mOriginalDrawable;
    private int mClickedDrawable;
    private String mSelectedType;
    private ArrayList<TransactionList> mTransactionList;
    private RecyclerView mRecyclerView;
    private HomeAdapter mAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mMonthTitle = view.findViewById(R.id.month_title);
        mPreviousMonth = view.findViewById(R.id.previous_month);
        mNextMonth = view.findViewById(R.id.next_month);

        mDailyView = view.findViewById(R.id.daily_selection);
        mWeeklyView = view.findViewById(R.id.weekly_selection);
        mMonthlyView = view.findViewById(R.id.monthly_selection);
        mRecyclerView = view.findViewById(R.id.recyclerView);

        mOriginalDrawable = R.drawable.custom_button_black_stroke_white_fill;
        mClickedDrawable = R.drawable.custom_button_black_stroke_red_fill;

        mSelectedType = DAILY_SELECTION;
        checkSelectedButton();
        mCalendar = Calendar.getInstance();
        updateMonthAndYear();

        mNextMonth.setOnClickListener(mOnClickListener);
        mPreviousMonth.setOnClickListener(mOnClickListener);
        mDailyView.setOnClickListener(mOnClickListener);
        mWeeklyView.setOnClickListener(mOnClickListener);
        mMonthlyView.setOnClickListener(mOnClickListener);
        fetchList();
        return view;
    }

    private void fetchList() {
        CategoryOptionsManager.getInstance().requestFetchTransaction(Constants.DATE_ONLY_SORT, StatsFragment.EXPENSES, mFormattedFirstDay, mFormattedLastDay, new ManagerCallback() {
            @Override
            public void onFinish() {
                mTransactionList = new ArrayList<TransactionList>();
                mTransactionList = CategoryOptionsManager.getInstance().getTransactionList();

                initializeAdapter();
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void initializeAdapter() {
        List<String> items = new ArrayList<>();
        // Add your items to the list
        items.add("Item 1");
        items.add("Item 2");
        items.add("Item 3");
        mAdapter = new HomeAdapter(getActivity(), mTransactionList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void checkSelectedButton() {
        int dailyBackgroundResource = mSelectedType.equals(DAILY_SELECTION) ? mClickedDrawable : mOriginalDrawable;
        int weeklyBackgroundResource = mSelectedType.equals(WEEKLY_SELECTION) ? mClickedDrawable : mOriginalDrawable;
        int monthlyBackgroundResource = mSelectedType.equals(MONTHLY_SELECTION) ? mClickedDrawable : mOriginalDrawable;

        int dailyTextColor = mSelectedType.equals(DAILY_SELECTION) ? R.color.white : R.color.black;
        int weeklyTextColor = mSelectedType.equals(WEEKLY_SELECTION) ? R.color.white : R.color.black;
        int monthlyTextColor = mSelectedType.equals(MONTHLY_SELECTION) ? R.color.white : R.color.black;

        mDailyView.setBackgroundResource(dailyBackgroundResource);
        mDailyView.setTextColor(getResources().getColor(dailyTextColor));
        mWeeklyView.setBackgroundResource(weeklyBackgroundResource);
        mWeeklyView.setTextColor(getResources().getColor(weeklyTextColor));
        mMonthlyView.setBackgroundResource(monthlyBackgroundResource);
        mMonthlyView.setTextColor(getResources().getColor(monthlyTextColor));
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.previous_month:
                    mCalendar.add(Calendar.MONTH, -1);
                    updateMonthAndYear();
                    break;
                case R.id.next_month:
                    mCalendar.add(Calendar.MONTH, 1);
                    updateMonthAndYear();
                    break;
                case R.id.daily_selection:
                    mSelectedType = DAILY_SELECTION;
                    checkSelectedButton();
                    break;
                case R.id.weekly_selection:
                    mSelectedType = WEEKLY_SELECTION;
                    checkSelectedButton();
                    break;
                case R.id.monthly_selection:
                    mSelectedType = MONTHLY_SELECTION;
                    checkSelectedButton();
                    break;
            }
        }
    };

    private void updateMonthAndYear() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String title = formatter.format(mCalendar.getTime());
        mMonthTitle.setText(title);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;

        // Set the calendar to the first day of the month
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mFirstDayOfTheMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        // Set the calendar to the last day of the month
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mLastDayOfTheMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        String formattedMonth = String.format(Locale.getDefault(),"%02d", mMonth);
        mFormattedFirstDay = formattedMonth + "/" + mFirstDayOfTheMonth + "/" + mYear;
        mFormattedLastDay = formattedMonth + "/" + mLastDayOfTheMonth + "/" + mYear;

    }
}