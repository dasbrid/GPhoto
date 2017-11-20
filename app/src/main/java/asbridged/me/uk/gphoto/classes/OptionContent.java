package asbridged.me.uk.gphoto.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        addItem(new OptionItem("Time period", "Choose a time period"));
        addItem(new OptionItem("Recent photos", "Show recent photos"));
        addItem(new OptionItem("Albums", "Show photos by albums"));
        addItem(new OptionItem("Year", "Show photos from a given year"));
        addItem(new OptionItem("Month", "Show photos from a given month"));
        addItem(new OptionItem("From date", "Show photos after given date"));
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
