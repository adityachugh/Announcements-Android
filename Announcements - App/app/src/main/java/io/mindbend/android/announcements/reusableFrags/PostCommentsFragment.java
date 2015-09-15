package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.parse.FunctionCallback;
import com.parse.ParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.mindbend.android.announcements.Comment;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.cloudCode.CommentsDataSource;

public class PostCommentsFragment extends Fragment implements Serializable, PostCommentsAdapter.CommenterInteractionListener, SwipyRefreshLayout.OnRefreshListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POST = "post";
    private static final String ARG_LISTENER = "comment_listener";

    private static final String SHARE_TAG = "Share_post_tag";

    private Post mPost;
    private PostCommentsAdapter mCommentsAdapter;
    private List<Comment> mComments;
    private transient ImageButton mFab;
    private CommentsInteractionListener mListener;
    private transient View mView;
    private transient SwipyRefreshLayout mRefreshComments;
    private transient RecyclerView mRecyclerView;
    private transient RelativeLayout mLoading;
    private TextView mNoCommentsText;

    public static PostCommentsFragment newInstance(Post postClicked, CommentsInteractionListener commentListener) {
        PostCommentsFragment fragment = new PostCommentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POST, postClicked);
        args.putSerializable(ARG_LISTENER, commentListener);
        fragment.setArguments(args);
        return fragment;
    }

    public PostCommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPost = (Post) getArguments().getSerializable(ARG_POST);
            mListener = (CommentsInteractionListener) getArguments().getSerializable(ARG_LISTENER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_post_comments, container, false);

            mNoCommentsText = (TextView) mView.findViewById(R.id.no_comments_loaded_TV);

            mRecyclerView = (RecyclerView) mView.findViewById(R.id.comments_recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            mLoading = (RelativeLayout) mView.findViewById(R.id.comments_progressbar_layout);
            mComments = new ArrayList<>();
            mCommentsAdapter = new PostCommentsAdapter(getActivity(), mComments, PostCommentsFragment.this, mLoading);
            mRecyclerView.setAdapter(mCommentsAdapter);
            loadComments(false, mLoading, 0, 10);

            mFab = (ImageButton) mView.findViewById(R.id.comments_fab);
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    // get prompts.xml view
                    LayoutInflater li = LayoutInflater.from(getActivity());
                    View addCommentView = li.inflate(R.layout.add_comment_dialog, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

                    // set the dialog's view to alertdialog builder
                    alertDialogBuilder.setView(addCommentView);

                    //work with all the elements in the dialog
                    final EditText userInput = (EditText) addCommentView.findViewById(R.id.add_comment_edittext);

                    //set the subtext to notify the user on WHOSE post they are commenting on
                    TextView subText = (TextView) addCommentView.findViewById(R.id.add_comment_subtext);
                    subText.setText(getString(R.string.add_comment_dialog_subtext, mPost.getmPostClubUsername()));

                    //FOR FUTURE: setting up the spinner(dropdown) to select which user/club to post the comment from
//                    Spinner spinner = (Spinner) addCommentView.findViewById(R.id.user_spinner);
//                    String[] userArray = {"User 1", "User 2"};
//                    ArrayAdapter<String> userAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, userArray);
//                    spinner.setAdapter(userAdapter);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Post",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if (userInput.getText().toString().equals(""))
                                                Snackbar.make(v, R.string.empty_comment_message, Snackbar.LENGTH_SHORT).show();
                                            else {
                                                CommentsDataSource.postCommentAsUserOnPost(mLoading, getActivity(), mPost.getmObjectId(), userInput.getText().toString(), new FunctionCallback<Comment>() {
                                                    @Override
                                                    public void done(Comment comment, ParseException e) {
                                                        if (e == null) {
                                                            mComments.add(0, comment);
                                                            mCommentsAdapter.notifyDataSetChanged();
                                                        } else {
                                                            e.printStackTrace();
                                                            Snackbar.make(v, "Error", Snackbar.LENGTH_SHORT);
                                                        }
                                                    }
                                                });
                                            }
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
            });

            //sharing the post
            Button shareButton = (Button) mView.findViewById(R.id.post_share_button);
            final String sharingPostText = getActivity().getResources().getString(R.string.sharing_post);

            mRefreshComments = (SwipyRefreshLayout) mView.findViewById(R.id.comments_refresher);
            mRefreshComments.setColorSchemeResources(R.color.accent, R.color.primary);
            mRefreshComments.setOnRefreshListener(this);
        }

        return mView;
    }

    private void loadComments(final boolean loadingMoreComments, final RelativeLayout loading, int startIndex, int numberOfComments) {
        CommentsDataSource.getRangeOfCommentsForPost(loading, getActivity(), startIndex, numberOfComments, mPost.getmObjectId(), new FunctionCallback<ArrayList<Comment>>() {
            @Override
            public void done(ArrayList<Comment> comments, ParseException e) {
                mRefreshComments.setRefreshing(false);
                if (e == null) {
                    Log.wtf("PostCommentsFragment", "comments loaded");
                    //instantiate and set the adapter
                    if (!loadingMoreComments)
                        mComments.clear();
                    mComments.addAll(comments);
                    mCommentsAdapter.notifyDataSetChanged();
                    if (!loadingMoreComments) {
                        if (comments.size() == 0) {
                            mNoCommentsText.setVisibility(View.VISIBLE);
                        } else {
                            mNoCommentsText.setVisibility(View.GONE);
                            //the animation for the recycler view to slide in from the bottom of the view
                            TranslateAnimation trans = new TranslateAnimation(0, 0, 1000, 0);
                            trans.setDuration(500);
                            trans.setInterpolator(new DecelerateInterpolator(1.0f));
                            mRecyclerView.startAnimation(trans);
                        }

                    }
                } else if (e.getCode() == ParseException.INCORRECT_TYPE) {
                    mComments.clear();
                    mCommentsAdapter.notifyDataSetChanged();
                    mNoCommentsText.setVisibility(View.VISIBLE);
                } else {
                    mComments.clear();
                    mCommentsAdapter.notifyDataSetChanged();
                    Snackbar.make(mView, "Error", Snackbar.LENGTH_SHORT).show();
                }
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
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
        mRefreshComments.setRefreshing(false);
        if (swipyRefreshLayoutDirection == SwipyRefreshLayoutDirection.TOP)
            loadComments(false, mLoading, 0, 10);
        else
            loadComments(true, mLoading, mComments.size(), 10);
    }

    public interface CommentsInteractionListener extends Serializable {
        void pressedBackToPosts();

        void pressedCommenterProfile(User commenterPressed);
    }

    @Override
    public void commenterProfilePressed(User commenterPressed) {
        mListener.pressedCommenterProfile(commenterPressed);
    }

    @Override
    public void deletedComment(Comment comment) {
        mComments.remove(comment);
        mCommentsAdapter.notifyDataSetChanged();
    }
}