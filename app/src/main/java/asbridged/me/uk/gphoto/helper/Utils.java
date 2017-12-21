package asbridged.me.uk.gphoto.helper;

import java.io.File;
import java.io.IOException;
import java.util.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.*;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import asbridged.me.uk.gphoto.activities.SettingsActivity;
import asbridged.me.uk.gphoto.classes.Album;
import asbridged.me.uk.gphoto.classes.OptionContent;

/**
 * Created by David on 10/11/2015.
 */
public class Utils {

    private static String TAG = "DAVE:Utils";

    private final static String PREFS_LOCATION = "asbridged.me.uk.gphoto";
    public static  String getLastDisplayed(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_LOCATION,Context.MODE_PRIVATE);

        String ssd = sharedPref.getString("lastDisplayed", "nothing");
        return ssd;
    }

    private static final int MIN_SLIDESHOW_DELAY_SECS = 1;
    private static final int MAX_SLIDESHOW_DELAY_SECS = 10;

    public static boolean getIsShareAllowed(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isShareAllowed = sharedPref.getBoolean(SettingsActivity.KEY_IS_SHARE_ALLOWED, false);
        LogHelper.i(TAG, "getIsShareAllowed=", isShareAllowed);
        return isShareAllowed;
    }

    public static boolean getIsDeleteAllowed(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isDeleteAllowed = sharedPref.getBoolean(SettingsActivity.KEY_IS_DELETE_ALLOWED, false);
        return isDeleteAllowed;
    }

    public static int getSlideshowDelayInSeconds(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_LOCATION,Context.MODE_PRIVATE);
        int ssd;
        int ssdPercent = sharedPref.getInt("slideshowDelayPercent", 50);

        ssd = MIN_SLIDESHOW_DELAY_SECS + (MAX_SLIDESHOW_DELAY_SECS-MIN_SLIDESHOW_DELAY_SECS)*(100-ssdPercent)/100;

        return ssd;
    }

    public static int getSlideshowDelayInPercent(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_LOCATION,Context.MODE_PRIVATE);
        int ssdPercent = sharedPref.getInt("slideshowDelayPercent", 50);

        return ssdPercent;
    }

    public static void setSlideshowDelayPercent(Context context, int ssdPercent)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_LOCATION,Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = sharedPref.edit();
        settingsEditor.putInt("slideshowDelayPercent", ssdPercent);
        settingsEditor.commit();
    }

    public static String getphotoDatePreference(Context context)
    {
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String photoDate = "DateTaken"; //  prefs.getString("photoDate", "DateTaken");
        return photoDate;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private static int calculateInSampleSize_std(File f, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // just do the decode, don't load into memory

        BitmapFactory.decodeFile(f.getAbsolutePath(),options); // decode results into options

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        String imageType = options.outMimeType;

        int inSampleSize = 1;

        if (actualHeight > reqHeight || actualWidth > reqWidth) {

            final int halfHeight = actualHeight / 2;
            final int halfWidth = actualWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static int calculateInSampleSize(File f, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // just do the decode, don't load into memory

        BitmapFactory.decodeFile(f.getAbsolutePath(),options); // decode results into options

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        int numPixelsAllowed = reqHeight * reqWidth;

        int scale = 1;
        while (actualHeight / scale * actualWidth / scale > numPixelsAllowed) {
            scale = scale * 2;
        }
        return scale;
    }


    public static Bitmap decodeFileByScale(File f, int sampleSize) throws IOException
    {
        // Decode bitmap with inSampleSize set
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;

        Bitmap scaledBitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), options);

        ExifInterface exif;

        exif = new ExifInterface(f.getAbsolutePath()); // can throw IOException

        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;
        int rotationInDegrees = exifToDegrees(orientation);
        Matrix matrix = new Matrix();
        if (orientation != 0f) {matrix.preRotate(rotationInDegrees);}
        Bitmap adjustedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,  scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        return adjustedBitmap;
    }

    private static Bitmap getBadImageBitmap(int height, int width) {

        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(14.f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Bad Image", (width / 2.f) , (height / 2.f), paint);

        return b;
    }

    public static void filterBadFiles(ArrayList<File> fileList) {
        if (fileList != null) {
            ListIterator listIterator = fileList.listIterator();
            File file;
            while (listIterator.hasNext()) {
                file = (File) listIterator.next();
                if (!file.exists()) {
                    LogHelper.i(TAG, "filtering bad file:", file.getAbsoluteFile());
                    listIterator.remove();
                }
            }
        }
    }

    public static Bitmap decodeFileToSize(File f, int reqWidth, int reqHeight) {
        // Decode bitmap with inSampleSize set
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        int sampleSize = calculateInSampleSize(f, reqWidth, reqHeight);
        try {
            return decodeFileByScale(f, sampleSize);
        } catch (Exception e) {
            // Some error has occurred ... Send back a static created bitmap
            // This happened on the phone with one 'bad' image
            return getBadImageBitmap(reqWidth,reqHeight);
        }
    }

    public static Bitmap decodeFileToThumbnail(File f) {
        return decodeFileToSize(f,AppConstant.THUMB_SIZE, AppConstant.THUMB_SIZE );
    }

    // Get all the media in specified list of buckets - buckets identified by bucket ID (more reliable if buckets have same names (e.g different subfolders with same name)
    public static ArrayList<File> getMediaInListofBuckets(Context context, ArrayList<String> bucketIDStrings, String orderxxx ) {
        ArrayList<File> files = new ArrayList<File>();
        for (String bucketIDString : bucketIDStrings) {
            long bucketID = Long.parseLong(bucketIDString);
            files.addAll(getMediaInBucketID(context, bucketID, orderxxx));
        }
        return files;
    }

    // Get all the media in specified bucket - identified by  bucket ID (more reliable if buckets have same names (e.g different subfolders with same name)
    public static ArrayList<File> getMediaInBucketID(Context context, long bucketID, String orderxxx )
    {

        String photoDatePreference;
        String storedpref;
        storedpref = Utils.getphotoDatePreference(context);
        if (Utils.getphotoDatePreference(context).equals("DateTaken"))
            photoDatePreference = MediaStore.Images.Media.DATE_TAKEN;
        else
            photoDatePreference = MediaStore.Images.Media.DATE_ADDED;

        // which image properties are we querying
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                photoDatePreference
        };

        // Get the base URI for ...
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // get media in specific bucket
        String [] selectionArgs = new String[1];
        selectionArgs[0] = Long.toString(bucketID);
        String selectionClause = MediaStore.Images.Media.BUCKET_ID + " = ?";

        String BUCKET_ORDER_BY = photoDatePreference + " " + orderxxx;

        ArrayList<File> files = new ArrayList<File>();


        // Make the query.
        Cursor cur = context.getContentResolver().query(
                images,     // URI
                projection, // Which columns to return
                selectionClause,       // WHERE clause  (null = all rows)
                selectionArgs,       // Selection arguments (null = none)
                BUCKET_ORDER_BY        // Ordering
        );


        if (cur.getCount() == 0) {
            cur.close();
            return null;
        }

        try {
            if (cur.moveToFirst()) {
                String bucket;
                String dateTakenString;
                String data;

                Date dateTaken;

                int bucketColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                int dateColumn = cur.getColumnIndex(photoDatePreference);

                int dataColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.DATA);

                do {
                    // Get the field values
                    bucket = cur.getString(bucketColumn);
                    dateTakenString = cur.getString(dateColumn);
                    dateTaken = new Date(Long.parseLong(dateTakenString));

                    data = cur.getString(dataColumn);

                    files.add(new File(data));

                } while (cur.moveToNext());
            }
        } finally {
            cur.close();
        }
        return files;
    }

    // Get list of media BUCKETS on the device
    public static ArrayList<Album> getAlbumsFromMedia(Context context) {
        LogHelper.i(TAG, "getAlbumsFromMedia");
        ArrayList<Album> albums = new ArrayList<>();

        String[] PROJECTION_BUCKET = {
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATA };

        String BUCKET_GROUP_BY = "1) GROUP BY 1,(2"; // this is really WHERE (1) GROUP BY 1,(2)
        String BUCKET_ORDER_BY = "MAX(datetaken) DESC";

        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor cur = context.getContentResolver().query(
                images,
                PROJECTION_BUCKET,
                BUCKET_GROUP_BY,
                null,
                BUCKET_ORDER_BY);

        try {
            if (cur.moveToFirst()) {
                String bucketname;
                String date;
                String data;
                long bucketId;
                int bucketColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                int dateColumn = cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                int dataColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
                int bucketIdColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);

                do {
                    // Get the field values
                    bucketname = cur.getString(bucketColumn);
                    date = cur.getString(dateColumn);
                    data = cur.getString(dataColumn);
                    bucketId = cur.getLong(bucketIdColumn);
                    Log.d(TAG, "bucket name=" + bucketname + " title=" + bucketId);
                    if (bucketname != null && bucketname.length() > 0) {
                        // Do something with the values.
                        File f = new File(data);

                        Album album = new Album(bucketname, bucketId, f, f.getParentFile(), "bucket");
                        albums.add(album);
                    }
                } while (cur.moveToNext());
            }
        } finally {
            cur.close();
        }

        return albums;
    }

    // Get media in a given month (of a given year)
    public static ArrayList<File> getMediaInMonth(Context context, int month, int year, String orderxxx) {
        long minDate;
        long maxDate;
        LogHelper.i(TAG, "getMediaInMonth ", month, "/", year);
        Calendar c = Calendar.getInstance();
        c.clear(); // set all fields (HH:MM:SS) to 0
        c.set(year, month, 1);
        minDate = c.getTimeInMillis();
        c.add(Calendar.MONTH, 1);
        maxDate = c.getTimeInMillis();

        return getMediaInDateRange(context, minDate, maxDate, orderxxx);
    }

    public static ArrayList<File> getPhotosLastYear(Context context, String orderxxx) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR)-1;
        return getMediaInYear(context, year, orderxxx);
    }

    // Get all media in specified year
    // Calculates min and max dates and gets data inbetween
    public static ArrayList<File> getMediaInYear(Context context, int year, String orderxxx) {
        long minDate;
        long maxDate;

        Calendar c = Calendar.getInstance();
        c.set(year, 0, 1, 0, 0, 0);
        minDate = c.getTimeInMillis();
        c.set(year+1, 0, 1, 0, 0, 0);
        maxDate = c.getTimeInMillis();
        return getMediaInDateRange(context, minDate, maxDate, orderxxx);
    }

    // Just get all the media. No dates specified
    public static ArrayList<File> getAllMedia(Context context, String orderxxx) {
        return getMediaInDateRange(context, -1, -1, orderxxx);
    }

    // get last n photos
    public static ArrayList<File> getLastNPhotosinMedia(Context context, int n, String orderxxx) {
        ArrayList<File> mostRecentNfiles = getMediaInDateRange(context, -1, -1, n, "DESC" /* for the Last N photos we ALWAYS want desc order (most recent first)*//*orderxxx*/);
        if (!(orderxxx.equals("DESC"))) {
            Collections.reverse(mostRecentNfiles);
        }
        return mostRecentNfiles;
    }


    // Get all media since a certain tima ago (e.g. one year)
    public static ArrayList<File> getRecentMedia(Context context, String orderxxx) {
        long minDate;

        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        minDate = c.getTimeInMillis();
        return getMediaInDateRange(context, minDate, -1, orderxxx);
    }

    // Get all media in specified year
    // Calculates min and max dates and gets data in between
    public static ArrayList<File> getMediaInCurrentYear(Context context, String orderxxx) {
        long minDate;

        Calendar c = Calendar.getInstance();
        int currentYear;
        currentYear = c.get(Calendar.YEAR);

        c.set(currentYear, 0, 1, 0, 0, 0);
        minDate = c.getTimeInMillis();
        return getMediaInDateRange(context, minDate, -1, orderxxx);
    }

    // Get all media since a certain time ago (e.g. one year)
    public static ArrayList<File> getMediaFromDate(Context context, int day, int month, int year, String orderxxx) {
        long minDate;

        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month, day);
        minDate = c.getTimeInMillis();
        return getMediaInDateRange(context, minDate, -1, orderxxx);
    }

    /**
     * Get all media between specified dates
     * @param context
     * @param day
     * @param month
     * @param year
     * @param today
     * @param tomonth
     * @param toyear
     * @return
     */
    public static ArrayList<File> getMediaBetweenDates(Context context, int day, int month, int year, int today, int tomonth, int toyear, String orderxxx) {
        long minDate;
        long maxDate;

        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month, day);
        minDate = c.getTimeInMillis();
        c.clear();
        c.set(toyear, tomonth, today);
        c.add(Calendar.HOUR, 24);
        maxDate = c.getTimeInMillis();
        return getMediaInDateRange(context, minDate, maxDate, orderxxx);
    }

    // Get Media between two dates (using Query) from description provider
    // Used by other methods
    // If maxdate is -1 then no max date is used (up to present date)
    // If minDate and maxdate are both -1 then no clause is used and we get all media
    public static ArrayList<File> getMediaInDateRange(Context context, long minDate, long maxDate, String orderxxx) {
        return getMediaInDateRange(context, minDate, maxDate, 0, orderxxx);
    }

    // Get Media between two dates and optionally limited to n items (using Query) from description provider
    // If limit = 0 then no limit
    public static ArrayList<File> getMediaInDateRange(Context context, long minDate, long maxDate, int limit, String orderxxx) {
        // which image properties are we querying
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN
        };

        // Get the base URI for ...
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] selectionArgs = null;
        String selectionClause = null;


        String photoDatePreference;
        if (Utils.getphotoDatePreference(context).equals("DateTaken"))
            photoDatePreference = MediaStore.Images.Media.DATE_TAKEN;
        else
            photoDatePreference = MediaStore.Images.Media.DATE_ADDED;

        String orderBy = photoDatePreference + " " + orderxxx;

        if (limit != 0)
        {
            orderBy += " limit " + limit;
        }

        if (minDate != -1 && maxDate != -1) {
            // get media in specified time range
            selectionArgs = new String[2];
            selectionArgs[0] = Long.toString(minDate); // min date
            selectionArgs[1] = Long.toString(maxDate); // max date
            selectionClause = photoDatePreference + ">=? and " + photoDatePreference + "<=?";
        }

        if (minDate != -1 && maxDate == -1) {
            // get media in from specified start time
            selectionArgs = new String[1];
            selectionArgs[0] = Long.toString(minDate); // min date
            selectionClause = photoDatePreference + ">=?";
            Log.d(TAG, "recent files:"+selectionClause);
        }

        // Make the query.
        Cursor cur = context.getContentResolver().query(
                images,     // URI
                projection, // Which columns to return
                selectionClause,       // WHERE clause  (null = all rows)
                selectionArgs,       // Selection arguments (null = none)
                orderBy        // Ordering (plus limit if applicable)
        );

        if (cur.getCount() == 0) {
            cur.close();
            return null;
        }

        ArrayList<File> files = new ArrayList<File>();

        try {

            if (cur.moveToFirst()) {
                String bucket;
                String dateTakenString;
                String data;

                Date dateTaken;
                Date dateAdded;

                int bucketColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                int dateTakenColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.DATE_TAKEN);

                int dataColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.DATA);

                do {
                    // Get the field values
                    bucket = cur.getString(bucketColumn);
                    dateTakenString = cur.getString(dateTakenColumn);
                    dateTaken = new Date(Long.parseLong(dateTakenString));
                    data = cur.getString(dataColumn);

                    files.add(new File(data));

                } while (cur.moveToNext());
            }
        } finally {
            cur.close();
        }
        return files;
    }

    /**
     * Get the list of files to show, based on the parameter bundle
     * @param parameters
     * @return list of files
     */
    public static ArrayList<File> getFilelist(Context context, Bundle parameters, String orderxxx) {

        ArrayList<File> filelist;
        String albumFolder = parameters.getString(SlideshowParametersConstants.folderAbsolutePath);

        String albumType = parameters.getString(SlideshowParametersConstants.albumType);
        int albumMonth = parameters.getInt(SlideshowParametersConstants.month);
        int albumYear = parameters.getInt(SlideshowParametersConstants.year);
        int albumDay = -1;
        if (albumType.equals(SlideshowParametersConstants.AlbumTypes.fromDate) || albumType.equals(SlideshowParametersConstants.AlbumTypes.betweenDates)) {
            albumDay = parameters.getInt(SlideshowParametersConstants.day);
        }

        int albumMonthTo = -1;
        int albumDayTo = -1;
        int albumYearTo = -1;
        if (albumType.equals(SlideshowParametersConstants.AlbumTypes.betweenDates)) {
            albumMonthTo = parameters.getInt(SlideshowParametersConstants.tomonth);
            albumYearTo = parameters.getInt(SlideshowParametersConstants.toyear);
            albumDayTo = parameters.getInt(SlideshowParametersConstants.today);
        }

        long albumBucketID = -1;
        int numPhotos = 0;
        ArrayList<String> bucketIDstrings = new ArrayList<String>();
        if (albumType.equals(SlideshowParametersConstants.AlbumTypes.bucket)) {
            albumBucketID = parameters.getLong(SlideshowParametersConstants.albumBucketID);
        } else if (albumType.equals(SlideshowParametersConstants.AlbumTypes.multipleBuckets)) {
            bucketIDstrings = parameters.getStringArrayList(SlideshowParametersConstants.bucketIDs);
        } else if (albumType.equals(SlideshowParametersConstants.AlbumTypes.lastNPhotos)) {
            numPhotos = parameters.getInt(SlideshowParametersConstants.numPhotos);
        }

        if (albumType.equals(SlideshowParametersConstants.AlbumTypes.lastYear)) {
            filelist = Utils.getPhotosLastYear(context, orderxxx);
        } else if (albumType.equals(SlideshowParametersConstants.AlbumTypes.lastNPhotos)) {
            filelist = Utils.getLastNPhotosinMedia(context, numPhotos, orderxxx);
        } else if (albumType.equals(SlideshowParametersConstants.AlbumTypes.multipleBuckets)) {
            filelist = Utils.getMediaInListofBuckets(context, bucketIDstrings, orderxxx);
        } else if (albumType.equals(SlideshowParametersConstants.AlbumTypes.bucket)) {
            filelist = Utils.getMediaInBucketID(context, albumBucketID, orderxxx);
        } else if (albumType.equals(SlideshowParametersConstants.AlbumTypes.thisYear)) {
            filelist = Utils.getMediaInCurrentYear(context, orderxxx);
        } else if (albumType.equals(SlideshowParametersConstants.AlbumTypes.fromDate)) {
            filelist = Utils.getMediaFromDate(context,albumDay, albumMonth, albumYear, orderxxx);
        } else if (albumType.equals(SlideshowParametersConstants.AlbumTypes.betweenDates)) {
            filelist = Utils.getMediaBetweenDates(context,albumDay, albumMonth, albumYear, albumDayTo, albumMonthTo, albumYearTo, orderxxx );
        } else if (albumType.equals(SlideshowParametersConstants.AlbumTypes.allPhotos)) {
            filelist = Utils.getAllMedia(context, orderxxx);
        }
        else if (albumMonth == -1 && albumYear != -1) {
            // Year but no month ... Get all for this year
            filelist = Utils.getMediaInYear(context, albumYear, orderxxx);
        } else if (albumMonth == -2 && albumYear == -2) {
            // Get RECENT files
            filelist = Utils.getRecentMedia(context, orderxxx);
        } else {
            filelist = Utils.getMediaInMonth(context, albumMonth, albumYear, orderxxx);
        }

        filterBadFiles(filelist);
        return filelist;
    }

}
