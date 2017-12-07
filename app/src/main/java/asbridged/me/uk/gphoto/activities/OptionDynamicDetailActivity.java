package asbridged.me.uk.gphoto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.detailfragments.AlbumsDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.BetweenDatesDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.FromDateDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.GivenPeriodDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.LastNPhotosDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.MonthDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.OptionDynamicDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.YearDetailFragment;
import asbridged.me.uk.gphoto.helper.LogHelper;

/**
 * An activity representing a single Option detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link OptionDynamicListActivity}.
 */
public class OptionDynamicDetailActivity extends AppCompatActivity {
    private static final String TAG = LogHelper.makeLogTag(OptionDynamicDetailActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_option_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            int item_id = getIntent().getIntExtra(OptionDynamicDetailFragment.ARG_ITEM_ID, -1);
            LogHelper.i(TAG, "making bundle, item_id=", item_id);
            arguments.putInt(OptionDynamicDetailFragment.ARG_ITEM_ID, item_id);

            OptionDynamicDetailFragment fragment;
            fragment = getFragmentToStart(item_id);
            // OptionDynamicDetailFragment fragment = new OptionDynamicDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.option_detail_container, fragment)
                    .commit();
        }
    }

    private OptionDynamicDetailFragment getFragmentToStart(int id) {
        OptionDynamicDetailFragment fragment;
        switch (id) {
            case 1: //"Time period":
                fragment = new GivenPeriodDetailFragment();
                break;
            case 2: //"Recent photos":
                fragment = new LastNPhotosDetailFragment();
                break;
            case 3: //"Albums":
                fragment = new AlbumsDetailFragment();
                break;
            case 4: //"Year":
                fragment = new YearDetailFragment();
                break;
            case 5: //"Month":
                fragment = new MonthDetailFragment();
                break;
            case 6: //"From date":
                fragment = new FromDateDetailFragment();
                break;
            case 7: //"Between dates":
                fragment = new BetweenDatesDetailFragment();
                break;
            default:
                fragment = null;
        }
        return fragment;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, OptionDynamicListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
