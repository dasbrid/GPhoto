package asbridged.me.uk.gphoto.detailfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.activities.MultiCheckablePhotoGridActivity;
import asbridged.me.uk.gphoto.activities.SlideshowActivity;
import asbridged.me.uk.gphoto.controls.NumberControl;
import asbridged.me.uk.gphoto.helper.LogHelper;

/**
 * Created by AsbridgeD on 15-Nov-17.
 */

public class LastNPhotosDetailFragment extends OptionDynamicDetailFragment {

    private static final String TAG = LogHelper.makeLogTag(LastNPhotosDetailFragment.class);

    private NumberControl ncLastNControl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogHelper.v(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_detail_last_n_photos, container, false);

        ncLastNControl = (NumberControl) v.findViewById(R.id.ncLastNControl);
        ncLastNControl.setMinNumber(10);
        ncLastNControl.setMaxNumber(100);
        if (savedInstanceState == null) {
            ncLastNControl.setNumber(25);
        } else {
            ncLastNControl.setNumber(savedInstanceState.getInt("currentValue"));
        }

        Button button = (Button) v.findViewById(R.id.btnShowSlideshow);
        button.setOnClickListener(this);
        button = (Button) v.findViewById(R.id.btnShowPictures);
        button.setOnClickListener(this);
        button = (Button) v.findViewById(R.id.btnShowPicturesShuffled);
        button.setOnClickListener(this);

        return v;
    }

    @Override
    public void viewAlbum() {
        int numPhotos = ncLastNControl.getNumber();

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
        int numPhotos = ncLastNControl.getNumber();

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
