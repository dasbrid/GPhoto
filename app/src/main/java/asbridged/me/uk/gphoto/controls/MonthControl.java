package asbridged.me.uk.gphoto.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import asbridged.me.uk.gphoto.R;

import java.util.Calendar;

/**
 * Created by David on 03/12/2015.
 http://code.tutsplus.com/tutorials/creating-compound-views-on-android--cms-22889
 */
public class MonthControl extends LinearLayout{
    private Button mPreviousButton;
    private Button mNextButton;
    private TextView mTextView;

    private static final String[] MONTHS = new String[] { "January", "February",
            "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };
    private int currentMonth = -1;


    public void setMonth(int i)
    {
        if (i >= 0 && i <= 11) {
            currentMonth = i;
            mTextView.setText(MONTHS[currentMonth]);
        }
    }

    public int getMonth() {
        return currentMonth;
    }
    public String getMonthText() {return MONTHS[currentMonth];}

    public MonthControl(Context context) {
        super(context);
        initializeViews(context);
    }

    public MonthControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public MonthControl(Context context,
                        AttributeSet attrs,
                        int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_control_number, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


        Calendar c = Calendar.getInstance();
        mTextView = (TextView) this.findViewById(R.id.sidespinner_view_current_value);
        setMonth(c.get(Calendar.MONTH));

        // Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.
        mPreviousButton = (Button) this
                .findViewById(R.id.sidespinner_view_previous);
        mPreviousButton
                .setBackgroundResource(android.R.drawable.ic_media_previous);

        mNextButton = (Button)this
                .findViewById(R.id.sidespinner_view_next);
        mNextButton
                .setBackgroundResource(android.R.drawable.ic_media_next);

        // When the previous button is pressed, go to prev month
        mPreviousButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (currentMonth > 0) {
                    setMonth(--currentMonth);
                }
            }
        });

        // When the previous button is pressed, go to prev month
        mNextButton = (Button)this
                .findViewById(R.id.sidespinner_view_next);
        mNextButton
                .setBackgroundResource(android.R.drawable.ic_media_next);
        mNextButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (currentMonth < 11) {
                    setMonth(++currentMonth);
                }
            }
        });
    }
}
