package asbridged.me.uk.gphoto.detailfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.activities.MultiCheckablePhotoGridActivity;
import asbridged.me.uk.gphoto.activities.SlideshowActivity;
import asbridged.me.uk.gphoto.helper.AppConstant;
import asbridged.me.uk.gphoto.helper.LogHelper;
import asbridged.me.uk.gphoto.helper.SlideshowParametersConstants;

/**
 * Created by AsbridgeD on 15-Nov-17.
 */

public class LastNPhotosDetailFragment extends OptionDynamicDetailFragment {

    private static final String TAG = LogHelper.makeLogTag(LastNPhotosDetailFragment.class);

    private NumberPicker npLastNControl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogHelper.v(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_detail_last_n_photos, container, false);

        npLastNControl = (NumberPicker) v.findViewById(R.id.npLastNControl);
        npLastNControl.setMinValue(AppConstant.LAST_N_PHOTOS_MIN);
        npLastNControl.setMaxValue(AppConstant.LAST_N_PHOTOS_MAX);
        LogHelper.i(TAG, "savedInstanceState=",savedInstanceState==null?"null":"not null");
        int startingValue;
        if (savedInstanceState == null) {
            startingValue = 25;
        } else {
            startingValue = savedInstanceState.getInt("currentValue");
        }
        npLastNControl.setValue(startingValue);

        ImageButton button = v.findViewById(R.id.btnShowSlideshow);
        button.setOnClickListener(this);
        button = v.findViewById(R.id.btnShowPictures);
        button.setOnClickListener(this);
        button =  v.findViewById(R.id.btnShowPicturesShuffled);
        button.setOnClickListener(this);

        return v;
    }

    @Override
    public void viewAlbum() {


        // start the slideshow activity
        Intent intent = new Intent(getActivity(), MultiCheckablePhotoGridActivity.class);
        addExtrasToIntent(intent);
        this.startActivity(intent);
    }

    private void addExtrasToIntent(Intent intent) {
        int numPhotos = npLastNControl.getValue();

        intent.putExtra(SlideshowParametersConstants.folderAbsolutePath, "not needed");
        intent.putExtra(SlideshowParametersConstants.albumName, "Most recent " + numPhotos + " photos");
        intent.putExtra(SlideshowParametersConstants.albumType, SlideshowParametersConstants.AlbumTypes.lastNPhotos);
//        intent.putExtra(SlideshowParametersConstants.STARTING_PHOTO_ABSOLUTE_PATH, -1);
        intent.putExtra(SlideshowParametersConstants.month, -1);
        intent.putExtra(SlideshowParametersConstants.year, -1);
        intent.putExtra(SlideshowParametersConstants.numPhotos, numPhotos);
    }

    @Override
    public void doSlideshow(boolean shuffled) {
        Intent intent = new Intent(getActivity(), SlideshowActivity.class);
        addExtrasToIntent(intent);
        intent.putExtra(SlideshowParametersConstants.playInRandomOrder, shuffled);
        this.startActivity(intent);
    }
}
