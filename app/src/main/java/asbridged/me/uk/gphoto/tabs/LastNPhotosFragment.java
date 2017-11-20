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

/**
 * Created by David on 02/12/2015.
 */
public class LastNPhotosFragment extends TabFragment {

    private static final String TAG = "LastNPhotosFragment";

    private NumberControl ncLastNControl;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentValue", ncLastNControl.getNumber());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_last_n_photos, container, false);

        ncLastNControl = (NumberControl) v.findViewById(R.id.ncLastNControl);
        ncLastNControl.setMinNumber(10);
        ncLastNControl.setMaxNumber(100);
        if (savedInstanceState == null) {
            ncLastNControl.setNumber(25);
        } else {
            ncLastNControl.setNumber(savedInstanceState.getInt("currentValue"));
        }
        return v;
    }

    public void doSlideshow() {
        // get the Year
        int numPhotos = ncLastNControl.getNumber();

        // start the slideshow activity
        Intent intent = new Intent(getActivity(), SlideshowActivity.class);
        intent.putExtra("folderAbsolutePath", "not needed");
        intent.putExtra("albumType", "lastNPhotos");
        intent.putExtra("position", -1);
        intent.putExtra("month", -1);
        intent.putExtra("year", -1);
        intent.putExtra("numPhotos", numPhotos);
        this.startActivity(intent);
    }

    public void viewAlbum() {
        // get the Year
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
}