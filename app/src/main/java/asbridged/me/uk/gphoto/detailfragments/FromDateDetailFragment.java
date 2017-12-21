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
import asbridged.me.uk.gphoto.helper.SlideshowParametersConstants;

/**
 * Created by AsbridgeD on 15-Nov-17.
 */

public class FromDateDetailFragment extends OptionDynamicDetailFragment {

    private static final String TAG = LogHelper.makeLogTag(FromDateDetailFragment.class);

    private DatePicker dpFromDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogHelper.v(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_detail_from_date, container, false);

        dpFromDate = (DatePicker) v.findViewById(R.id.datepickerFromDate);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -2);
        dpFromDate.init(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH), null);

        ImageButton button = v.findViewById(R.id.btnShowSlideshow);
        button.setOnClickListener(this);
        button = v.findViewById(R.id.btnShowPictures);
        button.setOnClickListener(this);
        button =  v.findViewById(R.id.btnShowPicturesShuffled);
        button.setOnClickListener(this);

        return v;
    }

    public void viewAlbum() {
        Intent intent;

        intent = new Intent(getActivity(), MultiCheckablePhotoGridActivity.class);

        addExtrasToIntent(intent);
        this.startActivity(intent);
    }

    private void addExtrasToIntent(Intent intent) {
        // get the date
        int day = dpFromDate.getDayOfMonth();
        int month = dpFromDate.getMonth();
        int year =  dpFromDate.getYear();

        intent.putExtra(SlideshowParametersConstants.folderAbsolutePath, "not needed");
        intent.putExtra(SlideshowParametersConstants.albumName, "Photos taken after " + day + "/" + (month+1) + "/" + year);
        intent.putExtra(SlideshowParametersConstants.albumType, SlideshowParametersConstants.AlbumTypes.fromDate);
//        intent.putExtra(SlideshowParametersConstants.STARTING_PHOTO_ABSOLUTE_PATH, -1);
        intent.putExtra(SlideshowParametersConstants.month, month);
        intent.putExtra(SlideshowParametersConstants.year, year);
        intent.putExtra(SlideshowParametersConstants.day, day);
    }

    @Override
    public void doSlideshow(boolean shuffled) {
        Intent intent = new Intent(getActivity(), SlideshowActivity.class);
        addExtrasToIntent(intent);

        intent.putExtra(SlideshowParametersConstants.playInRandomOrder, shuffled);
        this.startActivity(intent);
    }
}
