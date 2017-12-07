package asbridged.me.uk.gphoto.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import asbridged.me.uk.gphoto.R;

import asbridged.me.uk.gphoto.classes.OptionContent;
import asbridged.me.uk.gphoto.detailfragments.AlbumsDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.BetweenDatesDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.FromDateDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.GivenPeriodDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.LastNPhotosDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.MonthDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.OptionDynamicDetailFragment;
import asbridged.me.uk.gphoto.detailfragments.YearDetailFragment;
import asbridged.me.uk.gphoto.helper.LogHelper;

import java.util.List;

/**
 * An activity representing a list of Options. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link OptionDynamicDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class OptionDynamicListActivity extends AppCompatActivity {
    private static final String TAG = LogHelper.makeLogTag(OptionDynamicListActivity.class);
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_option_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.option_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.option_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        ((RecyclerView)recyclerView).addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, OptionContent.ITEMS, mTwoPane));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final OptionDynamicListActivity mParentActivity;
        private final List<OptionContent.OptionItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OptionContent.OptionItem item = (OptionContent.OptionItem) view.getTag();
                LogHelper.i(TAG, "clicked item", item.id, ", title=",item.title);
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putInt(OptionDynamicDetailFragment.ARG_ITEM_ID, item.id);

                    OptionDynamicDetailFragment fragment;
                    fragment = OptionContent.getFragmentToStart(item.id);

                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.option_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, OptionDynamicDetailActivity.class);
                    intent.putExtra(OptionDynamicDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }
            }
        };

/*
        private OptionDynamicDetailFragment getFragmentToStart(int id) {
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

*/
        SimpleItemRecyclerViewAdapter(OptionDynamicListActivity parent,
                                      List<OptionContent.OptionItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dynamic_option_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).title);
            holder.mContentView.setText(mValues.get(position).description);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}
