package asbridged.me.uk.gphoto.detailfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.activities.MultiCheckablePhotoGridActivity;
import asbridged.me.uk.gphoto.activities.SlideshowActivity;
import asbridged.me.uk.gphoto.helper.LogHelper;

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
        npLastNControl.setMinValue(10);
        npLastNControl.setMaxValue(100);
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
        int numPhotos = npLastNControl.getValue();

        // start the slideshow activity
        Intent intent = new Intent(getActivity(), MultiCheckablePhotoGridActivity.class);
        intent.putExtra("folderAbsolutePath", "not needed");
        intent.putExtra("albumName", "Most recent " + numPhotos + " photos");
        intent.putExtra("albumType", "lastNPhotos");
        intent.putExtra("position", -1);
        intent.putExtra("month", -1);
        intent.putExtra("year", -1);
        intent.putExtra("numPhotos", numPhotos);
        this.startActivity(intent);
    }

    @Override
    public void doSlideshow(boolean shuffled) {
        int numPhotos = npLastNControl.getValue();

        // start the slideshow activity
        Intent intent = new Intent(getActivity(), SlideshowActivity.class);
        intent.putExtra("folderAbsolutePath", "not needed");
        intent.putExtra("albumType", "lastNPhotos");
        intent.putExtra("position", -1);
        intent.putExtra("month", -1);
        intent.putExtra("year", -1);
        intent.putExtra("numPhotos", numPhotos);
        intent.putExtra("playInRandomOrder", shuffled);
        this.startActivity(intent);
    }
}
