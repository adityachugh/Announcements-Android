package io.mindbend.android.announcements;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.Serializable;

import io.mindbend.android.announcements.adminClasses.AdminMainFragment;
import io.mindbend.android.announcements.adminClasses.ModifyOrganizationFragment;
import io.mindbend.android.announcements.adminClasses.NewAnnouncementFragment;
import io.mindbend.android.announcements.reusableFrags.PostCommentsFragment;
import io.mindbend.android.announcements.reusableFrags.PostOverlayFragment;
import io.mindbend.android.announcements.reusableFrags.PostsCardsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsFeedAdapter;
import io.mindbend.android.announcements.reusableFrags.ProfileFragment;
import io.mindbend.android.announcements.tabbedFragments.AdminFragment;
import io.mindbend.android.announcements.tabbedFragments.DiscoverFragment;
import io.mindbend.android.announcements.tabbedFragments.YouFragment;
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

    private Bundle mSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        //to fix the "rotate & back button" crashing bug
        mSavedInstanceState = savedInstanceState;

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
        toolbar.setTitle(getString(R.string.format_tabbed_activity_toolbar_text, "Akshay")); //TODO: pull in user's name
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
                if(!mAdminFragment.getmAdminMain().isVisible())
                mAdminFragment.getChildFragmentManager().popBackStack();
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
                    if (mSavedInstanceState != null)
                        mTodayFragment = (TodayFragment)getSupportFragmentManager().findFragmentById(R.id.today_frag);
                    else if (mTodayFragment == null) mTodayFragment = new TodayFragment();
                    return mTodayFragment;
                case 1:
                    if (mSavedInstanceState != null)
                        mDiscoverFragment = (DiscoverFragment)getSupportFragmentManager().findFragmentById(R.id.discove_frag);
                    else if (mDiscoverFragment == null) mDiscoverFragment = new DiscoverFragment();
                    return mDiscoverFragment;
                case 2:
                    if (mSavedInstanceState != null)
                        mYouFragment = (YouFragment)getSupportFragmentManager().findFragmentById(R.id.you_frag);
                    else if (mYouFragment == null) mYouFragment = new YouFragment();
                    return mYouFragment;
                case 3:
                    if (mSavedInstanceState != null)
                        mAdminFragment = (AdminFragment)getSupportFragmentManager().findFragmentById(R.id.admin_frag);
                    else if (mAdminFragment == null) mAdminFragment = new AdminFragment();
                    return mAdminFragment;
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment)super.instantiateItem(container, position);
            switch (position){
                case 0:
                    mTodayFragment = (TodayFragment)fragment;
                    break;
                case 1:
                    mDiscoverFragment = (DiscoverFragment)fragment;
                    break;
                case 2:
                    mYouFragment = (YouFragment)fragment;
                    break;
                case 3:
                    mAdminFragment = (AdminFragment)fragment;
                    break;
            }

            return fragment;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == NewAnnouncementFragment.ADD_PHOTO) {
                Log.wtf("Image", "intent result was okay");
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap image = getBitmapFromUri(selectedImageUri);
                    Log.wtf("Image", "Bitmap is: " + image.toString());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    byte[] imageBytes = stream.toByteArray();
                    Log.wtf("Image", "Converted bytes are: " + imageBytes);
                    mAdminFragment.getmAdminMain().getmNewAnnouncementFragment().setmImageBytes(imageBytes);
                } catch (IOException f){
                    Log.wtf("crash", "sad face");
                    Toast.makeText(this, "Failed to add image", Toast.LENGTH_LONG).show();
                }
            }
            if (requestCode == ModifyOrganizationFragment.UPLOAD_OR_MODIFY_PHOTO){
                Log.wtf("Image", "intent result was okay");
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap image = getBitmapFromUri(selectedImageUri);
                    Log.wtf("Image", "Bitmap is: " + image.toString());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    byte[] imageBytes = stream.toByteArray();
                    Log.wtf("Image", "Converted bytes are: " + imageBytes);
                    mAdminFragment.getmAdminMain().getmModifyOrganizationFragment().setImageBytes(imageBytes);
                } catch (IOException f){
                    Log.wtf("crash", "sad face");
                    Toast.makeText(this, "Failed to add image", Toast.LENGTH_LONG).show();
                }
            }
            if (requestCode == AdminMainFragment.CHANGE_PARENT_PHOTO){
                Log.wtf("Image", "intent result was okay");
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap image = getBitmapFromUri(selectedImageUri);
                    Log.wtf("Image", "Bitmap is: " + image.toString());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    byte[] imageBytes = stream.toByteArray();
                    Log.wtf("Image", "Converted bytes are: " + imageBytes);
                    //TODO: send to Parse
                } catch (IOException f){
                    Log.wtf("crash", "sad face");
                    Toast.makeText(this, "Failed to add image", Toast.LENGTH_LONG).show();
                }
            }

            if (requestCode == ProfileFragment.UPDATE_PROFILE_IMAGE){
                Log.wtf("Image", "intent result was okay");
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap image = getBitmapFromUri(selectedImageUri);
                    Log.wtf("Image", "Bitmap is: " + image.toString());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    byte[] imageBytes = stream.toByteArray();
                    Log.wtf("Image", "Converted bytes are: " + imageBytes);
                    //TODO: update photo in parse
                    ((ProfileFragment)mYouFragment.getmProfileFragment()).updateImage(image);
                    Toast.makeText(this, "Image succesfully updated", Toast.LENGTH_LONG).show();
                } catch (IOException f){
                    Log.wtf("crash", "sad face");
                    Toast.makeText(this, "Failed to add image", Toast.LENGTH_LONG).show();
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
