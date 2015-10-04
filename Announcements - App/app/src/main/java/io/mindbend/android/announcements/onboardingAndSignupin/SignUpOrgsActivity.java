package io.mindbend.android.announcements.onboardingAndSignupin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.parse.FunctionCallback;
import com.parse.ParseException;

import java.io.Serializable;
import java.util.ArrayList;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.TabbedActivity;
import io.mindbend.android.announcements.cloudCode.AdminDataSource;
import io.mindbend.android.announcements.cloudCode.OrgsDataSource;
import io.mindbend.android.announcements.cloudCode.UserDataSource;
import io.mindbend.android.announcements.reusableFrags.OrgsGridAdapter;
import io.mindbend.android.announcements.reusableFrags.OrgsGridFragment;

public class SignUpOrgsActivity extends ActionBarActivity implements Serializable, OrgsGridFragment.OrgsGridInteractionListener, OrgsGridAdapter.OrgInteractionListener{

    private static final String TAG = "SignUpOrgsActivity";

    public static final String SIGN_UP_ORGS = "sign_up_orgs";

    private transient ProgressBar mProgressBar;
    private transient ImageButton mFab;
    private OrgsGridAdapter.OrgInteractionListener mOrgInteractionListener = this;
    private OrgsGridFragment.OrgsGridInteractionListener mOrgsGridInteractionListener = this;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_orgs);

        Toolbar toolbar = (Toolbar)findViewById(R.id.sign_up_toolbar);
        toolbar.inflateMenu(R.menu.menu_sign_up_orgs);
        toolbar.hideOverflowMenu();
        toolbar.setTitle("Config title here");

        mProgressBar = (ProgressBar) findViewById(R.id.sign_up_orgs_progressbar);
        mFab = (ImageButton) findViewById(R.id.signup_fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Follow Organizations")
                        .setMessage("Are you done following organizations?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                followOrganizations(v, mContext, mProgressBar);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing
                            }
                        })
                        .show();
            }
        });

        loadTopLevelOrgs(mProgressBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up_orgs, menu);
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

    private void loadTopLevelOrgs(ProgressBar progressBar){
        OrgsDataSource.getAllTopLevelOrganizations(progressBar, mContext, progressBar, new FunctionCallback<ArrayList<Organization>>() {
            @Override
            public void done(ArrayList<Organization> organizations, ParseException e) {
                if (e == null){
                    OrgsGridFragment orgsGridFragment = OrgsGridFragment.newInstance(organizations, mOrgInteractionListener, mOrgsGridInteractionListener, null, true);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.sign_up_orgs_framelayout, orgsGridFragment).commitAllowingStateLoss();
                }
                else{
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void pressedOrgFromGrid(Organization orgPressed) {
    }

    @Override
    public void pressedOrg(Organization orgSelected) {
        //handle card clicks in here!
        loadChildren(mProgressBar, orgSelected.getmObjectId());
    }

    private void loadChildren(ProgressBar progressBar ,String orgId){
        OrgsDataSource.getAllChildOrganizations(progressBar, mContext, progressBar, R.id.sign_up_org_remove_view_while_loading,orgId, new FunctionCallback<ArrayList<Organization>>() {
            @Override
            public void done(ArrayList<Organization> organizations, ParseException e) {
                if (e == null) {
                    if (organizations.size() != 0) {
                        OrgsGridFragment childrenOrgs = OrgsGridFragment.newInstance(organizations, mOrgInteractionListener, mOrgsGridInteractionListener, null, true);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.sign_up_orgs_framelayout, childrenOrgs).addToBackStack(SIGN_UP_ORGS).commitAllowingStateLoss();
                    }
                    else {
                        Snackbar.make((findViewById(R.id.sign_up_orgs_view)), "This organization has no children.", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void followOrganizations(View v, Context context, ProgressBar progressBar){
        UserDataSource.followOrganizations(v, context, OrgsToFollow.getInstance());
    }
}
