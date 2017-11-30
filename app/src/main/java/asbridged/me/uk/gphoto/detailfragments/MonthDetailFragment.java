package asbridged.me.uk.gphoto.detailfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import java.util.Calendar;

import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.activities.MultiCheckablePhotoGridActivity;
import asbridged.me.uk.gphoto.activities.SlideshowActivity;
import asbridged.me.uk.gphoto.helper.LogHelper;

/**
 * Created by AsbridgeD on 15-Nov-17.
 */

public class MonthDetailFragment extends OptionDynamicDetailFragment {

    private static final String TAG = LogHelper.makeLogTag(MonthDetailFragment.class);

    private static final String[] MONTHS = new String[] { "Jan", "Feb",
            "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec" };

    private NumberPicker npMonth;
    private NumberPicker npYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogHelper.v(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_detail_month, container, false);

        int startingYear;
        int startingMonth;

        if (savedInstanceState == null) {
            Calendar c = Calendar.getInstance();
            startingYear = c.get(Calendar.YEAR);
            startingMonth = c.get(Calendar.MONTH);

        } else {
            startingYear = savedInstanceState.getInt("currentYear");
            startingMonth =  savedInstanceState.getInt("currentMonth");
        }

        ImageButton button = v.findViewById(R.id.btnShowSlideshow);
        button.setOnClickListener(this);
        button = v.findViewById(R.id.btnShowPictures);
        button.setOnClickListener(this);
        button =  v.findViewById(R.id.btnShowPicturesShuffled);
        button.setOnClickListener(this);

        npYear = (NumberPicker) v.findViewById(R.id.numberControlYear);
        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        npYear.setMinValue(2011);
        //Specify the maximum value/number of NumberPicker
        npYear.setMaxValue(2030);
        npYear.setValue(startingYear);
        //Gets whether the selector wheel wraps when reaching the min/max value.
        npYear.setWrapSelectorWheel(false);

        npMonth = (NumberPicker) v.findViewById(R.id.numberControlMonth);
        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        npMonth.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        npMonth.setMaxValue(11);
        npMonth.setDisplayedValues(MONTHS);//new String[] { "Jan", "Feb", "Mar" , "Apr" , "May" , "Jun" , "Jul" , "Aug" , "Sep" , "Oct" , "Nov" , "Dec" });
        npMonth.setValue(startingMonth);
        //Gets whether the selector wheel wraps when reaching the min/max value.
        npMonth.setWrapSelectorWheel(false);

        return v;
    }

    public void viewAlbum() {
        // get the MONTH
        int month = npMonth.getValue();
        // get the Year
        int year = npYear.getValue();

        Intent intent = new Intent(getActivity(), MultiCheckablePhotoGridActivity.class);
        intent.putExtra("folderAbsolutePath", "not needed");
        intent.putExtra("albumName", "Photos taken in "+ MONTHS[npMonth.getValue()] + " in " + year);
        intent.putExtra("albumType", "givenMonth");
        intent.putExtra("position", -1);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        this.startActivity(intent);
    }

    @Override
    public void doSlideshow(boolean shuffled) {
        // get the MONTH
        int month = npMonth.getValue();
        // get the Year
        int year = npYear.getValue();

        // start the slideshow activity
        Intent intent = new Intent(getActivity(), SlideshowActivity.class);
        intent.putExtra("folderAbsolutePath", "not needed");
        intent.putExtra("albumType", "givenMonth");
        intent.putExtra("position", -1);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        intent.putExtra("playInRandomOrder", shuffled);
        this.startActivity(intent);
    }
}
