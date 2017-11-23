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
import android.view.ActionMode;
import android.view.View;
import android.widget.*;
import asbridged.me.uk.gphoto.classes.DeleteConfirmDialog;
import asbridged.me.uk.gphoto.classes.SlideshowViewPager;
import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.adapter.SlideshowPagerAdapter;
import asbridged.me.uk.gphoto.helper.AppConstant;
import asbridged.me.uk.gphoto.helper.LogHelper;
import asbridged.me.uk.gphoto.helper.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by David on 04/11/2015.
 * See http://developer.android.com/training/animation/screen-slide.html
 */
public class SlideshowActivity extends Activity
        implements
        SlideshowViewPager.OnTouchedListener,
        DeleteConfirmDialog.DeleteDialogOKListener
{

    private static final String TAG = LogHelper.makeLogTag(SlideshowActivity.class);

    private final int MIN_SLIDESHOW_DELAY_SECS = 1;
    private final int MAX_SLIDESHOW_DELAY_SECS = 10;

    private int numPages;
    private ArrayList<File> filelist;
    // Flag used when automatically going to the next image
    // If slideshowOn is false then we stay on the current image
    // this is saved in onSaveInstanceState and read in OnResume
    private boolean slideshowOn;


    // Flag used when automatically going to the next image
    // Choose next image randomly or sequentially
    // this is saved in onSaveInstanceState and read in OnResume
    private boolean shuffleOn;


    private boolean shuffleSharedState;
    private boolean slideshowSharedState;

    // Flag is set when a photo has been deleted
    // This is returned to the calling activity (in returnIntent) when "onBack" is called
    private boolean modified;

    // The time (in seconds) for which the controls are visible after screen interaction.
    private static int SHOW_CONTROLS_TIME_SECS = 3;


    private int page = 0;

    private ImageButton btnPhotoShare;
    private ImageButton btnPhotoDelete;
    private ImageButton btnStartSlideshow;
    private Button btnSlideshowIncreaseSpeed;
    private Button btnSlideshowDecreaseSpeed;

    private SlideshowPagerAdapter mSlideshowPagerAdapter;
    private SlideshowViewPager mViewPager;

    private Handler handler = new Handler();

    private Runnable nextpageRunnable = new Runnable() {
        @Override
        public void run() {
            if (slideshowOn == false)
                return;
            LogHelper.i(TAG, "in nextPageRunnnable, page=",page,"getCurrentItem()=",mViewPager.getCurrentItem());
            if (shuffleOn && filelist.size() > 1) {
                int newpage = page;
                Random r = new Random();
                while (newpage == page) {
                    newpage = r.nextInt(filelist.size());
                }
                LogHelper.i(TAG, "page=",page,"...chosen random page ",newpage);
                page = newpage;
            } else {
                LogHelper.i(TAG, "page=",page,"...choosing next page");
                page++;
                if (page >= numPages)
                    page = 0;
            }

            // Utils.setLastDisplayed(getApplicationContext(),Integer.toString(page));
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

    // button clicked - restart the slideshow
    public void btnPhotoStartSlideshowClicked(View v) {
        btnStartSlideshow.setVisibility(View.INVISIBLE);
        btnPhotoDelete.setVisibility(View.INVISIBLE);
        btnPhotoShare.setVisibility(View.INVISIBLE);
        btnSlideshowDecreaseSpeed.setVisibility(View.INVISIBLE);
        btnSlideshowIncreaseSpeed.setVisibility(View.INVISIBLE);
        page = mViewPager.getCurrentItem();
        startSlideshow();
    }

    /**
     * Called from the pager when touched
     * This stops the slideshow (if playing) on the current slide
     * and displays control buttons and android navigation bar for n seconds
     */
    public void onTouched() {
        LogHelper.i(TAG, "onTouched");
        slideshowOn = false;
        showControlsAndNavigationForNSeconds();
    }

    private void startSlideshow() {
        slideshowOn = true;
        handler.postDelayed(nextpageRunnable, Utils.getSlideshowDelay(this));
    }

    /**
     * We save the following properties, so that when the activity is recreated we continue
     * in the same state:
     * slideshowOn (are we playing slides or paused, viewing a single image)
     * shuffleOn (playing randomly or sequentially?)
     * currentPage (the current photo being viewed
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);        // Save the slideshow status
        LogHelper.i(TAG,"onSaveInstanceState");
        LogHelper.i(TAG, "Saving values :slideshowOn=", slideshowOn,"; shuffleOn=",shuffleOn,"; currentPage=",mViewPager.getCurrentItem());
        savedInstanceState.putBoolean("slideshowOn", slideshowOn);
        savedInstanceState.putBoolean("shuffleOn", shuffleOn);
        savedInstanceState.putInt("currentPage", mViewPager.getCurrentItem());
    }

    /** Override of onStop for the activity
     * We make a copy of the current slideshow state, and then turn the slideshow off
     * Not sure why we turn it off. Maybe because the runnable will still trigger after the activity is stopped????????
     */
    @Override
    public void onStop() {
        super.onStop();
        LogHelper.i(TAG, "onStop");
        slideshowSharedState= slideshowOn;
        slideshowOn = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.i(TAG,"onCreate");

        // Check we have necessary permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionCheck = ContextCompat.checkSelfPermission(SlideshowActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // We do not have necessary permission. Start activity to ask the user
                startActivity(new Intent(SlideshowActivity.this, GetPermissionsActivity.class));
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_slideshow);
        btnStartSlideshow = (ImageButton) findViewById(R.id.imgbtnPhotoStartSlideshow);
        btnPhotoDelete = (ImageButton) findViewById(R.id.imgbtnPhotoDelete);
        btnPhotoShare = (ImageButton) findViewById(R.id.imgbtnPhotoShare);
        btnSlideshowIncreaseSpeed = (Button) findViewById(R.id.btnSlideShowIncreaseSpeed);
        btnSlideshowDecreaseSpeed = (Button) findViewById(R.id.btnSlideShowDecreaseSpeed);

        mSlideshowPagerAdapter = new SlideshowPagerAdapter(this);
        mViewPager = (SlideshowViewPager) findViewById(R.id.slideshowpager);
        mViewPager.setAdapter(mSlideshowPagerAdapter);

        mViewPager.setOnTouchedListener(this);

        // if we are starting for first time (not restarting)
        if (savedInstanceState == null) {
            LogHelper.i(TAG,"Saved instance state == null");
            slideshowSharedState = true;
            slideshowOn = true;
            //shuffleSharedState = true;
        }

        Bundle parameters = getIntent().getExtras();

        // playInRandomOrder is set by the fragment (currently only lastNFragment)
        // to signal that we should play in random order, not sequential
        boolean playInRandomOrder = parameters.getBoolean("playInRandomOrder");
        shuffleSharedState = playInRandomOrder; //// NOT SURE

        // Position is ONLY used when coming from the Album grid activity, so say "Show this specific photo"
        Integer positionParameter = parameters.getInt("position");
        if (positionParameter != -1)
        {
            // we have been passed a specific photo index. Show the photo and turn the slideshow off
            page = positionParameter;
            slideshowSharedState = false;
        } else {
            page = 0;
            slideshowSharedState = true;
        }

        LogHelper.i(TAG, "Values from activity PARAMETERS: shuffled=",playInRandomOrder,"; position=",positionParameter);

        filelist = getFilelist (parameters);

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

    /**
     * Load the slideshow state from the saved instance state.
     * So that the activity starts in the same state as we left it
     * @param savedInstanceState
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        LogHelper.i(TAG, "onRestoreInstanceState");
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        slideshowSharedState = savedInstanceState.getBoolean("slideshowOn");
        shuffleSharedState = savedInstanceState.getBoolean("shuffleOn");
        page = savedInstanceState.getInt("currentPage");
        LogHelper.i(TAG, "Values from savedInstanceState:slideshowOn=", slideshowSharedState,"; shuffleOn=",shuffleSharedState,"; currentPage=",page);
    }

    /**
     * The only special thing here is that we pass the information
     * back to say wheter a photo has been deleted
     */
    @Override
    public void onBackPressed() {
        LogHelper.i(TAG, "onBackPressed");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("modified",modified);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    /**
     * Called after starting or when resuming (no saved instance state)
     *
     */
    @Override
    protected void onResume() {
        super.onResume();  // Always call the superclass method first
        LogHelper.i(TAG, "onResume");
        // get the saved (in memory state of the slideshow)
        slideshowOn = slideshowSharedState;
        shuffleOn = shuffleSharedState;
        LogHelper.i(TAG, "slideshowOn=", slideshowOn, ";shuffleOn=", shuffleOn, ";page=",page);
        mViewPager.setCurrentItem(page);

        if (slideshowOn) {
            startSlideshow();
        }
        hideControlsAndNavigation();
        // You should never show the action bar (application bar) if the
        // status bar is hidden, so hide that too if necessary.
//// GIVES NULL REFERENCE       ActionBar actionBar = getActionBar();
//// GIVES NULL REFERENCE EXCEPTION       actionBar.hide();
    }

    /**
     * Show the play, delete and share controls, togoether with the android nagigation
     * A runable will be called after n seconds to clear the controls
     */
    private void showControlsAndNavigationForNSeconds() {

        btnStartSlideshow.setVisibility(View.VISIBLE);
        btnPhotoDelete.setVisibility(View.VISIBLE);
        btnPhotoShare.setVisibility(View.VISIBLE);
        int ssd = Utils.getSlideshowDelay(this);
        // N.b. decrease speed (slower) means increase delay, and vice versa
        if (ssd < MAX_SLIDESHOW_DELAY_SECS) {
            btnSlideshowDecreaseSpeed.setVisibility(View.VISIBLE);
        }
        if (ssd > MIN_SLIDESHOW_DELAY_SECS) {
            btnSlideshowIncreaseSpeed.setVisibility(View.VISIBLE);
        }

        View decorView = getWindow().getDecorView();
        int uiOptions =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_FULLSCREEN  // hide staus bar (we want to hide the status bar)
                // |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION  // DON'T hide navigation
                // |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                ;

        decorView.setSystemUiVisibility(uiOptions);

        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          hideControlsAndNavigation();
                                      }
                                  },
                SHOW_CONTROLS_TIME_SECS*1000);
    }

    /**
     * Go back to imersive mode, with control buttons hidden
     * This is called by the runnable after the contrills have been shown for n seconds
     */
    private void hideControlsAndNavigation() {
        btnStartSlideshow.setVisibility(View.INVISIBLE);
        btnPhotoDelete.setVisibility(View.INVISIBLE);
        btnPhotoShare.setVisibility(View.INVISIBLE);
        btnSlideshowIncreaseSpeed.setVisibility(View.INVISIBLE);
        btnSlideshowDecreaseSpeed.setVisibility(View.INVISIBLE);


        // Code to make layout fullscreen. In onResume, otherwise when activity comes back it will revert to non-fullscreen
        // http://developer.android.com/training/system-ui/status.html
        // hide the status bar and application bar (top of the screen) with SYSTEM_UI_FLAG_FULLSCREEN
        // hide the navigation bar (back, home at bottom of screen) with SYSTEM_UI_FLAG_HIDE_NAVIGATION
        // SYSTEM_UI_FLAG_LAYOUT_STABLE puts the navigation controls on top of the activity layout (stays fullscreen)
        int uiOptions =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | // hide navigation
                        View.SYSTEM_UI_FLAG_FULLSCREEN | // hide staus bar
                        View.SYSTEM_UI_FLAG_IMMERSIVE;
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
    }



    /**
     * Get the list of files to show, based on the parameter bundle
     * @param parameters
     * @return list of files
     */
    private ArrayList<File>  getFilelist(Bundle parameters) {

        ArrayList<File> filelist;
        String albumFolder = parameters.getString("folderAbsolutePath");

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
        return filelist;
    }

    /**
     * Increase the speed of the slideshow
     * This means we DEcrease the delay by a second (if greater than minimum delay)
     * @param v
     */
    public void btnSlideShowIncreaseSpeedClicked(View v) {
        int ssd = Utils.getSlideshowDelay(getApplicationContext());
        if (ssd > MIN_SLIDESHOW_DELAY_SECS) {
            Utils.setSlideshowDelay(getApplicationContext(), ssd - 1);
        }
    }

    /**
     * Decrease the speed of the slideshow
     * This means we INcrease the delay by a second (if less than maximum delay)
     * @param v
     */
    public void btnSlideShowDecreaseSpeedClicked(View v) {
        int ssd = Utils.getSlideshowDelay(getApplicationContext());
        if (ssd < MAX_SLIDESHOW_DELAY_SECS) {
            Utils.setSlideshowDelay(getApplicationContext(), ssd + 1);
        }
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