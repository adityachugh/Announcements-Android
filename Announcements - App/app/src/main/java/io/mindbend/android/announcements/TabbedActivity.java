package io.mindbend.android.announcements;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import io.mindbend.android.announcements.adminClasses.AdminMainFragment;
import io.mindbend.android.announcements.adminClasses.ModifyOrganizationFragment;
import io.mindbend.android.announcements.adminClasses.NewAnnouncementFragment;
import io.mindbend.android.announcements.cloudCode.UserDataSource;
import io.mindbend.android.announcements.cloudCode.VerificationDataSource;
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
    public YouFragment mYouFragment;

    private Bundle mSavedInstanceState;
    private transient Toolbar mToolbar;
    private transient TextView mTitleTextView;

    private boolean userIsAdmin = false;

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
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        //gets tabBar, adds tabs
        mTabBar = (MaterialTabHost) findViewById(R.id.tab_bar);
        mTabBar.addTab(mTabBar.newTab().setText("Today").setTabListener(this));
        mTabBar.addTab(mTabBar.newTab().setText("Discover").setTabListener(this));
        mTabBar.addTab(mTabBar.newTab().setText("You").setTabListener(this));

        mToolbar.setTitle(getString(R.string.format_tabbed_activity_toolbar_text, ParseUser.getCurrentUser().getString(VerificationDataSource.USER_FIRST_NAME))); //TODO: pull in user's name
        setSupportActionBar(mToolbar);

        mTitleTextView = (TextView)findViewById(R.id.landscape_toolbar_title);

        if (getScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            mTabBar.setVisibility(View.GONE);
            mTitleTextView.setVisibility(View.VISIBLE);
            updateLandscapePageText();
        }

        UserDataSource.getOrganizationsThatUserIsAdminOf(this, (ProgressBar)findViewById(R.id.activity_overall_progressbar), ParseUser.getCurrentUser().getObjectId(), new FunctionCallback<ArrayList<Organization>>() {
            @Override
            public void done(ArrayList<Organization> organizations, ParseException e) {
                if (e == null){
                    if (organizations != null && organizations.size() > 0){
                        userIsAdmin = true;
                        mAdminFragment = AdminFragment.newInstance(organizations);
                        mTabBar.addTab(mTabBar.newTab().setText("Admin").setTabListener(TabbedActivity.this));
                        mAdapter.notifyDataSetChanged();
                        mTabBar.notifyDataSetChanged();
                    }
                }
            }
        });

    }

    private void updateLandscapePageText() {
        String tabText = "";
        switch (mViewPager.getCurrentItem()){
            case 0:
                tabText = "Today";
                break;
            case 1:
                tabText = "Discover";
                break;
            case 2:
                tabText = "You";
                break;
            case 3:
                tabText = "Admin";
                break;

        }
        mTitleTextView.setText(tabText);
    }

    public int getScreenOrientation()
    {
        /**
         * This method is being used instead of getResources.getConfiguration.orientation
         * as that sometimes returns the WRONG orientation on old devices.
         */
        Display getOrient = getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if(getOrient.getWidth()==getOrient.getHeight()){
            orientation = Configuration.ORIENTATION_SQUARE;
        } else{
            if(getOrient.getWidth() < getOrient.getHeight()){
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (getScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            updateLandscapePageText();
        }
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
                else if (!mTodayFragment.getmPostsOverlayFragment().getmPostsFragment().isVisible()){
                    mTodayFragment.getmPostsOverlayFragment().pressedBackToPosts();
                }
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
                if(!mAdminFragment.getmAdminOrgsFrag().isVisible())
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
            if (userIsAdmin)
                return 4;
            else {
                return 3;
            }
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

    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
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
                    mAdminFragment.getmAdminMainFrag().getmNewAnnouncementFragment().setmImageBytes(imageBytes);
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
                    mAdminFragment.getmAdminMainFrag().getmModifyOrganizationFragment().setImageBytes(imageBytes);
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

            if (requestCode == ProfileFragment.UPDATE_PROFILE_IMAGE || requestCode == ProfileFragment.UPDATE_COVER_IMAGE){
                Log.wtf("Image", "intent result was okay");
                Uri selectedImageUri = data.getData();
                try {
                    int resW = (requestCode == ProfileFragment.UPDATE_PROFILE_IMAGE) ? 500 : 2000;
                    int resH = (requestCode == ProfileFragment.UPDATE_PROFILE_IMAGE) ? 500 : 1200;
                    final Bitmap image = Bitmap.createScaledBitmap(getBitmapFromUri(selectedImageUri), resW, resH, true);
                    Log.wtf("Image", "Bitmap is: " + image.toString());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    byte[] imageBytes = stream.toByteArray();
                    Log.wtf("Image", "Converted bytes are: " + imageBytes.toString());
                    boolean isUpdatingProfilePhoto = (requestCode == ProfileFragment.UPDATE_PROFILE_IMAGE);
                    UserDataSource.updateUserProfileImages(this, mYouFragment.mLoading, imageBytes, new FunctionCallback<Boolean>() {
                        @Override
                        public void done(Boolean success, ParseException e) {
                            if (success) {
                                if (requestCode == ProfileFragment.UPDATE_PROFILE_IMAGE)
                                    ((ProfileFragment) mYouFragment.getmProfileFragment()).updateProfileImage(image);
                                else
                                    ((ProfileFragment) mYouFragment.getmProfileFragment()).updateCoverImage(image);
                                Toast.makeText(TabbedActivity.this, "Image successfully updated", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(TabbedActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }, isUpdatingProfilePhoto);
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
