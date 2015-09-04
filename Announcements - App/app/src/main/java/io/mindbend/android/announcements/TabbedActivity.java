package io.mindbend.android.announcements;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
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

import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import io.mindbend.android.announcements.adminClasses.AdminMainFragment;
import io.mindbend.android.announcements.adminClasses.ModifyOrganizationFragment;
import io.mindbend.android.announcements.adminClasses.NewAnnouncementFragment;
import io.mindbend.android.announcements.cloudCode.AdminDataSource;
import io.mindbend.android.announcements.cloudCode.UserDataSource;
import io.mindbend.android.announcements.cloudCode.VerificationDataSource;
import io.mindbend.android.announcements.reusableFrags.ProfileFragment;
import io.mindbend.android.announcements.tabbedFragments.AdminFragment;
import io.mindbend.android.announcements.tabbedFragments.DiscoverFragment;
import io.mindbend.android.announcements.tabbedFragments.YouFragment;
import io.mindbend.android.announcements.tabbedFragments.TodayFragment;

public class TabbedActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener, Serializable {


    //tab bar
    private transient TabLayout mTabBar;

    //viewpager; what allows the swiping between fragments
    private transient android.support.v4.view.ViewPager mViewPager;
    private PagerAdapter mAdapter;
    //all fragments under TabbedActivity
    private TodayFragment mTodayFragment;
    private AdminFragment mAdminFragment;
    private DiscoverFragment mDiscoverFragment;
    public YouFragment mYouFragment;

    private Bundle mSavedInstanceState;
    private transient TextView mTitleTextView;

    private boolean userIsAdmin = false;
    private View mView;
    private ProgressBar mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = getLayoutInflater().inflate(R.layout.activity_tabbed, null);
        setContentView(mView);

        mLoading = (ProgressBar) findViewById(R.id.activity_overall_progressbar);

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

        //gets tabBar, adds tabs
        mTabBar = (TabLayout) findViewById(R.id.tab_bar);
        mTabBar.setupWithViewPager(mViewPager);
        setupTabsForTabBar();

        toolbar.setTitle(getString(R.string.format_tabbed_activity_toolbar_text, ParseUser.getCurrentUser().getString(VerificationDataSource.USER_FIRST_NAME))); //TODO: pull in user's name
        setSupportActionBar(toolbar);

        mTitleTextView = (TextView) findViewById(R.id.landscape_toolbar_title);

        if (getScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            mTabBar.setVisibility(View.GONE);
            mTitleTextView.setVisibility(View.VISIBLE);
            updateLandscapePageText();
        }

        UserDataSource.getOrganizationsThatUserIsAdminOf(mView, mLoading, ParseUser.getCurrentUser().getObjectId(), new FunctionCallback<ArrayList<Organization>>() {
            @Override
            public void done(ArrayList<Organization> organizations, ParseException e) {
                if (e == null) {
                    if (organizations != null && organizations.size() > 0) {
                        nonAdminUserIsNowAdmin(organizations);
                    }
                }
            }
        });

    }

    private void setupTabsForTabBar() {
        for (int i = 0; i < mTabBar.getTabCount(); i++) {
            mTabBar.getTabAt(i).setText(getCurrentTabTitle(i));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mTabBar.setVisibility(View.GONE);
            mTitleTextView.setVisibility(View.VISIBLE);
            updateLandscapePageText();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mTabBar.setVisibility(View.VISIBLE);
            mTitleTextView.setVisibility(View.GONE);
        }
    }

    public void nonAdminUserIsNowAdmin(ArrayList<Organization> organizations) {
        userIsAdmin = true;
        mAdminFragment = AdminFragment.newInstance(organizations);
        mTabBar.addTab(mTabBar.newTab().setText("Admin"));
        mAdapter.notifyDataSetChanged();
    }

    private void updateLandscapePageText() {
        mTitleTextView.setText(getCurrentTabTitle(mViewPager.getCurrentItem()));
    }

    @NonNull
    private String getCurrentTabTitle(int position) {
        String tabText = "";
        switch (position) {
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
        return tabText;
    }

    public int getScreenOrientation() {
        /**
         * This method is being used instead of getResources.getConfiguration.orientation
         * as that sometimes returns the WRONG orientation on old devices.
         */
        Display getOrient = getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if (getOrient.getWidth() == getOrient.getHeight()) {
            orientation = Configuration.ORIENTATION_SQUARE;
        } else {
            if (getOrient.getWidth() < getOrient.getHeight()) {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            } else {
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
    }

    public ViewPager getmViewPager() {
        return mViewPager;
    }

    public AdminFragment getmAdminFragment() {
        return mAdminFragment;
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
                else if (!mTodayFragment.getmPostsOverlayFragment().getmPostsFragment().isVisible()) {
                    mTodayFragment.getmPostsOverlayFragment().pressedBackToPosts();
                }
                break;
            case 1:
                if (!mDiscoverFragment.getmOrgsGridFrag().isVisible())
                    mDiscoverFragment.getChildFragmentManager().popBackStack();
                break;
            case 2:
                if (!mYouFragment.getmProfileFragment().isVisible())
                    mYouFragment.getChildFragmentManager().popBackStack();
                break;
            case 3:
                if (!mAdminFragment.getmAdminOrgsFrag().isVisible())
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
                        mTodayFragment = (TodayFragment) getSupportFragmentManager().findFragmentById(R.id.today_frag);
                    else if (mTodayFragment == null) mTodayFragment = new TodayFragment();
                    return mTodayFragment;
                case 1:
                    if (mSavedInstanceState != null)
                        mDiscoverFragment = (DiscoverFragment) getSupportFragmentManager().findFragmentById(R.id.discove_frag);
                    else if (mDiscoverFragment == null) mDiscoverFragment = new DiscoverFragment();
                    return mDiscoverFragment;
                case 2:
                    if (mSavedInstanceState != null)
                        mYouFragment = (YouFragment) getSupportFragmentManager().findFragmentById(R.id.you_frag);
                    else if (mYouFragment == null) mYouFragment = new YouFragment();
                    return mYouFragment;
                case 3:
                    if (mSavedInstanceState != null)
                        mAdminFragment = (AdminFragment) getSupportFragmentManager().findFragmentById(R.id.admin_frag);
                    else if (mAdminFragment == null) mAdminFragment = new AdminFragment();
                    return mAdminFragment;
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            switch (position) {
                case 0:
                    mTodayFragment = (TodayFragment) fragment;
                    break;
                case 1:
                    mDiscoverFragment = (DiscoverFragment) fragment;
                    break;
                case 2:
                    mYouFragment = (YouFragment) fragment;
                    break;
                case 3:
                    mAdminFragment = (AdminFragment) fragment;
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
                } catch (IOException f) {
                    Log.wtf("crash", "sad face");
                    Snackbar.make(mView, "Failed to add image", Snackbar.LENGTH_LONG).show();
                }
            }
            if (requestCode == ModifyOrganizationFragment.UPLOAD_OR_MODIFY_PROFILE_PHOTO || requestCode == ModifyOrganizationFragment.UPLOAD_OR_MODIFY_COVER_PHOTO) {
                Log.wtf("Image", "intent result was okay");
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap image = getBitmapFromUri(selectedImageUri);
                    Log.wtf("Image", "Bitmap is: " + image.toString());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    byte[] imageBytes = stream.toByteArray();
                    Log.wtf("Image", "Converted bytes are: " + imageBytes);
                    if (requestCode == ModifyOrganizationFragment.UPLOAD_OR_MODIFY_PROFILE_PHOTO) {
                        ((ModifyOrganizationFragment) mAdminFragment.getmCurrentOrgModifyFrag()).setProfileImageBytes(imageBytes);
                    } else {
                        ((ModifyOrganizationFragment) mAdminFragment.getmCurrentOrgModifyFrag()).setCoverImageBytes(imageBytes);
                    }
                } catch (IOException f) {
                    Log.wtf("crash", "sad face");
                    Snackbar.make(mView, "Failed to add image", Snackbar.LENGTH_LONG).show();
                }
            }
            if (requestCode == AdminMainFragment.CHANGE_PARENT_PROFILE_PHOTO || requestCode == AdminMainFragment.CHANGE_PARENT_COVER_PHOTO) {
                Log.wtf("Image", "intent result was okay");
                Uri selectedImageUri = data.getData();
                byte[] imageBytes = convertImageUriToUploadableByteArray(selectedImageUri, requestCode, AdminMainFragment.CHANGE_PARENT_PROFILE_PHOTO);
                Log.wtf("Image", "Converted bytes are: " + imageBytes);
                if (requestCode == AdminMainFragment.CHANGE_PARENT_PROFILE_PHOTO) {
                    AdminDataSource.updateOrganizationProfilePhoto(mView, TabbedActivity.this, mLoading, mAdminFragment.getmAdminMainFrag().getmOrg().getmObjectId(), imageBytes, null);
                } else {
                    AdminDataSource.updateOrganizationCoverPhoto(mView, TabbedActivity.this, mLoading, mAdminFragment.getmAdminMainFrag().getmOrg().getmObjectId(), imageBytes, null);
                }
            }

            if (requestCode == ProfileFragment.UPDATE_PROFILE_IMAGE || requestCode == ProfileFragment.UPDATE_COVER_IMAGE) {
                Log.wtf("Image", "intent result was okay");
                final Uri selectedImageUri = data.getData();
                byte[] imageBytes = convertImageUriToUploadableByteArray(selectedImageUri, requestCode, ProfileFragment.UPDATE_PROFILE_IMAGE);
                Log.wtf("Image", "Converted bytes are: " + imageBytes.toString());
                boolean isUpdatingProfilePhoto = (requestCode == ProfileFragment.UPDATE_PROFILE_IMAGE);
                UserDataSource.updateUserProfileImages(mView, this, mYouFragment.mLoading, imageBytes, new FunctionCallback<Boolean>() {
                    @Override
                    public void done(Boolean success, ParseException e) {
                        if (success) {
                            if (requestCode == ProfileFragment.UPDATE_PROFILE_IMAGE)
                                ((ProfileFragment) mYouFragment.getmProfileFragment()).updateProfileImage(returnToUploadBitmapFromImage(selectedImageUri, requestCode, ProfileFragment.UPDATE_PROFILE_IMAGE));
                            else
                                ((ProfileFragment) mYouFragment.getmProfileFragment()).updateCoverImage(returnToUploadBitmapFromImage(selectedImageUri, requestCode, ProfileFragment.UPDATE_PROFILE_IMAGE));
                            Snackbar.make(mView, "Image successfully updated", Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(mView, "Failure", Snackbar.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, isUpdatingProfilePhoto);
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

    private byte[] convertImageUriToUploadableByteArray(Uri uri, int requestCode, int profileImageRequestCode) {
        final Bitmap image = returnToUploadBitmapFromImage(uri, requestCode, profileImageRequestCode);
        Log.wtf("Image", "Bitmap is: " + image.toString());
        int maxSizeOfUpload = (requestCode == profileImageRequestCode) ? 50*1024 : 100*1024;
        int qualityToUse = 100;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, qualityToUse, stream);
        int imageBytesSize = stream.toByteArray().length;
        while (imageBytesSize > maxSizeOfUpload && qualityToUse >= 20) {
            qualityToUse -= 5;
            stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, qualityToUse, stream);
            imageBytesSize = stream.toByteArray().length;
            Log.wtf("Image size:", imageBytesSize+"");
        }
        Log.wtf("Image size FINAL:", imageBytesSize+"");
        return stream.toByteArray();
    }

    private Bitmap returnToUploadBitmapFromImage(Uri uri, int requestCode, int profileImageRequestCode) {
        int resW = (requestCode == profileImageRequestCode) ? 500 : 2000;
        int resH = (requestCode == profileImageRequestCode) ? 500 : 1200;
        Bitmap source = null;
        try {
            source = getBitmapFromUri(uri);
            return Bitmap.createScaledBitmap(source, resW, resH, true);
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(mView, "Could not convert image", Snackbar.LENGTH_SHORT).show();
            return null;
        }
    }
}
