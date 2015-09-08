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

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;

public class AdminMainFragment extends Fragment implements Serializable {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ADMIN_LISTENER = "param1";
    private static final String ARG_ADMIN_ORG = "current_admin_org";
    private static final String ADMIN_MAIN_TAG = "admin_main_frag";
    public static final int CHANGE_PARENT_PROFILE_PHOTO = 4;
    public static final int CHANGE_PARENT_COVER_PHOTO = 7;

    private AdminInteractionListener mListener;
    private View mView;
    private Organization mOrg;
    private NewAnnouncementFragment mNewAnnouncementFragment;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param adminInteractionListener Parameter 1
     * @return A new instance of fragment AdminMainFragment.
     */
    public static AdminMainFragment newInstance(Organization currentAdminOrg, AdminInteractionListener adminInteractionListener) {
        AdminMainFragment fragment = new AdminMainFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ADMIN_LISTENER, adminInteractionListener);
        args.putSerializable(ARG_ADMIN_ORG, currentAdminOrg);
        fragment.setArguments(args);
        return fragment;
    }

    public Organization getmOrg() {
        return mOrg;
    }

    public AdminMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mListener = (AdminInteractionListener) getArguments().getSerializable(ARG_ADMIN_LISTENER);
            mOrg = (Organization) getArguments().getSerializable(ARG_ADMIN_ORG);
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
        //TODO: get org name (ex. "board") based on config
        String typeOfOrg = "Board";
        String typeOfChild = "School";

        if (!mOrg.isChildless()){
            LinearLayout hasChildrenFunctions = (LinearLayout)mView.findViewById(R.id.admin_main_has_children_fields);
            hasChildrenFunctions.setVisibility(View.VISIBLE);

            TextView viewChildrenOrgs= (TextView) mView.findViewById(R.id.text_view_children);
            viewChildrenOrgs.setText(getString(R.string.format_view_children, typeOfChild+"s"));
            viewChildrenOrgs.setVisibility(View.VISIBLE);

            TextView addChildOrg= (TextView) mView.findViewById(R.id.text_add_child_org);
            addChildOrg.setText(getString(R.string.format_add_child_org, typeOfChild));
            addChildOrg.setVisibility(View.VISIBLE);

            TextView viewPendingPosts= (TextView) mView.findViewById(R.id.text_view_child_pending_posts);
            viewPendingPosts.setText(getString(R.string.format_view_child_pending_posts, typeOfChild));
            viewPendingPosts.setVisibility(View.VISIBLE);
        }

        TextView addOrgAnnouncement = (TextView) mView.findViewById(R.id.text_add_org_announcement);
        addOrgAnnouncement.setText(getString(R.string.format_new_org_announcment, typeOfOrg));

        TextView allOrgAnnouncements= (TextView) mView.findViewById(R.id.text_all_org_announcements);
        allOrgAnnouncements.setText(getString(R.string.format_all_org_announcements, typeOfOrg));

        TextView viewOrgAdmins= (TextView) mView.findViewById(R.id.text_view_org_admins);
        viewOrgAdmins.setText(getString(R.string.format_view_org_admin, typeOfOrg));

        TextView changeOrgPhoto= (TextView) mView.findViewById(R.id.text_change_org_picture);
        changeOrgPhoto.setText(getString(R.string.format_change_org_photo, typeOfOrg));

        TextView changeOrgCoverPhoto = (TextView)mView.findViewById(R.id.text_change_org_cover_picture);
        changeOrgCoverPhoto.setText(getString(R.string.format_change_org_cover_photo, typeOfOrg));
    }

    private void setupClickableValues() {
        /**
         * the following Linearlayouts are the various possible "buttons" to click to complete various actions
         */

        if (!mOrg.isChildless()){
            LinearLayout viewChildrenOrgs= (LinearLayout) mView.findViewById(R.id.admin_view_children);
            viewChildrenOrgs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(ADMIN_MAIN_TAG, "view children");

                    mListener.viewChildren(mOrg);
                }
            });


            LinearLayout addChildOrg= (LinearLayout) mView.findViewById(R.id.admin_add_child_org);
            addChildOrg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(ADMIN_MAIN_TAG, "add admin");
                    mListener.addChildOrganization(mOrg);
                }
            });

            LinearLayout viewChildPendingPosts= (LinearLayout) mView.findViewById(R.id.admin_view_pending_posts);
            viewChildPendingPosts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(ADMIN_MAIN_TAG, "view child pending");
                    mListener.getChildPendingPosts(mOrg);
                }
            });
        }

        LinearLayout modifyOrg = (LinearLayout)mView.findViewById(R.id.admin_view_modify_org);
        modifyOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "add announcement");
                mListener.modifyOrg(mOrg);
            }
        });

        LinearLayout addOrgAnnouncement = (LinearLayout) mView.findViewById(R.id.admin_add_org_announcement);
        addOrgAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "add announcement");
                mNewAnnouncementFragment = NewAnnouncementFragment.newInstance(mOrg);
                mListener.addAnnouncement(mOrg);
            }
        });


        LinearLayout allOrgAnnouncements= (LinearLayout) mView.findViewById(R.id.admin_all_org_announcements);
        allOrgAnnouncements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "view announcements");
                mListener.viewAnnouncementsState(mOrg);
            }
        });

        LinearLayout viewOrgAdmins= (LinearLayout) mView.findViewById(R.id.admin_view_org_admins);
        viewOrgAdmins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "view admins");
                mListener.userListOpened(mOrg);
            }
        });


        LinearLayout changeOrgPhoto= (LinearLayout) mView.findViewById(R.id.admin_change_profile_org_picture);
        changeOrgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "change photo");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), CHANGE_PARENT_PROFILE_PHOTO);
            }
        });

        LinearLayout changeOrgCoverPhoto = (LinearLayout) mView.findViewById(R.id.admin_change_cover_org_picture);
        changeOrgCoverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "change photo");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), CHANGE_PARENT_COVER_PHOTO);
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
        void getChildPendingPosts (Organization parentOrg);
        void userListOpened(Organization parentOrg);
        void viewAnnouncementsState(Organization organization);
        void modifyOrg (Organization organization);
    }

    public NewAnnouncementFragment getmNewAnnouncementFragment() {
        return mNewAnnouncementFragment;
    }

}
