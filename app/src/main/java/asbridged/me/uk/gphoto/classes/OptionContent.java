package asbridged.me.uk.gphoto.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.detailfragments.AlbumsDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.BetweenDatesDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.FromDateDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.GivenPeriodDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.LastNPhotosDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.MonthDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.OptionDynamicDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.YearDetailFragment;

/**
 * Defines the options for generating slideshows
 * - A title and a description
 */
public class OptionContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<OptionItem> ITEMS = new ArrayList<OptionItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<Integer, OptionItem> ITEM_MAP = new HashMap<Integer, OptionItem>();

    public static final int TIME_PERIOD = 1;
    public static final int RECENT_PHOTOS = 2;
    public static final int ALBUMS = 3;
    public static final int YEAR = 4;
    public static final int MONTH = 5;
    public static final int FROM_DATE = 6;
    public static final int BETWEEN_DATES = 7;

    static {
        // Add some sample items.
        addItem(new OptionItem(TIME_PERIOD, ContextApplication.getContext().getString(R.string.time_period), ContextApplication.getContext().getString(R.string.choose_a_time_period)));
        addItem(new OptionItem(RECENT_PHOTOS, ContextApplication.getContext().getString(R.string.recent_photos), ContextApplication.getContext().getString(R.string.show_recent_photos)));
        addItem(new OptionItem(ALBUMS, ContextApplication.getContext().getString(R.string.albums), ContextApplication.getContext().getString(R.string.show_photos_by_albums)));
        addItem(new OptionItem(YEAR, ContextApplication.getContext().getString(R.string.year), ContextApplication.getContext().getString(R.string.show_photos_from_a_given_year)));
        addItem(new OptionItem(MONTH, ContextApplication.getContext().getString(R.string.month), ContextApplication.getContext().getString(R.string.show_photos_from_a_given_month)));
        addItem(new OptionItem(FROM_DATE, ContextApplication.getContext().getString(R.string.from_date), ContextApplication.getContext().getString(R.string.show_photos_after_given_date)));
        addItem(new OptionItem(BETWEEN_DATES, ContextApplication.getContext().getString(R.string.between_dates), ContextApplication.getContext().getString(R.string.show_photos_between_given_dates)));
    }

    private static void addItem(OptionItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static OptionDynamicDetailFragment getFragmentToStart(int id) {
        OptionDynamicDetailFragment fragment;
        switch (id) {
            case OptionContent.TIME_PERIOD:
                fragment = new GivenPeriodDetailFragment();
                break;
            case OptionContent.RECENT_PHOTOS:
                fragment = new LastNPhotosDetailFragment();
                break;
            case OptionContent.ALBUMS:
                fragment = new AlbumsDetailFragment();
                break;
            case OptionContent.YEAR:
                fragment = new YearDetailFragment();
                break;
            case OptionContent.MONTH:
                fragment = new MonthDetailFragment();
                break;
            case OptionContent.FROM_DATE:
                fragment = new FromDateDetailFragment();
                break;
            case OptionContent.BETWEEN_DATES:
                fragment = new BetweenDatesDetailFragment();
                break;
            default:
                fragment = null;
        }
        return fragment;
    }

    /**
     * An option for selecting pictures representing a piece of description.
     */
    public static class OptionItem {
        public final int id;
        public final String title;
        public final String description;

        public OptionItem(int id, String title, String content) {
            this.id = id;
            this.title = title;
            this.description = content;
        }

        @Override
        public String toString() {
            return Integer.toString(id) + ":" + description;
        }
    }
}
