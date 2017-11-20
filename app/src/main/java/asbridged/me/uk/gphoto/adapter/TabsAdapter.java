package asbridged.me.uk.gphoto.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import asbridged.me.uk.gphoto.tabs.BucketListFragment;
import asbridged.me.uk.gphoto.tabs.FromDateFragment;
import asbridged.me.uk.gphoto.tabs.GivenMonthFragment;
import asbridged.me.uk.gphoto.tabs.GivenPeriodFragment;
import asbridged.me.uk.gphoto.tabs.GivenYearFragment;
import asbridged.me.uk.gphoto.tabs.LastNPhotosFragment;
import asbridged.me.uk.gphoto.tabs.TabFragment;

/**
 * Created by AsbridgeD on 14-Nov-17.
 */

public class TabsAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    TabFragment currentFragment;
    public TabsAdapter(FragmentManager fm) {
        super(fm);
        this.mNumOfTabs = 6;
    }

    public TabFragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {

        if (currentFragment != object) {
            currentFragment = (TabFragment) object;
        }
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                GivenPeriodFragment tab0 = new GivenPeriodFragment();
                return tab0;
            case 1:
                LastNPhotosFragment tab1 = new LastNPhotosFragment();
                return tab1;
            case 2:
                BucketListFragment tab2 = new BucketListFragment();
                return tab2;
            case 3:
                GivenYearFragment tab3 = new GivenYearFragment();
                return tab3;
            case 4:
                GivenMonthFragment tab4 = new GivenMonthFragment();
                return tab4;
            case 5:
                FromDateFragment tab5 = new FromDateFragment();
                return tab5;

            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page "+position;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
