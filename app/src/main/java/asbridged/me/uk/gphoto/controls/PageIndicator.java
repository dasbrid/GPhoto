package asbridged.me.uk.gphoto.controls;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.LinearLayout;

import  asbridged.me.uk.gphoto.R;

/**
 * Created by AsbridgeD on 12/01/2016.
 */
public class PageIndicator extends LinearLayout {

    private static final String TAG = "PageIndicator";

    private int numButtons;
    private int ballResId;
    private View[] balls;
    private int currentPage = -1;

    private int selectedSize;
    private int unselectedSize;
    private int margin;

    private static final int DEFAULT_SELECTED_SIZE = 40;
    private static final int DEFAULT_UNSELECTED_SIZE = 20;
    private static final int DEFAULT_MARGIN = 8;

    public PageIndicator(Context context) {
        super(context);
        unselectedSize = DEFAULT_UNSELECTED_SIZE;
        selectedSize = DEFAULT_SELECTED_SIZE;
        margin = DEFAULT_MARGIN;
        initializeViews(context);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PageIndicator,
                0, 0);

        try {
            unselectedSize = a.getInt(R.styleable.PageIndicator_unselectedsize, DEFAULT_UNSELECTED_SIZE);
            selectedSize = a.getInteger(R.styleable.PageIndicator_selectedsize, DEFAULT_SELECTED_SIZE);
            margin = a.getInteger(R.styleable.PageIndicator_selectedsize, DEFAULT_MARGIN);
        } finally {
            a.recycle();
        }

        initializeViews(context);
    }

    public PageIndicator(Context context,
                         AttributeSet attrs,
                         int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_control_page_indicator, this);

    }

    public void setNumButtons(int n) {
        if (n == numButtons)
            return;
        numButtons = n;
        balls = new View[numButtons];
        View ball;
        removeAllViews();
        for (int i = 0; i < numButtons; i++) {
            ball = createBall();
            balls[i]=ball;
            addView(ball);
        }
        changePage(0);
    }


    private void setPage(int page, int size) {

        if (page <0 || page >= numButtons)
            return;
        Log.d(TAG, "setPage "+ page);
        View ball = balls[page];
        ViewGroup.LayoutParams params = ball.getLayoutParams();
        params.width = size;
        params.height = size;
        ball.setLayoutParams(params);


    }

    private void selectPage(int page) {
        setPage(page, selectedSize);
    }

    private void unselectPage(int page) {
        setPage(page, unselectedSize);
    }

    public void changePage(int page) {
        selectPage(page);
        unselectPage(currentPage);
        currentPage = page;
    }

    private View createBall() {
        View v;
        if (ballResId > 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(ballResId, this, true);
        } else {
            Button btn = new Button(getContext());
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(unselectedSize, unselectedSize);
            ll.gravity = Gravity.CENTER_VERTICAL;
            ll.leftMargin = margin;
            ll.rightMargin = margin;
            btn.setLayoutParams(ll);
            btn.setBackground(getResources().getDrawable(R.drawable.rounded_cell));

            v = btn;
        }
        return v;
    }
}