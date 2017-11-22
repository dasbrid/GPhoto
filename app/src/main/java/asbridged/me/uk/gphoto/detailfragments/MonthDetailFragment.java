package asbridged.me.uk.gphoto.detailfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Calendar;

import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.activities.MultiCheckablePhotoGridActivity;
import asbridged.me.uk.gphoto.activities.SlideshowActivity;
import asbridged.me.uk.gphoto.controls.MonthControl;
import asbridged.me.uk.gphoto.controls.NumberControl;
import asbridged.me.uk.gphoto.helper.LogHelper;

/**
 * Created by AsbridgeD on 15-Nov-17.
 */

public class MonthDetailFragment extends OptionDynamicDetailFragment {

    private static final String TAG = LogHelper.makeLogTag(MonthDetailFragment.class);

    private static final String[] MONTHS = new String[] { "January", "February",
            "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };

    private NumberControl ycYear;
    private MonthControl mcMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogHelper.v(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_detail_month, container, false);

        ycYear= (NumberControl) v.findViewById(R.id.yearControlMonth);
        mcMonth = (MonthControl) v.findViewById(R.id.monthControlMonth);
        ycYear.setMinNumber(2011);
        ycYear.setMaxNumber(2019);

        if (savedInstanceState == null) {
            Calendar c = Calendar.getInstance();
            ycYear.setNumber(c.get(Calendar.YEAR));
            mcMonth.setMonth(c.get(Calendar.MONTH));

        } else {
            ycYear.setNumber(savedInstanceState.getInt("currentYear"));
            mcMonth.setMonth(savedInstanceState.getInt("currentMonth"));
        }

        Button button = (Button) v.findViewById(R.id.btnShowSlideshow);
        button.setOnClickListener(this);
        button = (Button) v.findViewById(R.id.btnShowPictures);
        button.setOnClickListener(this);
        button = (Button) v.findViewById(R.id.btnShowPicturesShuffled);
        button.setOnClickListener(this);

        return v;
    }

    public void viewAlbum() {
        // get the MONTH
        int month = mcMonth.getMonth();
        // get the Year
        int year = ycYear.getNumber();

        Intent intent = new Intent(getActivity(), MultiCheckablePhotoGridActivity.class);
        intent.putExtra("folderAbsolutePath", "not needed");
        intent.putExtra("albumName", "Photos taken in "+mcMonth.getMonthText() + " in " + year);
        intent.putExtra("albumType", "givenMonth");
        intent.putExtra("position", -1);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        this.startActivity(intent);
    }

    @Override
    public void doSlideshow(boolean shuffled) {
        // get the MONTH
        int month = mcMonth.getMonth();
        // get the Year
        int year = ycYear.getNumber();

        // start the slideshow activity
        Intent intent = new Intent(getActivity(), SlideshowActivity.class);
        intent.putExtra("folderAbsolutePath", "not needed");
        intent.putExtra("albumType", "givenMonth");
        intent.putExtra("position", -1);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        this.startActivity(intent);
    }
}
