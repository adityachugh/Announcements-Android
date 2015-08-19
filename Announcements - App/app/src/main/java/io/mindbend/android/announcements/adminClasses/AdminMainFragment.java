package io.mindbend.android.announcements.adminClasses;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.reusableFrags.ListFragment;
import io.mindbend.android.announcements.reusableFrags.UserListAdapter;

public class AdminMainFragment extends Fragment implements Serializable {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ADMIN_LISTENER = "param1";
    private static final String ADMIN_MAIN_TAG = "admin_main_frag";
    public static final int CHANGE_PARENT_PHOTO = 4;

    // TODO: Rename and change types of parameters
    private AdminInteractionListener mListener;
    private View mView;
    private NewAnnouncementFragment mNewAnnouncementFragment;
    private ModifyOrganizationFragment mModifyOrganizationFragment;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param adminInteractionListener Parameter 1
     * @return A new instance of fragment AdminMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminMainFragment newInstance(AdminInteractionListener adminInteractionListener) {
        AdminMainFragment fragment = new AdminMainFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ADMIN_LISTENER, adminInteractionListener);
        fragment.setArguments(args);
        return fragment;
    }

    public AdminMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mListener = (AdminInteractionListener) getArguments().getSerializable(ARG_ADMIN_LISTENER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mView == null){
           mView = inflater.inflate(R.layout.fragment_admin_main, container, false);

            setupClickableValues();
            setupTextOfOrg();

        }
        return mView;
    }

    private void setupTextOfOrg() {
        //TODO: get what TYPE of org it is (currently statically set to board)
        String typeOfOrg = "Board";
        String typeOfChild = "School";

        TextView addOrgAnnouncement = (TextView) mView.findViewById(R.id.text_add_org_announcement);
        addOrgAnnouncement.setText(getString(R.string.format_new_org_announcment, typeOfOrg));

        TextView allOrgAnnouncements= (TextView) mView.findViewById(R.id.text_all_org_announcements);
        allOrgAnnouncements.setText(getString(R.string.format_all_org_announcements, typeOfOrg));

        TextView viewChildrenOrgs= (TextView) mView.findViewById(R.id.text_view_children);
        viewChildrenOrgs.setText(getString(R.string.format_view_children, typeOfChild));

        TextView addChildOrg= (TextView) mView.findViewById(R.id.text_add_child_org);
        addChildOrg.setText(getString(R.string.format_add_child_org, typeOfChild));

        TextView viewOrgAdmins= (TextView) mView.findViewById(R.id.text_view_org_admins);
        viewOrgAdmins.setText(getString(R.string.format_view_org_admin, typeOfOrg));

        TextView changeOrgPhoto= (TextView) mView.findViewById(R.id.text_change_org_picture);
        changeOrgPhoto.setText(getString(R.string.format_change_org_photo, typeOfOrg));
    }

    private void setupClickableValues() {
        /**
         * the following Linearlayouts are the various possible "buttons" to click to complete various actions
         */

        //TODO: get the actual main org of the current user
        final Organization organization = new Organization("fake_data", "PDSB", "The best school board in Ontario", 60000, "#PDSB", false, false);

        //TODO: only add certain layouts if the user is a certain level of admin

        LinearLayout addOrgAnnouncement = (LinearLayout) mView.findViewById(R.id.admin_add_org_announcement);
        addOrgAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "add announcement");
                mNewAnnouncementFragment = NewAnnouncementFragment.newInstance(organization);
                mListener.addAnnouncement(organization);
            }
        });


        LinearLayout allOrgAnnouncements= (LinearLayout) mView.findViewById(R.id.admin_all_org_announcements);
        allOrgAnnouncements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "view announcements");
            }
        });


        LinearLayout viewChildrenOrgs= (LinearLayout) mView.findViewById(R.id.admin_view_children);
        viewChildrenOrgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "view children");

                mListener.viewChildren(organization);
            }
        });


        LinearLayout addChildOrg= (LinearLayout) mView.findViewById(R.id.admin_add_child_org);
        addChildOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "add admin");
                mModifyOrganizationFragment = ModifyOrganizationFragment.newInstance(organization, null);
                mListener.addChildOrganization(organization);
            }
        });


        LinearLayout viewOrgAdmins= (LinearLayout) mView.findViewById(R.id.admin_view_org_admins);
        viewOrgAdmins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "view admins");
                mListener.userListOpened(organization);
            }
        });


        LinearLayout changeOrgPhoto= (LinearLayout) mView.findViewById(R.id.admin_change_org_picture);
        changeOrgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "change photo");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), CHANGE_PARENT_PHOTO);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface AdminInteractionListener extends Serializable{
        void viewChildren(Organization org);
        void addAnnouncement(Organization organization);
        void addChildOrganization (Organization parentOrg);
        void userListOpened(Organization parentOrg);
    }

    public NewAnnouncementFragment getmNewAnnouncementFragment() {
        return mNewAnnouncementFragment;
    }

    public ModifyOrganizationFragment getmModifyOrganizationFragment() {
        return mModifyOrganizationFragment;
    }
}
