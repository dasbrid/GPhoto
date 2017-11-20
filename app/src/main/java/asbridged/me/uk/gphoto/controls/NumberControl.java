package asbridged.me.uk.gphoto.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import asbridged.me.uk.gphoto.R;

/**
 * Created by David on 03/12/2015.
 http://code.tutsplus.com/tutorials/creating-compound-views-on-android--cms-22889
 */
public class NumberControl extends LinearLayout{
    private Button mPreviousButton;
    private Button mNextButton;
    private TextView mTextView;

    private int minNumber;
    private int maxNumber;

    private int currentNumber;

    public void setMinNumber(int i) {
        minNumber = i;
        if (currentNumber < i) currentNumber = i;
    }

    public void setMaxNumber(int i) {
        maxNumber = i;
        if (currentNumber > i) currentNumber = i;
    }

    public void setNumber(int i)
    {
        if (i < minNumber) currentNumber = minNumber;
        else if (i > maxNumber) currentNumber = maxNumber;
        else currentNumber = i;

        mTextView.setText(Integer.toString(currentNumber));
    }

    public int getNumber() {
        return currentNumber;
    }

    public NumberControl(Context context) {
        super(context);
        initializeViews(context);
    }

    public NumberControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public NumberControl(Context context,
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
        mTextView = (TextView) this.findViewById(R.id.sidespinner_view_current_value);
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

        // When the previous button is pressed, go to prev year
        mPreviousButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (currentNumber > minNumber) {
                    setNumber(--currentNumber);
                }
            }
        });

        // When the next button is pressed, select the next item in the
        // list.
        mNextButton = (Button)this
                .findViewById(R.id.sidespinner_view_next);
        mNextButton
                .setBackgroundResource(android.R.drawable.ic_media_next);
        mNextButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (currentNumber < maxNumber) {
                    setNumber(++currentNumber);
                }
            }
        });
    }
}
