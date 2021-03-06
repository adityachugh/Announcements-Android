package io.mindbend.android.announcements.adminClasses;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.Serializable;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.TabbedActivity;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.cloudCode.AdminDataSource;
import io.mindbend.android.announcements.cloudCode.OrgsDataSource;
import io.mindbend.android.announcements.cloudCode.UserDataSource;
import io.mindbend.android.announcements.reusableFrags.OrgsGridFragment;
import io.mindbend.android.announcements.reusableFrags.SearchableFrag;
import io.mindbend.android.announcements.tabbedFragments.AdminFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ModifyOrganizationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModifyOrganizationFragment extends Fragment implements Serializable {
    private static final String ARG_PARENT = "parent_org";
    private static final String ARG_ORG = "if_to_modify_org";
    private static final String ARG_LISTENER = "listener";
    public static final int UPLOAD_OR_MODIFY_PROFILE_PHOTO = 3;
    public static final int UPLOAD_OR_MODIFY_COVER_PHOTO = 8;
    private Organization mParentOrg;
    private Organization mOrgToModify;
    private ModifyOrgInterface mListener;

    private EditText mName;
    private EditText mHandle;
    private TextView mHandleTV;
    private EditText mAccessCode;
    private TextView mAccessCodeTitle;
    private EditText mDescription;
    private byte[] toUploadProfileImageBytes;
    private byte[] toUploadCoverImageBytes;
    private User mInitialAdmin;
    private View mView;

    private boolean isPrivate = true;
    private int mUpdatesToBeMade;

    public static ModifyOrganizationFragment newInstance(Organization parentOrg, Organization orgToModifyIfNeeded,
                                                         ModifyOrgInterface listener) {
        ModifyOrganizationFragment fragment = new ModifyOrganizationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARENT, parentOrg);
        args.putSerializable(ARG_ORG, orgToModifyIfNeeded);
        args.putSerializable(ARG_LISTENER, listener);
        fragment.setArguments(args);
        return fragment;
    }

    public ModifyOrganizationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParentOrg = (Organization) getArguments().getSerializable(ARG_PARENT);
            mOrgToModify = (Organization) getArguments().getSerializable(ARG_ORG);
            mListener = (ModifyOrgInterface) getArguments().getSerializable(ARG_LISTENER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_modify_organization, container, false);

        setupViews(mView);

        if (mOrgToModify != null) {
            mName.setText(mOrgToModify.getTitle());
            mHandle.setText(mOrgToModify.getTag());
            mHandle.setEnabled(false);
            mHandleTV.setTextColor(getResources().getColor(R.color.text_secondary));
            LinearLayout approvalRequired = (LinearLayout) mView.findViewById(R.id.newO_approval_required_field);
            approvalRequired.setVisibility(View.GONE);
            if (mOrgToModify.isPrivateOrg()) {
                isPrivate = true;
                ((RadioButton) mView.findViewById(R.id.newO_type_private)).setChecked(true);
            } else {
                isPrivate = false;
                ((RadioButton) mView.findViewById(R.id.newO_type_public)).setChecked(true);
            }
        } else {
            LinearLayout mInitialAdminField = (LinearLayout) mView.findViewById(R.id.newO_add_admin_field);
            mInitialAdminField.setVisibility(View.VISIBLE);
            mInitialAdminField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.wtf("Create new org", "choosing initial admin...");
                    //take you to search view to choose an initial admin
                    if (mListener != null)
                        mListener.searchForAdmins(null);
                    else {
                        Log.wtf("Create new org", "listener (interface) is null!");
                    }
                }
            });
        }

        ImageButton updateOrCreateOrgFab = (ImageButton) mView.findViewById(R.id.new_OR_modify_org_fab);
        updateOrCreateOrgFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mName.getText().toString().equals("") || mHandle.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                    builder.setMessage("Cannot have an empty name or handle for an organization.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //nothing
                                }
                            })
                            .show();
                } else if (isPrivate) {
                    String accessCode = mAccessCode.getText().toString();
                    Log.wtf("Access Code", accessCode + "----->" + accessCode.length());
                    if (accessCode.length() == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                        builder.setMessage(getActivity().getString(R.string.create_private_org_without_access_code_message))
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        submitOrg();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //nothing
                                    }
                                })
                                .show();
                    } else if (accessCode.length() == 4) {
                        submitOrg();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                        builder.setMessage(getActivity().getString(R.string.access_code_length_message))
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //nothing
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
                } else {
                    submitOrg();
                }
            }
        });

        return mView;
    }

    private void submitOrg() {
        if (mOrgToModify != null) {

            final ProgressDialog dialog = new ProgressDialog(getActivity(), R.style.DialogTheme);
            dialog.setMessage(getActivity().getString(R.string.modify_existing_org_message));
            dialog.show();
            AdminDataSource.updateOrganizationFields(getActivity(), mView, mOrgToModify.getmObjectId(), mAccessCode.getText().toString(),
                    mDescription.getText().toString(), mName.getText().toString(), new FunctionCallback<Organization>() {
                        @Override
                        public void done(Organization organization, ParseException e) {
                            dialog.dismiss();
                            if (e == null && organization != null) {
                                ((TabbedActivity) getActivity()).getmAdminFragment().updateModifiedAdminOrg(organization);
                                OrgsGridFragment mainFragment = ((OrgsGridFragment) getFragmentManager().findFragmentByTag(AdminFragment.ADMIN_ORGS_TAG));
                                getFragmentManager().beginTransaction().show(mainFragment).commitAllowingStateLoss();
                                getFragmentManager().popBackStack();
                            }
                        }
                    });
        } else {
            /**
             * Create the new org in Parse (with an access code if necessary)
             */
            String initAdminObjectId = mInitialAdmin == null ? ParseUser.getCurrentUser().getObjectId() : mInitialAdmin.getmObjectId();
            Integer accessCode = (!isPrivate || mAccessCode.getText().toString().equals("")) ? null : Integer.parseInt(mAccessCode.getText().toString());
            boolean approvalRequired = ((Switch) mView.findViewById(R.id.newO_approval_required_switch)).isChecked();
            String childLevelConfigObjectId = mParentOrg.getmChildConfig() == null ? null : mParentOrg.getmChildConfig().getmObjectId();

            final ProgressDialog dialog2 = new ProgressDialog(getActivity(), R.style.DialogTheme);
            dialog2.setMessage(getActivity().getString(R.string.create_new_org_message));
            dialog2.show();
            AdminDataSource.createNewChildOrganization(mView, getActivity(), mParentOrg.getmObjectId(),
                    mParentOrg.getmConfigId(), mName.getText().toString(),
                    mHandle.getText().toString(), isPrivate, initAdminObjectId, approvalRequired, accessCode,
                    toUploadProfileImageBytes, toUploadCoverImageBytes, mDescription.getText().toString(),
                    mParentOrg.getmMainLevel().getmObjectId(), childLevelConfigObjectId, new FunctionCallback<Boolean>() {
                        @Override
                        public void done(Boolean success, ParseException e) {
                            dialog2.dismiss();
                            if (success && e == null) {
                                Snackbar.make(mView, "Successfully created " + mName.getText().toString(), Snackbar.LENGTH_SHORT).show();
                                ((TabbedActivity) getActivity()).getmAdminFragment().getChildFragmentManager().popBackStack();
                            }
                        }
                    });
        }
    }

    public void setProfileImageBytes(byte[] profileImageBytes) {
        toUploadProfileImageBytes = profileImageBytes;
    }

    public void setCoverImageBytes(byte[] coverImageBytes) {
        toUploadCoverImageBytes = coverImageBytes;
    }

    private void changesCompleted() {
        mUpdatesToBeMade--;
        if (mUpdatesToBeMade == 0)
            getFragmentManager().popBackStack();
    }

    private void setupViews(View v) {
        mName = (EditText) v.findViewById(R.id.newO_name);
        mHandle = (EditText) v.findViewById(R.id.newO_handle);
        mHandleTV = (TextView) v.findViewById(R.id.newO_handle_TV);
        mAccessCode = (EditText) v.findViewById(R.id.newO_access_code_ET);
        mAccessCodeTitle = (TextView) v.findViewById(R.id.newO_access_code_TV);
        RadioGroup mOrgType = (RadioGroup) v.findViewById(R.id.newO_org_type);

        mDescription = (EditText) mView.findViewById(R.id.newO_description);
        if (mOrgToModify != null) {
            if (mOrgToModify.hasAccessCode())
                mAccessCode.setText("" + mOrgToModify.getmAccessCode());
            mDescription.setText(mOrgToModify.getDescription());
        }

        mOrgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.newO_type_private:
                        isPrivate = true;
                        mAccessCode.setEnabled(true);
                        mAccessCodeTitle.setTextColor(getResources().getColor(R.color.text_primary));
                        break;
                    case R.id.newO_type_public:
                        isPrivate = false;
                        mAccessCode.setEnabled(false);
                        mAccessCodeTitle.setTextColor(getResources().getColor(R.color.text_secondary));
                        break;
                }
            }
        });

        LinearLayout profileImageField = (LinearLayout) v.findViewById(R.id.newO_profile_image_field);
        profileImageField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("Image", "image selection begun");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), UPLOAD_OR_MODIFY_PROFILE_PHOTO);
            }
        });

        LinearLayout coverImageField = (LinearLayout) v.findViewById(R.id.newO_cover_image_field);
        coverImageField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("Image", "image selection begun");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), UPLOAD_OR_MODIFY_COVER_PHOTO);
            }
        });

        TextView orgType = (TextView)v.findViewById(R.id.modify_org_type_TV);
        String orgTypeName = (mOrgToModify != null) ? mOrgToModify.getmLevelConfig().getmLevelTitle() : mParentOrg.getmChildConfig().getmLevelTitle();
        orgType.setText(orgTypeName + " Type");
    }

    public void setInitialAdmin(User initialAdmin) {
        mInitialAdmin = initialAdmin;
        Log.wtf("Modify Org", "Successfully set "+mInitialAdmin.getmFirstName()+" as initial admin in frag");
    }

    public interface ModifyOrgInterface extends Serializable {
        void searchForAdmins(Organization organization);
    }
}
