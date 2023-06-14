package com.example.budgettracker2.Fragment;

import static com.example.budgettracker2.Activity.MainActivity.MY_TAG;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.budgettracker2.Adapter.TransactionAdapter;
import com.example.budgettracker2.CategoryOptionsManager;
import com.example.budgettracker2.ManagerCallback;
import com.example.budgettracker2.Model.TransactionList;
import com.example.budgettracker2.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class StatsFragment extends Fragment {
    private static final String EXPENSES = "Expenses";
    private static final String INCOME = "INCOME";
    private TextView mMonthTitle;
    private View mPreviousMonth;
    private View mNextMonth;
    private Calendar mCalendar;
    private PieChart mPieChart;
    private int mYear;
    private int mMonth;
    private int mFirstDayOfTheMonth;
    private int mLastDayOfTheMonth;
    private String mFormattedFirstDay;
    private String mFormattedLastDay;
    private ConstraintLayout mLoadingView;
    private RecyclerView mRecyclerView;
    private TransactionAdapter mAdapter;
    private ArrayList<TransactionList> mTransactionList;
    private View mExpensesSelection;
    private View mIncomeSelection;
    private ConstraintLayout mNoDataView;
    private int mTotal = 0;
    private int mOriginalDrawable;
    private int mClickedDrawable;
    private String mTransactionType;
    private LinearLayout mPieChartContainer;
    private LinearLayout mListContainer;
    public StatsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        mMonthTitle = view.findViewById(R.id.stats_month_title);
        mPreviousMonth = view.findViewById(R.id.stats_previous_month);
        mNextMonth = view.findViewById(R.id.stats_next_month);
        mLoadingView = view.findViewById(R.id.loading_view);
        mRecyclerView = view.findViewById(R.id.transaction_list_view);
        mExpensesSelection = view.findViewById(R.id.expenses_selection);
        mIncomeSelection = view.findViewById(R.id.income_selection);
        mNoDataView = view.findViewById(R.id.no_data_view);
        mPieChartContainer = view.findViewById(R.id.chart_container);
        mListContainer = view.findViewById(R.id.transaction_list_container);

        mOriginalDrawable = R.drawable.custom_button_black_stroke_white_fill;
        mClickedDrawable = R.drawable.custom_button_black_stroke_red_fill;

        mCalendar = Calendar.getInstance();
        mPieChart = view.findViewById(R.id.pie_chart);
        mPieChart.setNoDataText("Initializing...");
        mPieChart.setNoDataTextColor(getResources().getColor(R.color.black));

        mTransactionType = EXPENSES; //on load load expenses
        checkSelectedButton();
        fetchList();
        updateMonthAndYear();
        mNextMonth.setOnClickListener(mOnClickListener);
        mPreviousMonth.setOnClickListener(mOnClickListener);
        mIncomeSelection.setOnClickListener(mOnClickListener);
        mExpensesSelection.setOnClickListener(mOnClickListener);

        return view;
    }

    private void initializeRecyclerView() {
        mAdapter = new TransactionAdapter(getActivity(), mTransactionList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void fetchList() {
        mLoadingView.setVisibility(View.VISIBLE);
        CategoryOptionsManager.getInstance().requestFetchTransaction(mTransactionType, mFormattedFirstDay, mFormattedLastDay, new ManagerCallback() {
            @Override
            public void onFinish() {
                mLoadingView.setVisibility(View.GONE);
                mTransactionList = new ArrayList<TransactionList>();
                mTransactionList = CategoryOptionsManager.getInstance().getTransactionList();
                // Create a new list to store the combined transactions
                ArrayList<TransactionList> combinedTransactions = new ArrayList<>();

                // Iterate over the transactions
                for (TransactionList transaction : mTransactionList) {
                    boolean found = false;

                    // Iterate over the combined transactions to check if a transaction with the same name exists
                    for (TransactionList combinedTransaction : combinedTransactions) {
                        if (transaction.getTransactionCategoryType().equals(combinedTransaction.getTransactionCategoryType())) {
                            // If a transaction with the same name is found, combine them
                            String result = String.valueOf(Integer.parseInt(combinedTransaction.getTransactionAmount()) + Integer.parseInt(transaction.getTransactionAmount()));
                            combinedTransaction.setTransactionAmount(result);
                            found = true;
                            break;
                        }
                    }

                    // If no transaction with the same name was found, add the transaction to the combined list
                    if (!found) {
                        combinedTransactions.add(transaction);
                    }
                }
                // Update mTransactionList with the combined transactions
                mTransactionList = combinedTransactions;

                if(mTransactionList.size() == 0) {
                    mNoDataView.setVisibility(View.VISIBLE);
                    mPieChartContainer.setVisibility(View.GONE);
                    mListContainer.setVisibility(View.GONE);
                } else {
                    mNoDataView.setVisibility(View.GONE);
                    mPieChartContainer.setVisibility(View.VISIBLE);
                    mListContainer.setVisibility(View.VISIBLE);
                }
                Log.d("LOUCHIIIN", "mTOOOTAL " + mTotal);
                calculateTotal();
                initializeRecyclerView();
                initializeGraph();
            }

            @Override
            public void onError(String error) {
                mLoadingView.setVisibility(View.GONE);
            }
        });
    }

    private void calculateTotal() {
        mTotal = 0;
        for (TransactionList transaction : mTransactionList) {
            int amount = Integer.parseInt(transaction.getTransactionAmount());
            mTotal += amount;
        }
        Log.d("LOUCHIIIN", "mTotal " + mTotal);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.stats_previous_month:
                    Log.d(MY_TAG, "onClick: previous month");
                    mCalendar.add(Calendar.MONTH, -1);
                    updateMonthAndYear();
                    fetchList();
                    break;
                case R.id.stats_next_month:
                    Log.d(MY_TAG, "onClick: next month");
                    mCalendar.add(Calendar.MONTH, 1);
                    updateMonthAndYear();
                    fetchList();
                    break;
                case R.id.expenses_selection:
                    mTransactionType = EXPENSES;
                    checkSelectedButton();
                    fetchList();
                    break;
                case R.id.income_selection:
                    mTransactionType = INCOME;
                    checkSelectedButton();
                    fetchList();
                    break;

            }
        }
    };

    private void checkSelectedButton() {
        mIncomeSelection.setBackgroundResource(mTransactionType.equals(INCOME) ? mClickedDrawable : mOriginalDrawable);
        mExpensesSelection.setBackgroundResource(mTransactionType.equals(EXPENSES) ? mClickedDrawable : mOriginalDrawable);
    }

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

    private void initializeGraph() {
        List<PieEntry> entries = new ArrayList<>();
        for (TransactionList transaction : mTransactionList) {
            int percentage = Integer.parseInt(transaction.getTransactionAmount());
            double percentageFloat = ((double) percentage / mTotal) * 100;
            String label = transaction.getTransactionCategoryType();
            entries.add(new PieEntry((float) percentageFloat, label));
        }


        PieDataSet dataSet = new PieDataSet(entries, "TransactionList");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextSize(16f); // Set the text size to 14
        dataSet.setValueTextColor(getResources().getColor(R.color.white)); // Set the text color (optional)

        PieData data = new PieData(dataSet);
        data.setDrawValues(true); // Enable value visibility
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.2f%%", value); // Customize value formatting
            }

        });
        mPieChart.setData(data);
        mPieChart.animate();
        mPieChart.animateY(300);
        mPieChart.setDrawEntryLabels(true);
        mPieChart.setHoleRadius(0);
        mPieChart.setTransparentCircleRadius(0);


        Description description = new Description();
        description.setEnabled(false); // Disable description label
        /*description.setText("Pie Chart");
        description.setTextSize(18f);
        description.setTextColor(getResources().getColor(R.color.white));*/
        mPieChart.setDescription(description);

        Legend legend = mPieChart.getLegend();
        legend.setEnabled(false); // Disable legends
        mPieChart.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchList();
    }

    //create a recyclerview adapter class
}