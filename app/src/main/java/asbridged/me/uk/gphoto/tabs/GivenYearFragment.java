package asbridged.me.uk.gphoto.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import asbridged.me.uk.gphoto.activities.MultiCheckablePhotoGridActivity;
import asbridged.me.uk.gphoto.activities.SlideshowActivity;
import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.controls.NumberControl;

import java.util.Calendar;

/**
 * Created by David on 02/12/2015.
 */
public class GivenYearFragment extends TabFragment {

    private NumberControl ycYear;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentValue", ycYear.getNumber());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_given_year, container, false);

        ycYear= (NumberControl) v.findViewById(R.id.yearControl);
        ycYear.setMinNumber(2011);
        ycYear.setMaxNumber(2018);

        if (savedInstanceState == null) {
            Calendar c = Calendar.getInstance();
            ycYear.setNumber(c.get(Calendar.YEAR));
        } else {
            ycYear.setNumber(savedInstanceState.getInt("currentValue"));
        }

        return v;
    }

    public void doSlideshow() {
        Log.d("DAVE", "GivenYearFragment.doSlideshow" );
        // get the Year
        int year = ycYear.getNumber();

        // start the slideshow activity
        Intent intent = new Intent(getActivity(), SlideshowActivity.class);
        intent.putExtra("folderAbsolutePath", "not needed");
        intent.putExtra("albumType", "givenYear");
        intent.putExtra("position", -1);
        intent.putExtra("month", -1);
        intent.putExtra("year", year);
        this.startActivity(intent);
    }

    public void viewAlbum() {
        // do some shit
        Log.d("DAVE", "GivenYearFragment.viewAlbum" );

        // get the Year
        int year = ycYear.getNumber();

        Intent intent;

        intent = new Intent(getActivity(), MultiCheckablePhotoGridActivity.class);

        intent.putExtra("folderAbsolutePath", "not needed");
        intent.putExtra("albumName", "Photos taken in " + year);
        intent.putExtra("albumType", "givenYear");
        intent.putExtra("position", -1);
        intent.putExtra("month", -1);
        intent.putExtra("year", year);
        this.startActivity(intent);
    }
}