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

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

/**
 * Created by AsbridgeD on 15-Nov-17.
 */

public class YearDetailFragment extends OptionDynamicDetailFragment {

    private static final String TAG = LogHelper.makeLogTag(YearDetailFragment.class);

    private NumberPicker np;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogHelper.v(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_detail_year, container, false);

        ImageButton button = v.findViewById(R.id.btnShowSlideshow);
        button.setOnClickListener(this);
        button = v.findViewById(R.id.btnShowPictures);
        button.setOnClickListener(this);
        button =  v.findViewById(R.id.btnShowPicturesShuffled);
        button.setOnClickListener(this);

        int startingValue;
        if (savedInstanceState == null) {
            Calendar c = Calendar.getInstance();
            startingValue = c.get(Calendar.YEAR);
        } else {
            startingValue = savedInstanceState.getInt("currentValue");
        }

        np = (NumberPicker) v.findViewById(R.id.numberpickercontrol);

        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        np.setMinValue(2011);
        //Specify the maximum value/number of NumberPicker
        np.setMaxValue(2030);
        np.setValue(startingValue);
        //Gets whether the selector wheel wraps when reaching the min/max value.
        np.setWrapSelectorWheel(false);

        return v;
    }

    public void viewAlbum() {
        // get the Year
        int year = np.getValue();
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
        int year = np.getValue();

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
