package asbridged.me.uk.gphoto.detailfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import java.util.Calendar;

import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.activities.MultiCheckablePhotoGridActivity;
import asbridged.me.uk.gphoto.activities.SlideshowActivity;
import asbridged.me.uk.gphoto.helper.LogHelper;
import asbridged.me.uk.gphoto.helper.SlideshowParametersConstants;

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
        Intent intent;

        intent = new Intent(getActivity(), MultiCheckablePhotoGridActivity.class);

        addExtrasToIntent(intent);
        this.startActivity(intent);
    }

    private void addExtrasToIntent(Intent intent) {
        // get the Year
        int year = np.getValue();
        intent.putExtra(SlideshowParametersConstants.folderAbsolutePath, "not needed");
        intent.putExtra(SlideshowParametersConstants.albumName, "Photos taken in " + year);
        intent.putExtra(SlideshowParametersConstants.albumType, SlideshowParametersConstants.AlbumTypes.givenYear);
 //       intent.putExtra(SlideshowParametersConstants.STARTING_PHOTO_ABSOLUTE_PATH, -1);
        intent.putExtra(SlideshowParametersConstants.month, -1);
        intent.putExtra(SlideshowParametersConstants.year, year);
    }

    @Override
    public void doSlideshow(boolean shuffled) {
        Intent intent = new Intent(getActivity(), SlideshowActivity.class);
        addExtrasToIntent(intent);
        intent.putExtra(SlideshowParametersConstants.playInRandomOrder, shuffled);
        this.startActivity(intent);
    }
}
