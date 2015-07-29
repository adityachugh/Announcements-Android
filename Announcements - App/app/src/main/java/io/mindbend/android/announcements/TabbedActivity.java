package io.mindbend.android.announcements;

import android.app.Fragment;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class TabbedActivity extends ActionBarActivity implements MaterialTabListener {

    //tab bar
    private MaterialTabHost mTabBar;

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
        mTabBar.addTab(mTabBar.newTab().setText("Admin").setTabListener(this));
        mTabBar.addTab(mTabBar.newTab().setText("Discover").setTabListener(this));
        mTabBar.addTab(mTabBar.newTab().setText("More").setTabListener(this));

        //creates todayFragment by default (first screen)
        mTodayFragment = (TodayFragment)getFragmentManager().findFragmentById(R.id.fragment_container);
        if (mTodayFragment == null){
            mTodayFragment = new TodayFragment();

            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mTodayFragment)
                    .commit();
        }
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        //gets position of tab selected, only sets nav accent bar to that tab
        int position = materialTab.getPosition();
        mTabBar.setSelectedNavigationItem(position);

        Fragment fragment = null;
        //Creates fragments if they do not exist when tab is selected
        //changes arbitrary 'fragment' to fragment associated with selected tab
        switch (position){
            //Today
            case 0:
                //no need to instantiate today fragment, created on default when user enters tabbed activity
                fragment = mTodayFragment;
                break;
            //Notifications
            case 1:
                if (mNotificationsFragment == null){
                    mNotificationsFragment = new NotificationsFragment();
                }
                fragment = mNotificationsFragment;
                break;
            //Admin
            case 2:
                if (mAdminFragment == null){
                    mAdminFragment = new AdminFragment();
                }
                fragment = mAdminFragment;
                break;
            //Discover
            case 3:
                if (mDiscoverFragment == null){
                    mDiscoverFragment = new DiscoverFragment();
                }
                fragment = mDiscoverFragment;
                break;
            //More
            case 4:
                if (mMoreFragment == null){
                    mMoreFragment = new MoreFragment();
                }
                fragment = mMoreFragment;
                break;
        }

        //Changes fragment based on selected tab
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

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

        return super.onOptionsItemSelected(item);
    }
}
