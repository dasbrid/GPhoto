package asbridged.me.uk.gphoto.detailfragments;

import android.app.Activity;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.activities.OptionDynamicDetailActivity;
import asbridged.me.uk.gphoto.activities.OptionDynamicListActivity;
import asbridged.me.uk.gphoto.classes.OptionContent;
import asbridged.me.uk.gphoto.helper.LogHelper;

/**
 * A fragment representing a single Option detail screen.
 * This fragment is either contained in a {@link OptionDynamicListActivity}
 * in two-pane mode (on tablets) or a {@link OptionDynamicDetailActivity}
 * on handsets.
 */
public abstract class OptionDynamicDetailFragment extends Fragment implements android.view.View.OnClickListener, IButtonActions {
    private static final String TAG = LogHelper.makeLogTag(OptionDynamicDetailFragment.class);
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy description this fragment is presenting.
     */
    private OptionContent.OptionItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OptionDynamicDetailFragment() {
    }


    public void onClick(View view) {
        LogHelper.i(TAG, "build version ", Build.VERSION.SDK_INT);

        switch(view.getId()){
            //here you need the title of the button i.e. its title in the xml file
            case R.id.btnShowSlideshow:
                doSlideshow(false);
                break;
            case R.id.btnShowPictures:
                viewAlbum();
                break;
            case R.id.btnShowPicturesShuffled:
                doSlideshow(true);
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            LogHelper.i(TAG, getArguments().getString(ARG_ITEM_ID));
            // Load the dummy description specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load description from a description provider.
            mItem = OptionContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.title  );
            }
        }
    }
}
