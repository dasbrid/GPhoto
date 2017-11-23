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
import asbridged.me.uk.gphoto.controls.NumberControl;
import asbridged.me.uk.gphoto.helper.LogHelper;

/**
 * Created by AsbridgeD on 15-Nov-17.
 */

public class YearDetailFragment extends OptionDynamicDetailFragment {

    private static final String TAG = LogHelper.makeLogTag(YearDetailFragment.class);

    private NumberControl ycYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogHelper.v(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_detail_year, container, false);

        ycYear= (NumberControl) v.findViewById(R.id.yearControl);
        ycYear.setMinNumber(2011);
        ycYear.setMaxNumber(2018);

        if (savedInstanceState == null) {
            Calendar c = Calendar.getInstance();
            ycYear.setNumber(c.get(Calendar.YEAR));
        } else {
            ycYear.setNumber(savedInstanceState.getInt("currentValue"));
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

    @Override
    public void doSlideshow(boolean shuffled) {
        // get the Year
        int year = ycYear.getNumber();

        // start the slideshow activity
        Intent intent = new Intent(getActivity(), SlideshowActivity.class);
        intent.putExtra("folderAbsolutePath", "not needed");
        intent.putExtra("albumType", "givenYear");
        intent.putExtra("position", -1);
        intent.putExtra("month", -1);
        intent.putExtra("year", year);
        intent.putExtra("playInRandomOrder", shuffled);
        this.startActivity(intent);
    }
}
