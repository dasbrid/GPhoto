package asbridged.me.uk.gphoto.detailfragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Calendar;

import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.activities.MultiCheckablePhotoGridActivity;
import asbridged.me.uk.gphoto.activities.SlideshowActivity;
import asbridged.me.uk.gphoto.helper.LogHelper;

/**
 * Created by AsbridgeD on 15-Nov-17.
 */

public class GivenPeriodDetailFragment extends OptionDynamicDetailFragment {

    private static final String TAG = LogHelper.makeLogTag(GivenPeriodDetailFragment.class);

    Calendar c;
    String albumName;

    RadioGroup radioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_given_period, container, false);
        //radioGroup =(RadioGroup)v.findViewById(R.title.radioGroup);
        radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
        RadioButton rbDefault = (RadioButton) v.findViewById(R.id.rbAllPhotos);
        rbDefault.setChecked(true);

        ImageButton button = v.findViewById(R.id.btnShowSlideshow);
        button.setOnClickListener(this);
        button = v.findViewById(R.id.btnShowPictures);
        button.setOnClickListener(this);
        button =  v.findViewById(R.id.btnShowPicturesShuffled);
        button.setOnClickListener(this);

        return v;
    }

    @Override
    public void doSlideshow(boolean shuffled) {
        int selectedId= radioGroup.getCheckedRadioButtonId();

        Log.d(TAG,"selectedId="+selectedId);
        Intent intent = new Intent(getActivity(), SlideshowActivity.class);
        intent.putExtra("folderAbsolutePath", "not needed");

        if (selectedId == R.id.rbAllPhotos) {
            intent.putExtra("albumType", "allPhotos");
            intent.putExtra("albumName", "All photos");
            intent.putExtra("position", -1);
            intent.putExtra("month", -1);
            intent.putExtra("year", -1);
            intent.putExtra("playInRandomOrder", shuffled);
            this.startActivity(intent);
            return;
        } else if (selectedId == R.id.rbLastYear) {
            intent.putExtra("albumType", "lastYear");
            intent.putExtra("albumName", "Photos from last year");
            intent.putExtra("position", -1);
            intent.putExtra("month", -1);
            intent.putExtra("year", -1);
            intent.putExtra("playInRandomOrder", shuffled);
            this.startActivity(intent);
            return;
        }

        // all other options are photos from a given date to present
        getStartDateAndAlbumName(selectedId);

        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        // start the slideshow activity

        intent.putExtra("albumType", "fromDate");
        intent.putExtra("albumName", albumName);
        intent.putExtra("position", -1);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        intent.putExtra("day", day);
        intent.putExtra("playInRandomOrder", shuffled);
        this.startActivity(intent);
    }

    public void getStartDateAndAlbumName(int selectedId) {
        c = Calendar.getInstance();

        albumName = "Photos";
        switch (selectedId) {
            case (R.id.rbInLastWeek): {
                c.add(Calendar.DATE, -7);
                albumName = "Photos in the last 7 days";
                break;
            }
            case (R.id.rbThisMonth): {
                c.set(Calendar.DAY_OF_MONTH, 1);
                albumName = "Photos taken this month";
                break;
            }
            case (R.id.rbInLastMonth): {
                c.add(Calendar.MONTH, -1);
                albumName = "Photos taken in the past month";
                break;
            }
            case (R.id.rbInLast3Months): {
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.add(Calendar.MONTH, -3);
                albumName = "Photos taken in the past 3 months";
                break;
            }
            case (R.id.rbThisYear): {
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.MONTH, 0);
                albumName = "Photos taken this year";
                break;
            }
            case (R.id.rbInLastYear): {
                c.add(Calendar.YEAR, -1);
                albumName = "Photos during the past year";
                break;
            }
        }
    }

    public void viewAlbum() {
        int selectedId= radioGroup.getCheckedRadioButtonId();
        Log.d(TAG,"selectedId="+selectedId);
        Intent intent;

//////////////////////////////        intent = new Intent(getActivity(), MultiCheckablePhotoGridActivity.class);
        intent = new Intent(getActivity(), MultiCheckablePhotoGridActivity.class);

        intent.putExtra("folderAbsolutePath", "not needed");

        if (selectedId == R.id.rbAllPhotos) {
            intent.putExtra("albumType", "allPhotos");
            intent.putExtra("albumName", "All photos");
            intent.putExtra("position", -1);

            intent.putExtra("month", -1);
            intent.putExtra("year", -1);
            this.startActivity(intent);
            return;
        } else if (selectedId == R.id.rbLastYear) {
            intent.putExtra("albumType", "lastYear");
            intent.putExtra("albumName", "Photos from last year");
            intent.putExtra("position", -1);
            intent.putExtra("month", -1);
            intent.putExtra("year", -1);
            this.startActivity(intent);
            return;
        }

        // all other options are photos from a given date to present
        getStartDateAndAlbumName(selectedId);

        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        // start the slideshow activity
        intent.putExtra("albumType", "fromDate");
        intent.putExtra("albumName", albumName);
        intent.putExtra("position", -1);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        intent.putExtra("day", day);
        this.startActivity(intent);
    }

}
