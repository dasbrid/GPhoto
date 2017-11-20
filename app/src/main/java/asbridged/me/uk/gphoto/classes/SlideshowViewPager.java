package asbridged.me.uk.gphoto.classes;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import asbridged.me.uk.gphoto.adapter.SlideshowPagerAdapter;

import java.io.File;

/**
 * Created by David on 07/11/2015.
 * For disabling swipe see
 * http://stackoverflow.com/questions/9650265/how-do-disable-paging-by-swiping-with-finger-in-viewpager-but-still-be-able-to-s
 */
public class SlideshowViewPager extends ViewPager {

    OnTouchedListener mCallback = null;
    // implement this interface to listen
    public interface OnTouchedListener {
        public void onTouched();
    }

    public SlideshowViewPager(Context context) {
        super(context);
    }

    public SlideshowViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCurrentItemManual(int pageNumber) {
        if (pageNumber != getCurrentItem())
            setCurrentItem(pageNumber);
    }

    public void getCurrentFile()
    {
        int currentIndex = getCurrentItem();
        SlideshowPagerAdapter adapter = (SlideshowPagerAdapter) getAdapter();
        File currentFile = adapter.getFileAtPosition(currentIndex);
        Log.d("DAVE","file = "+currentFile.getName());
    }

    public void setOnTouchedListener(OnTouchedListener otl) {
        mCallback = otl;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCallback != null)
            mCallback.onTouched();
        return super.onTouchEvent(event);
    }
}
