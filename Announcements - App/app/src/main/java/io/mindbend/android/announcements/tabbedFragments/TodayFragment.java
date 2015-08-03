package io.mindbend.android.announcements.tabbedFragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.mindbend.android.announcements.App;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.TabbedActivity;
import io.mindbend.android.announcements.reusableFrags.PostCommentsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsCardsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsFeedAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment implements PostsFeedAdapter.PostInteractionListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {

    //in order to add frags to the backstack
    public static final String TODAY_POSTS_FRAG = "today_posts_frag";
    public static final String COMMENTS_FRAG = "comments_frag";

    //to allow the activity to figure out if the today frag is on posts or comments
    public boolean isOnComments = false;
    private Fragment mCurrentComments;
    private ImageButton mFab;

    //saving the last post in order to get the club's name, as used in the "add comment" dialog
    Post mLastPost;


    public TodayFragment() {
        // Required empty public constructor

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_today, container, false);
        setRetainInstance(true);

        //instantiate the fab so that you can change its onClick method and src logo when switching between posts and comments fragments
        mFab = (ImageButton)v.findViewById(R.id.today_fab);
        mFab.setOnClickListener(this);

        //set the listener for the posts feed adapter in order to open the comments feed for a post
        PostsFeedAdapter.setListener(this);

        //TODO: query today's posts data from Parse, then pass that data into a PostsCardFragment that will be created using the PostsCardsFragment.NewInstance static method
        Fragment postsFragment = PostsCardsFragment.newInstance("test", "test");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.today_framelayout, postsFragment).addToBackStack(TODAY_POSTS_FRAG).commit();

        return v;
    }

    @Override
    public void onClick(View v) {
        if (isOnComments){
            //TODO: dialogue box to add a comment
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(getActivity());
            View addCommentView = li.inflate(R.layout.add_comment_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            // set the dialog's view to alertdialog builder
            alertDialogBuilder.setView(addCommentView);

            //work with all the elements in the dialog
            final EditText userInput = (EditText) addCommentView.findViewById(R.id.add_comment_edittext);

            //set the subtext to notify the user on WHOSE post they are commenting on
            TextView subText = (TextView)addCommentView.findViewById(R.id.add_comment_subtext);
            subText.setText(getString(R.string.add_comment_dialog_subtext, mLastPost.getmPostClubUsername()));

            //setting up the spinner(dropdown) to select which user/club to post the comment from
            Spinner spinner = (Spinner)addCommentView.findViewById(R.id.user_spinner);
            //TODO: dynamically create the user list
            String[] userArray = {"User 1", "User 2"};
            ArrayAdapter<String> userAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, userArray);
            spinner.setAdapter(userAdapter);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Post",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    //TODO: add a comment to the comments post
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
        else {
            //TODO: dialogue box to change today date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date()); //new Date gets the current date and time

            //instantiate the date picker dialog and implement the onDateSet method (it is implemented by the today frag)
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme ,this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //TODO: reload the posts fragment with the new date.
    }

    @Override
    public void pressedPost(Post postPressed) {
        mLastPost = postPressed;

        isOnComments = true;

        //set the fab's src/icon to the "add a new comment" image
        mFab.setImageResource(R.drawable.ic_comment);

        //replace the current posts frag with the comments frag, while adding it to a backstack (in case user clicks a commenters profile in which case returning to the comments frag would be required)
        mCurrentComments = PostCommentsFragment.newInstance(postPressed);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.today_framelayout, mCurrentComments).addToBackStack(COMMENTS_FRAG).commit();
    }

    public void returnFromComments(){
        isOnComments = false;

        //set the fab's src/icon back to the "change date" image
        mFab.setImageResource(R.drawable.ic_date);

        getChildFragmentManager()
                .beginTransaction()
                .remove(mCurrentComments)
                .commit();
        mCurrentComments = null;
        getChildFragmentManager().popBackStack();
    }
}
