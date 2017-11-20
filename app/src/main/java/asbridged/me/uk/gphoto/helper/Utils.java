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
import android.provider.MediaStore;
import android.util.Log;
import asbridged.me.uk.gphoto.classes.Album;

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

    public static void setLastDisplayed(Context context,String ssd)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_LOCATION,Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = sharedPref.edit();
        settingsEditor.putString("lastDisplayed", ssd);
        settingsEditor.commit();
    }


    public static void setImageFilename(Context context,String ssd)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_LOCATION,Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = sharedPref.edit();
        settingsEditor.putString("imageFilename", ssd);
        settingsEditor.commit();
    }

    public static String getImageFilename(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_LOCATION,Context.MODE_PRIVATE);

        String ssd = sharedPref.getString("imageFilename", "none");
        return ssd;
    }

    public static int getSlideshowDelay(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_LOCATION,Context.MODE_PRIVATE);

        int ssd = sharedPref.getInt("slideshowDelay", 3);
        return ssd;
    }

    public static void setSlideshowDelay(Context context, int ssd)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_LOCATION,Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = sharedPref.edit();
        settingsEditor.putInt("slideshowDelay", ssd);
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

    // Get all the media in specified bucket NAME (not reliable if buckets have same names (e.g different subfolders with same name)
    public static ArrayList<File> getMediaInBucket(Context context, String bucketDisplayName )
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
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                photoDatePreference
        };

        // Get the base URI for ...
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] selectionArgs = null;
        String selectionClause = null;

        if (bucketDisplayName != null) {
            // get media in specific bucket
            selectionArgs = new String[1];
            selectionArgs[0] = bucketDisplayName;
            selectionClause = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?";
        }

        String BUCKET_ORDER_BY = photoDatePreference + " DESC"; // newest photo first

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

    // Get all the media in specified list of buckets - buckets identified by bucket ID (more reliable if buckets have same names (e.g different subfolders with same name)
    public static ArrayList<File> getMediaInListofBuckets(Context context, ArrayList<String> bucketIDStringss ) {
        ArrayList<File> files = new ArrayList<File>();
        for (String bucketIDStrings : bucketIDStringss) {
            long bucketID = Long.parseLong(bucketIDStrings);
            files.addAll(getMediaInBucketID(context, bucketID));
        }
        return files;
    }



    // Get all the media in specified bucket - identified by  bucket ID (more reliable if buckets have same names (e.g different subfolders with same name)
    public static ArrayList<File> getMediaInBucketID(Context context, long bucketID )
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

        String BUCKET_ORDER_BY = photoDatePreference + " DESC"; // newest photo first

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

    public static ArrayList<Album> getAlbumsFromMediaGroupedByMonth(Context context) {
        return getAlbumsFromMediaGrouped(context, true);
    }

    public static ArrayList<Album> getAlbumsFromMediaGroupedByYear(Context context) {
        return getAlbumsFromMediaGrouped(context, false);
    }

    // gets ALBUMS (not images) containing all images grouped by Year, and optionally month
    public static ArrayList<Album> getAlbumsFromMediaGrouped(Context context, boolean groupbyMonth) {

        ArrayList<Album> albums = new ArrayList<>();

        String photoDatePreference;
        String storedpref;
        storedpref = Utils.getphotoDatePreference(context);
        if (Utils.getphotoDatePreference(context).equals("DateTaken"))
            photoDatePreference = MediaStore.Images.Media.DATE_TAKEN;
        else
            photoDatePreference = MediaStore.Images.Media.DATE_ADDED;

        String[] PROJECTION_BUCKET = {
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                photoDatePreference,
                MediaStore.Images.Media.DATA };

        String BUCKET_GROUP_BY = null; // no group by


        String BUCKET_ORDER_BY = photoDatePreference + " DESC"; // newest photo first, therefore newest bucket first

        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        int numimages = 0;
        int numimagesWithDate = 0;

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
                int dateColumn = cur.getColumnIndex(photoDatePreference);
                int dataColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
                int bucketIdColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);

                int currentMonth = -1;
                int currentYear = -1;


                do {
                    // Get the field values
                    bucketname = cur.getString(bucketColumn);
                    date = cur.getString(dateColumn);
                    data = cur.getString(dataColumn);
                    bucketId = cur.getInt(bucketIdColumn);

                    numimages++;

                    if (date != null) {
                        long milliseconds = Long.parseLong(date); // since 1/1/1970
                        Calendar cl = Calendar.getInstance();
                        cl.setTimeInMillis(milliseconds);
                        int month = cl.get(Calendar.MONTH);
                        int year = cl.get(Calendar.YEAR);

                        numimagesWithDate++;

                        if (bucketname != null && bucketname.length() > 0) {
                            boolean newGroup = false;
                            if (groupbyMonth == false) {
                                newGroup = (year != currentYear);
                            } else {
                                newGroup = (month != currentMonth || year != currentYear);
                            }
                            if (newGroup) {
                                currentMonth = month;
                                currentYear = year;
                                String albumdate;
                                File f = new File(data);
                                int albumMonth;
                                if (groupbyMonth) {
                                    albumdate = cl.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " + year;
                                    albumMonth = month;
                                } else {
                                    albumdate = Integer.toString(year);
                                    albumMonth = -1;
                                }
                                Album album = new Album(albumdate, year, albumMonth, f, f.getParentFile());
                                albums.add(album);
                            }
                        }
                    }
                } while (cur.moveToNext());
            }
        } finally {
            cur.close();
        }
        return albums;
    }

    // Get media in a given month (of a given year)
    public static ArrayList<File> getMediaInMonth(Context context, int month, int year) {
        long minDate;
        long maxDate;

        Calendar c = Calendar.getInstance();
        c.clear(); // set all fields (HH:MM:SS) to 0
        c.set(year, month, 1);
        minDate = c.getTimeInMillis();
        c.add(Calendar.MONTH, 1);
        maxDate = c.getTimeInMillis();

        return getMediaInDateRange(context, minDate, maxDate);
    }

    public static ArrayList<File> getPhotosLastYear(Context context) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR)-1;
        return getMediaInYear(context, year);
    }

    // Get all media in specified year
    // Calculates min and max dates and gets data inbetween
    public static ArrayList<File> getMediaInYear(Context context, int year) {
        long minDate;
        long maxDate;

        Calendar c = Calendar.getInstance();
        c.set(year, 0, 1, 0, 0, 0);
        minDate = c.getTimeInMillis();
        c.set(year+1, 0, 1, 0, 0, 0);
        maxDate = c.getTimeInMillis();
        return getMediaInDateRange(context, minDate, maxDate);
    }

    // Just get all the media. No dates specified
    public static ArrayList<File> getAllMedia(Context context) {
        return getMediaInDateRange(context, -1, -1);
    }

    // Just get all the media. No dates specified
    public static ArrayList<File> getLastNPhotosinMedia(Context context, int n) {
        return getMediaInDateRange(context, -1, -1, n);
    }


    // Get all media since a certain tima ago (e.g. one year)
    public static ArrayList<File> getRecentMedia(Context context) {
        long minDate;

        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        minDate = c.getTimeInMillis();
        return getMediaInDateRange(context, minDate, -1);
    }

    // Get all media in specified year
    // Calculates min and max dates and gets data in between
    public static ArrayList<File> getMediaInCurrentYear(Context context) {
        long minDate;

        Calendar c = Calendar.getInstance();
        int currentYear;
        currentYear = c.get(Calendar.YEAR);

        c.set(currentYear, 0, 1, 0, 0, 0);
        minDate = c.getTimeInMillis();
        return getMediaInDateRange(context, minDate, -1);
    }

    // Get all media since a certain time ago (e.g. one year)
    public static ArrayList<File> getMediaFromDate(Context context, int day, int month, int year) {
        long minDate;

        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month, day);
        minDate = c.getTimeInMillis();
        return getMediaInDateRange(context, minDate, -1);
    }

    // Get Media between two dates (using Query) from description provider
    // Used by other methods
    // If maxdate is -1 then no max date is used (up to present date)
    // If minDate and maxdate are both -1 then no clause is used and we get all media
    public static ArrayList<File> getMediaInDateRange(Context context, long minDate, long maxDate) {
        return getMediaInDateRange(context, minDate, maxDate, 0);
    }

    // Get Media between two dates and optionally limited to n items (using Query) from description provider
    // If limit = 0 then no limit
    public static ArrayList<File> getMediaInDateRange(Context context, long minDate, long maxDate, int limit) {
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

        String orderBy = photoDatePreference + " desc";

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
            Log.d("DAVE", "recent files:"+selectionClause);
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

/*

    // get all FILES in path (including all files in subfolders)
    // TODO: only return compatible (image) files
    public static ArrayList<File> getAllFiles(String absolutePath)
    {
        ArrayList<File> files = new ArrayList<File>();
        File rootFolder = new File(absolutePath);
        if (!rootFolder.isDirectory())
            return files;

        addFilesToList(files, rootFolder);
        return files;
    }

    // recursion method for getting FILES in a folder
    private static void addFilesToList(ArrayList<File> fileList, File folder) {
        if (folder.isFile())
        {
            if (isImageFile(folder)) {
                fileList.add(folder);
            }
            return;
        }

        File[] listFiles = folder.listFiles();
        if (listFiles == null)
            return;

        for (File file : listFiles) {
            addFilesToList(fileList, file);
        }
    }

    public static ArrayList<Album> getAlbumsFromFolders(String rootPhotosFolder)
    {
        ArrayList<Album> albums = new ArrayList<Album>();
        File rootFolder = new File(rootPhotosFolder);
        addAlbumsToList(albums, rootFolder);
        return albums;
    }

    public static int addAlbumsToList(ArrayList<Album> albumList, File folder) {
        int numfiles = 0;
        File[] listFiles = folder.listFiles();
        if (listFiles == null)
            return numfiles;
        // loop through all files
        for (File file : listFiles) {
            if (file.isFile()) {
                numfiles++;
            }
            if (file.isDirectory()) {
                numfiles=numfiles+addAlbumsToList(albumList, file);
            }
        }
        if (numfiles > 0) {
            File firstfile = getFirstImageInFolder(folder);
            Album a = new Album(folder.getName(), firstfile, folder);
            albumList.add(a);
        }
        return numfiles;
    }

    // TODO: first image may be in a subfolder
    public static File getFirstImageInFolder(File folder)
    {
        if (!folder.isDirectory()) {
            return null;
        }
        File[] listFiles = folder.listFiles();
        // first look for a file in this folder
        for (File file : listFiles) {
            if (file.isFile() && isImageFile(file)) {
                return file;
            }
        }

        File file;
        for (File subfolder : listFiles) {
            if (subfolder.isDirectory()) {
                file = getFirstImageInFolder(subfolder);
                if (file != null)
                return file;
            }
        }
        return null;
    }


    public static boolean isImageFile(File file)
    {
        String filePath = file.getAbsolutePath();
        // Check supported file extensions
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());

        if (AppConstant.FILE_EXTN
                .contains(ext.toLowerCase(Locale.getDefault())))
            return true;
        else
            return false;

    }
     */


}
