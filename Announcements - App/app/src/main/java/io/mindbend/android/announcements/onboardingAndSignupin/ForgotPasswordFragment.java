package io.mindbend.android.announcements.onboardingAndSignupin;


import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.Serializable;

import io.mindbend.android.announcements.App;
import io.mindbend.android.announcements.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment implements Serializable {


    private String mEmail;
    private EditText mForgotPasswordEmail;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);

        //Stop keyboard from automatically popping up
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        View v = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        //Fetch UI fields
        mForgotPasswordEmail = (EditText) v.findViewById(R.id.forgot_password_email);
        Button resetPasswordButton = (Button) v.findViewById(R.id.reset_password_button);

        //button colour background if under API 21 (default tint will not work)
        //NOTE: TINT DOES NOT WORK ON API 21! Background coloured instead.
        if (!App.isAPI22OrHigher) {
            resetPasswordButton.setBackgroundColor(getResources().getColor(R.color.accent));
        }

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get email from edittext
                mEmail = mForgotPasswordEmail.getText().toString();

                //Inflate dialog
                AlertDialog.Builder resetPassDialog = new AlertDialog.Builder(getActivity())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing, close dialog
                            }
                        });

                if (mEmail.equals("")){
                    //No email inputted
                    //TODO: add condition for invalid email
                    resetPassDialog.setTitle(R.string.forgot_password_invalid_email);
                    resetPassDialog.setMessage(R.string.reset_password_dialog_bad_email);
                }else {
                    //email inputted
                    resetPassDialog.setTitle(R.string.forgot_password_email_sent);
                    resetPassDialog.setMessage(R.string.reset_password_dialog_text);
                }

                resetPassDialog.show();
            }
        });


        return v;
    }


}
