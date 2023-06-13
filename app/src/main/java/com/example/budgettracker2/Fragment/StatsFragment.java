package com.example.budgettracker2.Fragment;

import static com.example.budgettracker2.Activity.MainActivity.MY_TAG;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private TextView mMonthTitle;
    private View mPreviousMonth;
    private View mNextMonth;
    private Calendar mCalendar;
    private int mYear;
    private int mMonth;
    private ConstraintLayout mLoadingView;
    private RecyclerView mRecyclerView;
    private TransactionAdapter mAdapter;
    private ArrayList<TransactionList> mTransactionList;
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

        mCalendar = Calendar.getInstance();
        initializeGraph(view);
        updateMonthAndYear();
        fetchList();
        mNextMonth.setOnClickListener(mOnClickListener);
        mPreviousMonth.setOnClickListener(mOnClickListener);

        return view;
    }

    private void initializeRecyclerView() {
        mAdapter = new TransactionAdapter(getActivity(), mTransactionList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void fetchList() {
        mLoadingView.setVisibility(View.VISIBLE);
        CategoryOptionsManager.getInstance().requestFetchTransaction(new ManagerCallback() {
            @Override
            public void onFinish() {
                mLoadingView.setVisibility(View.GONE);
                mTransactionList = new ArrayList<TransactionList>();
                mTransactionList = CategoryOptionsManager.getInstance().getTransactionList();
                initializeRecyclerView();
            }

            @Override
            public void onError(String error) {
                mLoadingView.setVisibility(View.GONE);
            }
        });
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.stats_previous_month:
                    Log.d(MY_TAG, "onClick: previous month");
                    mCalendar.add(Calendar.MONTH, -1);
                    updateMonthAndYear();
                    break;
                case R.id.stats_next_month:
                    Log.d(MY_TAG, "onClick: next month");
                    mCalendar.add(Calendar.MONTH, 1);
                    updateMonthAndYear();
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

        //log year and month
        Log.d(MY_TAG, "updateMonthAndYear: " + mYear + " " + mMonth);
    }

    private void initializeGraph(View view) {
        PieChart pieChart = view.findViewById(R.id.pieChart);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(22f, "Label 1"));
        entries.add(new PieEntry(35f, "Label 2"));
        entries.add(new PieEntry(40f, "Label 3"));

        PieDataSet dataSet = new PieDataSet(entries, "HELLO");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(16f); // Set the text size to 14
        dataSet.setValueTextColor(getResources().getColor(R.color.white)); // Set the text color (optional)

        PieData data = new PieData(dataSet);
        data.setDrawValues(true); // Enable value visibility
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.1f%%", value); // Customize value formatting
            }

        });
        pieChart.setData(data);

        pieChart.setHoleRadius(0);
        pieChart.setTransparentCircleRadius(0);

        Description description = new Description();
        //description.setEnabled(false); // Disable description label
        description.setText("Pie Chart");
        description.setTextSize(18f);
        description.setTextColor(getResources().getColor(R.color.white));
        pieChart.setDescription(description);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false); // Disable legends
        pieChart.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchList();
    }

    //create a recyclerview adapter class
}