package asbridged.me.uk.gphoto.detailfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;

import java.util.Calendar;

import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.activities.MultiCheckablePhotoGridActivity;
import asbridged.me.uk.gphoto.activities.SlideshowActivity;
import asbridged.me.uk.gphoto.helper.LogHelper;

/**
 * Created by AsbridgeD on 15-Nov-17.
 */

public class BetweenDatesDetailFragment extends OptionDynamicDetailFragment {

    private static final String TAG = LogHelper.makeLogTag(BetweenDatesDetailFragment.class);

    private DatePicker dpFromDate;
    private DatePicker dpToDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogHelper.v(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_detail_between_dates, container, false);

        dpFromDate = (DatePicker) v.findViewById(R.id.datepickerFromDate);
        dpToDate = (DatePicker) v.findViewById(R.id.datepickerToDate);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -2);
        dpFromDate.init(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH), null);
        dpToDate.init(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH), null);

        ImageButton button = v.findViewById(R.id.btnShowSlideshow);
        button.setOnClickListener(this);
        button = v.findViewById(R.id.btnShowPictures);
        button.setOnClickListener(this);
        button =  v.findViewById(R.id.btnShowPicturesShuffled);
        button.setOnClickListener(this);

        return v;
    }

    public void viewAlbum() {
        // get the from date
        int day = dpFromDate.getDayOfMonth();
        int month = dpFromDate.getMonth();
        int year =  dpFromDate.getYear();

        // get the to date
        int toDay = dpToDate.getDayOfMonth();
        int toMonth = dpToDate.getMonth();
        int toYear =  dpToDate.getYear();

        Intent intent;

        intent = new Intent(getActivity(), MultiCheckablePhotoGridActivity.class);

        intent.putExtra("folderAbsolutePath", "not needed");
        intent.putExtra("albumName", "Photos taken between " + day + "/" + (month+1) + "/" + year + " and " + toDay + "/" + (toMonth+1) + "/" + toYear);
        intent.putExtra("albumType", "betweenDates");
        intent.putExtra("position", -1);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        intent.putExtra("day", day);
        intent.putExtra("tomonth", toMonth);
        intent.putExtra("toyear", toYear);
        intent.putExtra("today", toDay);
        this.startActivity(intent);
    }

    @Override
    public void doSlideshow(boolean shuffled) {
        // get the date
        int day = dpFromDate.getDayOfMonth();
        int month = dpFromDate.getMonth();
        int year =  dpFromDate.getYear();

        // get the to date
        int toDay = dpToDate.getDayOfMonth();
        int toMonth = dpToDate.getMonth();
        int toYear =  dpToDate.getYear();

        // start the slideshow activity
        Intent intent = new Intent(getActivity(), SlideshowActivity.class);
        intent.putExtra("folderAbsolutePath", "not needed");
        intent.putExtra("albumType", "betweenDates");
        intent.putExtra("position", -1);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        intent.putExtra("day", day);
        intent.putExtra("tomonth", toMonth);
        intent.putExtra("toyear", toYear);
        intent.putExtra("today", toDay);
        intent.putExtra("playInRandomOrder", shuffled);
        this.startActivity(intent);
    }
}
