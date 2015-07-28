package io.mindbend.android.announcements;

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

    private MaterialTabHost mTabBar;

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
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        //gets position of tab selected, only sets nav accent bar to that tab
        int position = materialTab.getPosition();
        mTabBar.setSelectedNavigationItem(position);

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
