package io.mindbend.android.announcements.adminClasses;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.mindbend.android.announcements.R;

public class AdminMainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ADMIN_MAIN_TAG = "admin_main_frag";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminMainFragment newInstance(String param1, String param2) {
        AdminMainFragment fragment = new AdminMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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

        TextView addOrgAdmin= (TextView) mView.findViewById(R.id.text_add_org_admin);
        addOrgAdmin.setText(getString(R.string.format_add_org_admin, typeOfOrg));

        TextView viewOrgAdmins= (TextView) mView.findViewById(R.id.text_view_org_admins);
        viewOrgAdmins.setText(getString(R.string.format_view_org_admin, typeOfOrg));

        TextView changeOrgPhoto= (TextView) mView.findViewById(R.id.text_change_org_picture);
        changeOrgPhoto.setText(getString(R.string.format_change_org_photo, typeOfOrg));
    }

    private void setupClickableValues() {
        /**
         * the following Linearlayouts are the various possible "buttons" to click to complete various actions
         */

        //TODO: only add certain layouts if the user is a certain level of admin

        LinearLayout addOrgAnnouncement = (LinearLayout) mView.findViewById(R.id.admin_add_org_announcement);
        addOrgAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "add announcement");
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
            }
        });
        LinearLayout addOrgAdmin= (LinearLayout) mView.findViewById(R.id.admin_add_org_admin);
        addOrgAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "add admin");
            }
        });
        LinearLayout viewOrgAdmins= (LinearLayout) mView.findViewById(R.id.admin_view_org_admins);
        viewOrgAdmins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "view admins");
            }
        });
        LinearLayout changeOrgPhoto= (LinearLayout) mView.findViewById(R.id.admin_change_org_picture);
        changeOrgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ADMIN_MAIN_TAG, "change photo");
            }
        });
    }


}
