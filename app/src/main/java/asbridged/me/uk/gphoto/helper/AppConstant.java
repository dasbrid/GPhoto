package asbridged.me.uk.gphoto.helper;

import java.util.Arrays;
import java.util.List;

/**
 * Created by David on 10/11/2015.
 */
public class AppConstant {

    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 3;

    // Gridview image padding
    public static final int GRID_PADDING = 8; // in dp

    // SD card image directory
    public static final String PHOTO_ALBUM = "Photos";

    // supported file formats
    public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg", "png");

    // size (pixels) of images used for thumbnails
    // NOT the size on the screen (specified in gui constants)
    public static int THUMB_SIZE= 240;

    public static int SLIDESHOW_IMAGE_WIDTH = 1280;
    public static int SLIDESHOW_IMAGE_HEIGHT = 800;

    public static final boolean ALLOW_DELETE = true;
    public static final boolean ALLOW_VIEW_PHOTOS = true;

    public static final int LAST_N_PHOTOS_MIN = 4;
    public static final int LAST_N_PHOTOS_MAX = 100;


}
