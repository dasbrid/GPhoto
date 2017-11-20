package asbridged.me.uk.gphoto.activities;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.*;
import asbridged.me.uk.gphoto.classes.DeleteConfirmDialog;
import asbridged.me.uk.gphoto.classes.SlideshowViewPager;
import asbridged.me.uk.gphoto.classes.SlideshowSpeedDialog;
import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.adapter.SlideshowPagerAdapter;
import asbridged.me.uk.gphoto.helper.AppConstant;
import asbridged.me.uk.gphoto.helper.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

/**
 * Created by David on 04/11/2015.
 * See http://developer.android.com/training/animation/screen-slide.html
 */
public class SlideshowActivity extends Activity
        implements
        SlideshowViewPager.OnTouchedListener,
        DeleteConfirmDialog.DeleteDialogOKListener,
        SlideshowSpeedDialog.SlideshowSpeedChangedListener,
        ToggleButton.OnCheckedChangeListener,
        RadioGroup.OnCheckedChangeListener
{

    private final static String TAG = "SlideshowActivity";

    private int numPages;
    private ArrayList<File> filelist;
    private boolean slideshowOn;
    private boolean slideshowSharedState;
    private boolean shuffleOn;
    private boolean shuffleSharedState;
    private boolean modified;

    // The time (in seconds) for which the navigation controls are visible after screen interaction.
    private static int SHOW_NAVIGATION_CONTROLS_TIME = 3;
    // Delay between slides
    //private static int SLIDE_SHOW_DELAY;

    private Timer timer;
    private int page = 0;

    private Button btnPhotoShare;
    private Button btnPhotoDelete;
    private Button btnStartSlideshow;
    private Button btnSlideshowSpeed;
    private RadioButton rbtnShuffleOn;
    private RadioButton rbtnShuffleOff;
    private RadioGroup radioGroupShuffle;

    private SlideshowPagerAdapter mSlideshowPagerAdapter;
    private SlideshowViewPager mViewPager;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (slideshowOn == false)
                return;
            if (shuffleOn && filelist.size() > 1) {
                int newpage = page;
                Random r = new Random();
                while (newpage == page) {
                    newpage = r.nextInt(filelist.size());
                }
                Log.d(TAG, "page = "+page+"chosen random page "+newpage);
                page = newpage;
            } else {
                Log.d(TAG, "page = "+page+" choosing next page");
                page++;
                if (page >= numPages)
                    page = 0;
            }

            Utils.setLastDisplayed(getApplicationContext(),Integer.toString(page));
            mViewPager.setCurrentItem(page,true);

            // to keep the slideshow going, start the timer again
            int ssd = Utils.getSlideshowDelay(getApplicationContext());
            handler.postDelayed(this, ssd * 1000);

        }
    };

    // Runnable called by handler x seconds after user interaction
    private Runnable hidenavigation = new  Runnable() {
        @Override
        public void run() {
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(uiOptions);
            getActionBar().hide();
        }
    };

    // callback from the slideshow speed dialog after OK clicked
    public void slideshowSpeedChanged(int newSpeed)
    {
        Utils.setSlideshowDelay(this,newSpeed);
    }

    // button clicked - restart the slideshow
    public void btnPhotoStartSlideshowClicked(View v) {
        btnStartSlideshow.setVisibility(View.INVISIBLE);
        btnPhotoDelete.setVisibility(View.INVISIBLE);
        btnPhotoShare.setVisibility(View.INVISIBLE);
        radioGroupShuffle.setVisibility(View.INVISIBLE);
        btnSlideshowSpeed.setVisibility(View.INVISIBLE);
        page = mViewPager.getCurrentItem();
        startSlideshow();
    }

    // called back from the pager when touched
    public void onTouched() {
        stopSlideshow();
        btnStartSlideshow.setVisibility(View.VISIBLE);
        btnPhotoDelete.setVisibility(View.VISIBLE);
        btnPhotoShare.setVisibility(View.VISIBLE);
        radioGroupShuffle.setVisibility(View.VISIBLE);
        btnSlideshowSpeed.setVisibility(View.VISIBLE);
    }

    private void stopSlideshow() {
        slideshowOn = false;
    }

    private void startSlideshow() {
        slideshowOn = true;
        handler.postDelayed(runnable, Utils.getSlideshowDelay(this));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);        // Save the slideshow status
        savedInstanceState.putBoolean("slideshowOn", slideshowOn);
        savedInstanceState.putBoolean("shuffleOn", shuffleOn);
        savedInstanceState.putInt("currentPage", mViewPager.getCurrentItem());
    }

    @Override
    public void onStop() {
        super.onStop();
        slideshowSharedState= slideshowOn;
        slideshowOn = false;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState");
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        slideshowSharedState = savedInstanceState.getBoolean("slideshowOn");
        shuffleSharedState = savedInstanceState.getBoolean("shuffleOn");
        page = savedInstanceState.getInt("currentPage");
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("modified",modified);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    // Called after starting or when resuming (no saved instance state)
    @Override
    protected void onResume() {
        super.onResume();  // Always call the superclass method first
        Log.v(TAG, "onResume");
        // get the saved (in memory state of the slideshow)
        slideshowOn = slideshowSharedState;
        shuffleOn = shuffleSharedState;
        mViewPager.setCurrentItem(page);
        rbtnShuffleOn.setChecked(shuffleOn);
        rbtnShuffleOff.setChecked(!shuffleOn);

        if (slideshowOn) {
            startSlideshow();
            btnStartSlideshow.setVisibility(View.INVISIBLE);
            btnPhotoDelete.setVisibility(View.INVISIBLE);
            btnPhotoShare.setVisibility(View.INVISIBLE);
            radioGroupShuffle.setVisibility(View.INVISIBLE);
            btnSlideshowSpeed.setVisibility(View.INVISIBLE);
        } else {
            btnStartSlideshow.setVisibility(View.VISIBLE);
            btnPhotoDelete.setVisibility(View.VISIBLE);
            btnPhotoShare.setVisibility(View.VISIBLE);
            radioGroupShuffle.setVisibility(View.VISIBLE);
            btnSlideshowSpeed.setVisibility(View.VISIBLE);
        }
        // Code to make layout fullscreen. In onResume, otherwise when activity comes back it will revert to non-fullscreen
        // http://developer.android.com/training/system-ui/status.html
        // hide the status bar and application bar (top of the screen) with SYSTEM_UI_FLAG_FULLSCREEN
        // hide the navigation bar (back, home at bottom of screen) with SYSTEM_UI_FLAG_HIDE_NAVIGATION
        // SYSTEM_UI_FLAG_LAYOUT_STABLE puts the navigation controls on top of the activity layout (stays fullscreen)
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
        // You should never show the action bar (application bar) if the
        // status bar is hidden, so hide that too if necessary.
//// GIVES NULL REFERENCE       ActionBar actionBar = getActionBar();
//// GIVES NULL REFERENCE EXCEPTION       actionBar.hide();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId != -1) {
            if (checkedId == R.id.rbShuffleOn)
                shuffleOn = true;
            else
                shuffleOn = false;
            Log.d(TAG, "shuffle turned "+(shuffleOn?"on":"off"));
        }
    }


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "shuffle turned "+(isChecked?"on":"off"));
        shuffleOn = isChecked;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"onCreate");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionCheck = ContextCompat.checkSelfPermission(SlideshowActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // We do not have necessary permission. Must ask the user
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                // No explanation needed, we can request the permission.

                startActivity(new Intent(SlideshowActivity.this, GetPermissionsActivity.class));
                finish();
                return;
            }
        }

        // SLIDE_SHOW_DELAY = Integer.parseInt(Utils.getSlideshowDelay(this)); // in seconds
        // if we are starting for first time (not restarting)
        if (savedInstanceState == null) {
            Log.v("TAG","Saved instance state == null");
            slideshowSharedState = true;
            slideshowOn = true;
            shuffleSharedState = true;
        }

        setContentView(R.layout.activity_slideshow);
        btnStartSlideshow = (Button) findViewById(R.id.btnPhotoStartSlideshow);
        btnPhotoDelete = (Button) findViewById(R.id.btnPhotoDelete);
        btnPhotoShare = (Button) findViewById(R.id.btnPhotoShare);
        btnSlideshowSpeed = (Button) findViewById(R.id.btnSlideShowSpeed);
        radioGroupShuffle = (RadioGroup) findViewById(R.id.radioGroupShuffleSlideshow);
        rbtnShuffleOff = (RadioButton) findViewById(R.id.rbShuffleOff);
        rbtnShuffleOn = (RadioButton) findViewById(R.id.rbShuffleOn);


        radioGroupShuffle.setOnCheckedChangeListener(this);

        mSlideshowPagerAdapter = new SlideshowPagerAdapter(this);
        mViewPager = (SlideshowViewPager) findViewById(R.id.slideshowpager);
        mViewPager.setAdapter(mSlideshowPagerAdapter);

        mViewPager.setOnTouchedListener(this);

        Bundle parameters = getIntent().getExtras();
        String albumFolder = parameters.getString("folderAbsolutePath");
        Integer positionParameter = parameters.getInt("position");
        String albumType = parameters.getString("albumType");
        int albumMonth = parameters.getInt("month");
        int albumYear = parameters.getInt("year");
        int albumDay = -1;
        if (albumType.equals("fromDate"))
            albumDay = parameters.getInt("day");
        long albumBucketID = -1;
        int numPhotos = 0;
        ArrayList<String> bucketIDstrings = new ArrayList<String>();
        if (albumType.equals("bucket")) {
            albumBucketID = parameters.getLong("albumBucketID");
        } else if (albumType.equals("multipleBuckets")) {
            bucketIDstrings = parameters.getStringArrayList("bucketIDs");
        } else if (albumType.equals("lastNPhotos")) {
            numPhotos = parameters.getInt("numPhotos");
        }
        Log.d(TAG, "positionParameter = "+positionParameter);
        if (positionParameter != -1)
        {
            // we have been passed a specific photo index.
            // show the photo and slideshow off
            page = positionParameter;
            slideshowSharedState = false;
        } else {
            page = 0;
            slideshowSharedState = true;
        }

        Log.d("DAVE", "displaying photos for " + albumMonth +"/" + albumYear);

        if (albumType.equals("lastYear")) {
            filelist = Utils.getPhotosLastYear(this);
        } else if (albumType.equals("lastNPhotos")) {
            filelist = Utils.getLastNPhotosinMedia(this, numPhotos);
        } else if (albumType.equals("multipleBuckets")) {
            filelist = Utils.getMediaInListofBuckets(this, bucketIDstrings);
        } else if (albumType.equals("bucket")) {
            filelist = Utils.getMediaInBucketID(this, albumBucketID);
        } else if (albumType.equals("thisYear")) {
            filelist = Utils.getMediaInCurrentYear(this);
        } else if (albumType.equals("fromDate")) {
            filelist = Utils.getMediaFromDate(this,albumDay, albumMonth, albumYear);
        } else if (albumType.equals("allPhotos")) {
            // ALL files
            filelist = Utils.getAllMedia(this);
        }
        else if (albumMonth == -1 && albumYear != -1) {
            // Year but no month ... Get all for this year
            filelist = Utils.getMediaInYear(this, albumYear);
        } else if (albumMonth == -2 && albumYear == -2) {
            // Get RECENT files
            filelist = Utils.getRecentMedia(this);
        } else {
            filelist = Utils.getMediaInMonth(this, albumMonth, albumYear);
        }


        if (filelist == null)
        {
            filelist = new ArrayList<File>();
            Toast.makeText(this,"No pictures found", Toast.LENGTH_LONG).show();
        }

        mSlideshowPagerAdapter.setFileList(filelist);
        mSlideshowPagerAdapter.notifyDataSetChanged();

        modified = false;
        numPages = filelist.size();
    }

    // button delete clicked.
    public void btnSlideShowSpeedClicked(View v) {
    // show confirm dialog
    FragmentManager fm = getFragmentManager();
    SlideshowSpeedDialog slideshowspeeddialog = new SlideshowSpeedDialog();
    Bundle args = new Bundle();
    args.putInt("currentValue", Utils.getSlideshowDelay(this));
    slideshowspeeddialog.setArguments(args);
    slideshowspeeddialog.show(fm, "fragment_slideshowspeed_dialog");
    }

    // button delete clicked.
    public void btnPhotoDeleteClicked(View v)
    {
        // show confirm dialog
        FragmentManager fm = getFragmentManager();
        DeleteConfirmDialog deleteDialog = new DeleteConfirmDialog();
        Bundle args = new Bundle();
        args.putString("title", "Delete Picture");
        args.putString("message", "Are you sure you want to delete this picture?");
        deleteDialog.setArguments(args);
        deleteDialog.show(fm, "fragment_delete_dialog");
    }


    // Delete dialog button clicked (callback)
    public void onDeleteDialogOK(ActionMode am) {
        int currentPage = mViewPager.getCurrentItem();
        File currentFile = this.filelist.get(currentPage);
        String filepath = currentFile.getAbsolutePath();
        String msg = "path="+filepath +(currentFile.exists()?" exists":" NOT exists");
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        this.filelist.remove(currentFile);
        mViewPager.invalidate();
        mSlideshowPagerAdapter.notifyDataSetChanged();
        modified = true;
        // Only actually delete if deletion enabled
        if (AppConstant.ALLOW_DELETE)
            currentFile.delete();
    }

    // button share clicked. Share selected image
    public void btnPhotoShareClicked(View v) {
        int currentPage = mViewPager.getCurrentItem();
        File currentFile = this.filelist.get(currentPage);
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(currentFile));

        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Photo");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "I hope you enjoy this photo");

        startActivity(Intent.createChooser(emailIntent, "Send mail:"));
    }

}