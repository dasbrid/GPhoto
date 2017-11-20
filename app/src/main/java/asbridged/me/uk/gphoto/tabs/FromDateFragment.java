package asbridged.me.uk.gphoto.tabs;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import asbridged.me.uk.gphoto.activities.MultiCheckablePhotoGridActivity;
import asbridged.me.uk.gphoto.activities.SlideshowActivity;
import asbridged.me.uk.gphoto.R;

import java.util.Calendar;

/**
 * Created by David on 02/12/2015.
 */
public class FromDateFragment extends TabFragment {

    private DatePicker dpFromDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_from_date, container, false);
        dpFromDate = (DatePicker) v.findViewById(R.id.datepickerFromDate);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -2);
        dpFromDate.init(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH), null);
        return v;
    }

    public void doSlideshow() {
        Log.d("DAVE", "GivenYearFragment.doSlideshow" );
        // get the date
        int day = dpFromDate.getDayOfMonth();
        int month = dpFromDate.getMonth();
        int year =  dpFromDate.getYear();

        // start the slideshow activity
        Intent intent = new Intent(getActivity(), SlideshowActivity.class);
        intent.putExtra("folderAbsolutePath", "not needed");
        intent.putExtra("albumType", "fromDate");
        intent.putExtra("position", -1);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        intent.putExtra("day", day);
        this.startActivity(intent);
    }

    public void viewAlbum() {
        Log.d("DAVE", "GivenYearFragment.viewAlbum" );
        // get the date
        int day = dpFromDate.getDayOfMonth();
        int month = dpFromDate.getMonth();
        int year =  dpFromDate.getYear();

        Intent intent;

        intent = new Intent(getActivity(), MultiCheckablePhotoGridActivity.class);

        intent.putExtra("folderAbsolutePath", "not needed");
        intent.putExtra("albumName", "Photos taken after " + day + "/" + (month+1) + "/" + year);
        intent.putExtra("albumType", "fromDate");
        intent.putExtra("position", -1);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        intent.putExtra("day", day);
        this.startActivity(intent);
    }
}