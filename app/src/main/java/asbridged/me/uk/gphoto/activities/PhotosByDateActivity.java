package asbridged.me.uk.gphoto.activities;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.adapter.TabsAdapter;
import asbridged.me.uk.gphoto.controls.PageIndicator;
import asbridged.me.uk.gphoto.helper.AppConstant;
import asbridged.me.uk.gphoto.tabs.TabFragment;

public class PhotosByDateActivity extends FragmentActivity {

    private static final String TAG = "PhotosByDateActivity";

    private TabsAdapter tabsAdapter;
    private PageIndicator pageIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_by_date);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pagertabs);
        //http://www.androidbegin.com/tutorial/android-viewpagertabstrip-fragments-tutorial/

        Button btnViewPhotos = (Button) findViewById(R.id.btnShowAlbum);
        if (AppConstant.ALLOW_VIEW_PHOTOS == false)
            btnViewPhotos.setVisibility(View.INVISIBLE);
        tabsAdapter = new TabsAdapter(getSupportFragmentManager());

        viewPager.setAdapter(tabsAdapter);
        //https://github.com/codepath/android_guides/wiki/Google-Play-Style-Tabs-using-TabLayout
        //https://guides.codepath.com/android/google-play-style-tabs-using-tablayout
        // Give the TabLayout the ViewPager
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrollStateChanged(int position) {}
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            @Override
            public void onPageSelected(int position) {
                pageIndicator.changePage(position);
            }

        });
        pageIndicator = (PageIndicator) findViewById(R.id.pageindicator);
        pageIndicator.setNumButtons(6);
    }

    public void btnShowSlideshowClicked(View v) {
        TabFragment currentFragment = tabsAdapter.getCurrentFragment();
        currentFragment.doSlideshow();
    }

    public void btnShowOptionsClicked(View v) {
        Intent intent = new Intent(this, OptionDynamicListActivity.class);
        this.startActivity(intent);
    }

    public void btnShowAlbumClicked(View v) {
        TabFragment currentFragment = tabsAdapter.getCurrentFragment();
        currentFragment.viewAlbum();
    }

}
