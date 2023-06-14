package com.example.budgettracker2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class CustomMaxWidthFrameLayout extends RelativeLayout {

    public CustomMaxWidthFrameLayout(Context context) {
        super(context);
    }

    public CustomMaxWidthFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomMaxWidthFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {

        if (child.getId() == -1) {
            child.setId(View.generateViewId());
        }

        super.addView(child);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int l = this.getChildCount();
        if (l > 0) {
            int max = MeasureSpec.getSize(widthMeasureSpec);
            View pLine = this.getChildAt(0);
            View prev = this.getChildAt(0);
            prev.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int currentTotal = pLine.getMeasuredWidth();
            for (int i = 1; i < l; i++) {
                View child = this.getChildAt(i);
                child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                int width = child.getMeasuredWidth();

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) child.getLayoutParams();

                int next = RelativeLayout.END_OF;
                int start = RelativeLayout.ALIGN_START;
                if(getLayoutDirection() == LAYOUT_DIRECTION_LTR){
                    next = RelativeLayout.RIGHT_OF;
                    start = RelativeLayout.ALIGN_LEFT;
                } else if(getLayoutDirection() == LAYOUT_DIRECTION_RTL){
                    next = RelativeLayout.LEFT_OF;
                    start = RelativeLayout.ALIGN_RIGHT;
                }

                if (max > currentTotal + width) {
                    currentTotal += width;
                    layoutParams.addRule(RelativeLayout.ALIGN_TOP, prev.getId());
                    layoutParams.addRule(next, prev.getId());
                } else {
                    layoutParams.addRule(RelativeLayout.BELOW, pLine.getId());
                    layoutParams.addRule(start, pLine.getId());
                    pLine = child;
                    currentTotal = pLine.getMeasuredWidth();
                }
                child.setLayoutParams(layoutParams);
                prev = child;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
