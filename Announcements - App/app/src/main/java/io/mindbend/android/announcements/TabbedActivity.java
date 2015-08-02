package io.mindbend.android.announcements;

import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import io.mindbend.android.announcements.tabbedFragments.AdminFragment;
import io.mindbend.android.announcements.tabbedFragments.DiscoverFragment;
import io.mindbend.android.announcements.tabbedFragments.MoreFragment;
import io.mindbend.android.announcements.tabbedFragments.NotificationsFragment;
import io.mindbend.android.announcements.tabbedFragments.TodayFragment;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class TabbedActivity extends ActionBarActivity implements MaterialTabListener, ViewPager.OnPageChangeListener {

    //tab bar
    private MaterialTabHost mTabBar;

    //viewpager; what allows the swiping between fragments
    private android.support.v4.view.ViewPager mViewPager;
    private PagerAdapter mAdapter;
    //all fragments under TabbedActivity
    private TodayFragment mTodayFragment;
    private NotificationsFragment mNotificationsFragment;
    private AdminFragment mAdminFragment;
    private DiscoverFragment mDiscoverFragment;
    private MoreFragment mMoreFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        //initialize the viewpager
        mViewPager = (android.support.v4.view.ViewPager) findViewById(R.id.viewpager);
        mAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);

        //Get linear layout with tabbar and toolbar in order to add elevation (if API 21+)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            LinearLayout toolbarLayout = (LinearLayout)findViewById(R.id.tab_and_toolbar);
            toolbarLayout.setElevation(8);
        }

        //gets backwards compatible toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //gets tabBar, adds tabs
        mTabBar = (MaterialTabHost)findViewById(R.id.tab_bar);
        mTabBar.addTab(mTabBar.newTab().setText("Today").setTabListener(this));
        mTabBar.addTab(mTabBar.newTab().setText("Notifications").setTabListener(this));
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
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home){
            switch (mViewPager.getCurrentItem()){
                case 0:
                    if(mTodayFragment.isOnComments){
                        mTodayFragment.returnFromComments();
                    }
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter{
        PagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            //TODO: only return 5 IF *****ADMIN*****, otherwise return 4.
            return 5;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position){
                case 0:
                    if (mTodayFragment == null) mTodayFragment = new TodayFragment();
                    return mTodayFragment;
                case 1:
                    if (mNotificationsFragment == null) mNotificationsFragment = new NotificationsFragment();
                    return mNotificationsFragment;
                case 2:
                    if (mDiscoverFragment == null) mDiscoverFragment = new DiscoverFragment();
                    return mDiscoverFragment;
                case 3:
                    if (mMoreFragment == null) mMoreFragment = new MoreFragment();
                    return mMoreFragment;
                case 4:
                    if (mAdminFragment == null) mAdminFragment = new AdminFragment();
                    return mAdminFragment;
            }
            return null;
        }
    }
}
