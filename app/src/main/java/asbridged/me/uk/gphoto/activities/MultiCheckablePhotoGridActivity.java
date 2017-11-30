package asbridged.me.uk.gphoto.activities;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.*;
import asbridged.me.uk.gphoto.classes.DeleteConfirmDialog;
import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.adapter.MultiCheckablePhotoGridAdapter;
import asbridged.me.uk.gphoto.helper.AppConstant;
import asbridged.me.uk.gphoto.helper.LogHelper;
import asbridged.me.uk.gphoto.helper.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by David on 10/11/2015.
 * An Activity for viewing (and later editing/sharing) all the photos in an album
 */
public class MultiCheckablePhotoGridActivity extends Activity
    implements
            GridView.OnItemClickListener,
             DeleteConfirmDialog.DeleteDialogOKListener
{
    private MultiCheckablePhotoGridAdapter adapter;
    private GridView gridView;
    private String albumAbsolutePath;
    private ArrayList<File> imageFiles;
    private String albumName;
    private int albumMonth;
    private int albumYear;
    private int albumDay;
    private String albumType;
    private long albumBucketID;
    private ArrayList<String> bucketIDstrings;
    private int numPhotos;

    private Bundle parameters;

    private boolean modified;

    private static final String TAG = LogHelper.makeLogTag(MultiCheckablePhotoGridActivity.class);


    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == 100)
        {
            if (resultCode == RESULT_OK) {
                // The activity ended sucessfully
                // The Intent's data Uri identifies which contact was selected.
                modified = resultData.getBooleanExtra("modified", false);
            }
        }
    }

    // Called after starting or when resuming (no saved instance state)
    @Override
    protected void onResume() {
        super.onResume();  // Always call the superclass method first
        LogHelper.i(TAG, "OnResume");
        // N.b. It isn't really necessary to always reload the list onResume()
        // because the list already exists. We could just do this in onCreate()
        // But in that case we would have to take account of the modified flag here
        // - This indicates that a file has been deleted in the slideshow activity
        ArrayList<File> files;
        files = Utils.getFilelist (this, parameters);
        imageFiles.clear();
        imageFiles.addAll(files);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.i(TAG, "onCreate");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionCheck = ContextCompat.checkSelfPermission(MultiCheckablePhotoGridActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // We do not have necessary permission. Must ask the user
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                // No explanation needed, we can request the permission.

                startActivity(new Intent(MultiCheckablePhotoGridActivity.this, GetPermissionsActivity.class));
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_multi_checkable_photo_grid);
        gridView = (GridView) findViewById(R.id.photo_grid_view);

        parameters = getIntent().getExtras();
/*
This is done in onResume
        ArrayList<File> files;
        files = Utils.getFilelist (this, parameters);
        LogHelper.i(TAG, "get ", files.size(), " files");
*/
/*
        String albumFolder = parameters.getString("folderAbsolutePath");
        this.albumType = parameters.getString("albumType");
        this.albumName = parameters.getString("albumName");

        if (albumType.equals("bucket")) {
            this.albumBucketID = parameters.getLong("albumBucketID");
        } else if (albumType.equals("multipleBuckets")) {
            this.bucketIDstrings = parameters.getStringArrayList("bucketIDs");
        } else if (albumType.equals("lastNPhotos")) {
            this.numPhotos = parameters.getInt("numPhotos");
        }

        this.albumMonth = parameters.getInt("month");
        this.albumYear = parameters.getInt("year");
        if (this.albumType.equals("fromDate")) {
            this.albumDay = parameters.getInt("day");
        } else {
            this.albumDay = -1;
        }
        this.albumAbsolutePath = albumFolder;
        modified = false;

//// GIVES NULL REFERENCE        getActionBar().setTitle(albumName);



        Log.d("DAVE", "displaying album for " + albumMonth +"/" + albumYear);

        if (albumType.equals("lastYear")) {
            files = Utils.getPhotosLastYear(this);
        } else if (albumType.equals("lastNPhotos")) {
            files = Utils.getLastNPhotosinMedia(this, numPhotos);
        } else if (albumType.equals("multipleBuckets")) {
            files = Utils.getMediaInListofBuckets(this, bucketIDstrings);
        } else if (albumType.equals("bucket")) {
            files = Utils.getMediaInBucketID(this, albumBucketID);
        } else if (albumType.equals("thisYear")) {
            files = Utils.getMediaInCurrentYear(this);
        } else if (albumType.equals("fromDate")) {
            files = Utils.getMediaFromDate(this,albumDay, albumMonth, albumYear);
        } else if (albumType.equals("allPhotos")) {
            // ALL files
            files = Utils.getAllMedia(this);
        } else if (albumMonth == -1 && albumYear != -1) {
            // Year but no month ... Get all for this year
            files = Utils.getMediaInYear(this, albumYear);
        } else if (albumMonth == -2 && albumYear == -2) {
            // Get RECENT files
            files = Utils.getRecentMedia(this);
        } else {
            // Year and month specified ... get for this month
            files = Utils.getMediaInMonth(this, albumMonth, albumYear);
        }
*/
        imageFiles = new ArrayList<>(); // files;
        // Gridview adapter
        adapter = new MultiCheckablePhotoGridAdapter(MultiCheckablePhotoGridActivity.this, imageFiles);
        // setting grid view adapter
        gridView.setAdapter(adapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new MultiChoiceModeListener());
        gridView.setOnItemClickListener(this);
    }

    // grid item short clicked - launch the slideshow activity
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        File f = imageFiles.get(position);
        Intent intent = new Intent(this, SlideshowActivity.class);

        intent.putExtra("folderAbsolutePath", parameters.getString("folderAbsolutePath"));
        intent.putExtra("albumName",parameters.getString("albumName"));
        intent.putExtra("albumType",parameters.getString("albumType"));
        intent.putExtra("albumBucketID", parameters.getLong("albumBucketID"));
        intent.putStringArrayListExtra("bucketIDs", parameters.getStringArrayList("bucketIDs"));
        intent.putExtra("position", position);
        intent.putExtra("month", parameters.getInt("month"));
        intent.putExtra("year", parameters.getInt("year"));
/*
        intent.putExtra("folderAbsolutePath", this.albumAbsolutePath);
        intent.putExtra("albumName",this.albumName);
        intent.putExtra("albumType",this.albumType);
        intent.putExtra("albumBucketID", albumBucketID);
        intent.putStringArrayListExtra("bucketIDs", this.bucketIDstrings);
        intent.putExtra("position", position);
        intent.putExtra("month", albumMonth);
        intent.putExtra("year", albumYear);
*/
        this.startActivityForResult(intent,100);
    }

    // listener for the long press on the grid
    public class MultiChoiceModeListener implements GridView.MultiChoiceModeListener {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.photo_grid_context_menu, menu);

            mode.setTitle("Select Items");

            setActionModeSubtitle(mode);

            return true;
        }

        private void setActionModeSubtitle(ActionMode mode) {
            int selectCount = gridView.getCheckedItemCount();
            switch (selectCount) {
                case 1:
                    mode.setSubtitle("One item selected");
                    break;
                default:
                    mode.setSubtitle("" + selectCount + " items selected");
                    break;
            }
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_share:
                    shareSelectedPhotos();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.menu_delete:
                    showDeleteDialog(mode);
//                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        // callback from grid view in multi select mode
        // android automatically takes care of setting the item checked (hence changing it's appearance due to the selector)
        // this is handled automatically in the GridView (not in the Adapter so adapter.getNumSelectedItems() will stil equal 0)
        public void onItemCheckedStateChanged(ActionMode mode, int position,
                                              long id, boolean checked) {
            setActionModeSubtitle(mode);
        }
    }

    // share selected on CAB. Share selected images
    public void shareSelectedPhotos() {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> attachmentUris = new ArrayList<Uri>();

        SparseBooleanArray checkedItems = gridView.getCheckedItemPositions();
        int numCheckedItems = checkedItems.size();

        for (int i = 0; i < numCheckedItems ; i++)
        {
            int key = checkedItems.keyAt(i);
            if (checkedItems.get(key)) {
                File file = imageFiles.get(key);
                Uri u = Uri.fromFile(file);
                attachmentUris.add(u);
            }
        }

        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachmentUris);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Photos");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "I hope you enjoy these photos");

        startActivity(Intent.createChooser(emailIntent, "Send mail:"));
    }


    // Delete dialog button clicked (callback)
    public void onDeleteDialogOK(ActionMode am) {
        Toast.makeText(this, "Delete files", Toast.LENGTH_LONG).show();

        SparseBooleanArray checkedItems = gridView.getCheckedItemPositions();
        int numCheckedItems = checkedItems.size();

        for (int i = 0; i < numCheckedItems ; i++)
        {
            int key = checkedItems.keyAt(i);
            if (checkedItems.get(key)) {
                File file = imageFiles.get(key);
                Toast.makeText(this, "Delete "+file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                if (AppConstant.ALLOW_DELETE) {
                    file.delete();
                }
                imageFiles.remove(key);
            }
        }
        adapter.notifyDataSetChanged();
        am.finish();
    }

     // button delete clicked. Delete selected images
    public void showDeleteDialog(ActionMode mode)
    {
        // show confirm dialog. OK will call back to OnDeleteDialogOK
        FragmentManager fm = getFragmentManager();
        DeleteConfirmDialog deleteDialog = new DeleteConfirmDialog();
        Bundle args = new Bundle();
        args.putString("title", "Delete Pictures");
        args.putString("message", "Are you sure you want to delete the selected pictures?");
        deleteDialog.setArguments(args);
        deleteDialog.setActionMode(mode);
        deleteDialog.show(fm, "fragment_delete_dialog");
//        mode.finish();
    }

    // button clicked, launch slideshow for this folder
    public void btnStartSlideshowClicked(View v)
    {
        Log.d("DAVE", "start slideshow for type "+this.albumType+" name = "+this.albumName);
        Intent intent = new Intent(this, SlideshowActivity.class);
        intent.putExtra("folderAbsolutePath", this.albumAbsolutePath);
        intent.putExtra("albumType", this.albumType);
        intent.putExtra("albumName", this.albumName);
        intent.putExtra("albumBucketID", this.albumBucketID);
        intent.putStringArrayListExtra("bucketIDs", this.bucketIDstrings);
        intent.putExtra("numPhotos", this.numPhotos);
        intent.putExtra("position", -1);
        intent.putExtra("month", this.albumMonth);
        intent.putExtra("year", this.albumYear);
        intent.putExtra("day", this.albumDay);
        this.startActivityForResult(intent,100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_grid_standard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_play_slideshow:
                Log.d("DAVE", "start slideshow for type "+this.albumType+" name = "+this.albumName);
                Intent intent = new Intent(this, SlideshowActivity.class);
                intent.putExtra("folderAbsolutePath", this.albumAbsolutePath);
                intent.putExtra("albumType", this.albumType);
                intent.putExtra("albumName", this.albumName);
                intent.putExtra("albumBucketID", this.albumBucketID);
                intent.putStringArrayListExtra("bucketIDs", this.bucketIDstrings);
                intent.putExtra("numPhotos", this.numPhotos);
                intent.putExtra("position", -1);
                intent.putExtra("month", this.albumMonth);
                intent.putExtra("year", this.albumYear);
                intent.putExtra("day", this.albumDay);
                this.startActivityForResult(intent,100);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}