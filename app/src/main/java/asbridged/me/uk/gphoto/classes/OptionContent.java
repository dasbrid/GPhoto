package asbridged.me.uk.gphoto.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asbridged.me.uk.gphoto.R;

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
    public static final Map<String, OptionItem> ITEM_MAP = new HashMap<String, OptionItem>();

    static {
        // Add some sample items.
        addItem(new OptionItem(ContextApplication.getContext().getString(R.string.time_period), ContextApplication.getContext().getString(R.string.choose_a_time_period)));
        addItem(new OptionItem(ContextApplication.getContext().getString(R.string.recent_photos), ContextApplication.getContext().getString(R.string.show_recent_photos)));
        addItem(new OptionItem(ContextApplication.getContext().getString(R.string.albums), ContextApplication.getContext().getString(R.string.show_photos_by_albums)));
        addItem(new OptionItem(ContextApplication.getContext().getString(R.string.year), ContextApplication.getContext().getString(R.string.show_photos_from_a_given_year)));
        addItem(new OptionItem(ContextApplication.getContext().getString(R.string.month), ContextApplication.getContext().getString(R.string.show_photos_from_a_given_month)));
        addItem(new OptionItem(ContextApplication.getContext().getString(R.string.from_date), ContextApplication.getContext().getString(R.string.show_photos_after_given_date)));
        addItem(new OptionItem(ContextApplication.getContext().getString(R.string.between_dates), ContextApplication.getContext().getString(R.string.show_photos_between_given_dates)));
    }

    private static void addItem(OptionItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.title, item);
    }

    /**
     * An option for selecting pictures representing a piece of description.
     */
    public static class OptionItem {
        public final String title;
        public final String description;

        public OptionItem(String title, String content) {
            this.title = title;
            this.description = content;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
