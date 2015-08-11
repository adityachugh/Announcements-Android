package io.mindbend.android.announcements;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.Serializable;

import io.mindbend.android.announcements.reusableFrags.PostCommentsFragment;
import io.mindbend.android.announcements.reusableFrags.PostOverlayFragment;
import io.mindbend.android.announcements.reusableFrags.PostsCardsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsFeedAdapter;
import io.mindbend.android.announcements.tabbedFragments.AdminFragment;
import io.mindbend.android.announcements.tabbedFragments.DiscoverFragment;
import io.mindbend.android.announcements.tabbedFragments.YouFragment;
import io.mindbend.android.announcements.tabbedFragments.NotificationsFragment;
import io.mindbend.android.announcements.tabbedFragments.TodayFragment;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class TabbedActivity extends ActionBarActivity implements MaterialTabListener, ViewPager.OnPageChangeListener, Serializable {


    //tab bar
    private transient MaterialTabHost mTabBar;

    //viewpager; what allows the swiping between fragments
    private transient android.support.v4.view.ViewPager mViewPager;
    private PagerAdapter mAdapter;
    //all fragments under TabbedActivity
    private TodayFragment mTodayFragment;
    private AdminFragment mAdminFragment;
    private DiscoverFragment mDiscoverFragment;
    private YouFragment mYouFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        //initialize the viewpager
        mViewPager = (android.support.v4.view.ViewPager) findViewById(R.id.viewpager);
        mAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(4); //this makes sure that all 5 fragments are saved at a time
        mViewPager.setOnPageChangeListener(this);

        //Get linear layout with tabbar and toolbar in order to add elevation (if API 21+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LinearLayout toolbarLayout = (LinearLayout) findViewById(R.id.tab_and_toolbar);
            toolbarLayout.setElevation(8);
        }

        //gets backwards compatible toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //gets tabBar, adds tabs
        mTabBar = (MaterialTabHost) findViewById(R.id.tab_bar);
        mTabBar.addTab(mTabBar.newTab().setText("Today").setTabListener(this));
        mTabBar.addTab(mTabBar.newTab().setText("Discover").setTabListener(this));
        mTabBar.addTab(mTabBar.newTab().setText("You").setTabListener(this));
        //TODO: only add admin tag if user is admin
        mTabBar.addTab(mTabBar.newTab().setText("Admin").setTabListener(this));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mTabBar.setSelectedNavigationItem(position);
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        //gets position of tab selected, only sets nav accent bar to that tab
        int position = materialTab.getPosition();
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_more) {
            Intent i = new Intent(this, MoreActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                if (!mTodayFragment.getmPostsOverlayFragment().isVisible())
                    mTodayFragment.getChildFragmentManager().popBackStack();
                break;
            case 1:
                if(!mDiscoverFragment.getmOrgsGridFrag().isVisible())
                    mDiscoverFragment.getChildFragmentManager().popBackStack();
                break;
            case 2:
                if(!mYouFragment.getmProfileFragment().isVisible())
                    mYouFragment.getChildFragmentManager().popBackStack();
                break;
            case 3:
                //TODO: popBackStack of admin frag
                break;
        }
    }

    private class PagerAdapter extends FragmentPagerAdapter implements Serializable {
        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            //TODO: only return 4 IF *****ADMIN*****, otherwise return 3.
            return 4;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (mTodayFragment == null) mTodayFragment = new TodayFragment();
                    return mTodayFragment;
                case 1:
                    if (mDiscoverFragment == null) mDiscoverFragment = new DiscoverFragment();
                    return mDiscoverFragment;
                case 2:
                    if (mYouFragment == null) mYouFragment = new YouFragment();
                    return mYouFragment;
                case 3:
                    if (mAdminFragment == null) mAdminFragment = new AdminFragment();
                    return mAdminFragment;
            }
            return null;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PostsFeedAdapter.SELECT_PICTURE) {
                Log.wtf("test", "intent result was okay");
                Uri selectedImageUri = data.getData();
                PostsCardsFragment postsCardsFragment = ((PostOverlayFragment) mTodayFragment.getmPostsOverlayFragment()).getmPostsFragment();
                try {
                    Bitmap image = getBitmapFromUri(selectedImageUri);
                    postsCardsFragment.getmPostFeedAdapter().updatePostImageBitmap(image);
                } catch (IOException f){
                    Log.wtf("crash", "sad face");
                }
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
