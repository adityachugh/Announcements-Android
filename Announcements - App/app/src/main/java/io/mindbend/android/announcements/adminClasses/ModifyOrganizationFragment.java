package io.mindbend.android.announcements.adminClasses;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ModifyOrganizationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModifyOrganizationFragment extends Fragment {
    private static final String ARG_PARENT = "parent_org";
    private static final String ARG_ORG = "if_to_modify_org";
    public static final int UPLOAD_OR_MODIFY_PHOTO = 3;
    private Organization mParentOrg;
    private Organization mOrgToModify;

    private EditText mName;
    private EditText mHandle;
    private Switch mClubType;
    private EditText mAccessCode;
    private TextView mAccessCodeTitle;
    private byte[] imageBytes;

    public static ModifyOrganizationFragment newInstance(Organization parentOrg, Organization orgToModifyIfNeeded) {
        ModifyOrganizationFragment fragment = new ModifyOrganizationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARENT, parentOrg);
        args.putSerializable(ARG_ORG, orgToModifyIfNeeded);
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
            mParentOrg = (Organization)getArguments().getSerializable(ARG_PARENT);
            mOrgToModify = (Organization)getArguments().getSerializable(ARG_ORG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_modify_organization, container, false);

        if (mOrgToModify != null){
            //TODO: load in data from org and lock certain features (club handle)
        }

        setupViews(v);

        ImageButton updateOrCreateOrgFab = (ImageButton)v.findViewById(R.id.new_OR_modify_org_fab);
        updateOrCreateOrgFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mName.getText().toString().equals("") || mHandle.getText().toString().equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                    builder.setMessage("Cannot have an empty name or handle for an organization.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //nothing
                                }
                            });
                    AlertDialog alertDialog = builder.show();
                } else if(!mClubType.isChecked() && mAccessCode.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                    builder.setMessage("A private organization must have an access code. Make sure to remember this code!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //nothing
                                }
                            });
                    AlertDialog alertDialog = builder.show();
                } else {
                    /**
                     * everything is okay. Create the org in Parse (with an access code if necessary)
                     */
                }
            }
        });

        return v;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    private void setupViews(View v) {
        mName = (EditText)v.findViewById(R.id.newO_name);
        mHandle = (EditText)v.findViewById(R.id.newO_handle);
        mAccessCode = (EditText)v.findViewById(R.id.newO_access_code_ET);
        mAccessCodeTitle = (TextView)v.findViewById(R.id.newO_access_code_TV);
        mClubType = (Switch)v.findViewById(R.id.newO_org_type);

        mClubType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAccessCode.setEnabled(false);
                    mAccessCodeTitle.setTextColor(getResources().getColor(R.color.text_secondary));
                } else {
                    mAccessCode.setEnabled(true);
                    mAccessCodeTitle.setTextColor(getResources().getColor(R.color.text_primary));
                }
            }
        });

        LinearLayout imageField = (LinearLayout)v.findViewById(R.id.newO_image_field);
        imageField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("Image", "image selection begun");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), UPLOAD_OR_MODIFY_PHOTO);
            }
        });


    }
}
