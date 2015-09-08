package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.parse.FunctionCallback;
import com.parse.ParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.cloudCode.PostsDataSource;
import io.mindbend.android.announcements.tabbedFragments.TodayFragment;

public class PostsCardsFragment extends Fragment implements Serializable, PostOverlayFragment.PostsOverlayListener, SwipyRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSTS = "posts";
    private static final String ARG_POSTS_LISTENER = "post_touch_listener";
    private static final String ARG_POSTS_OVERLAY_LISTENER = "post_overlay_listener";
    private static final String ARG_IS_VIEWING_STATE = "is_viewing_announcements_state";
    private static final String ARG_IS_APPROVING = "is_approving_posts";

    // TODO: Rename and change types of parameters
    private List<Post> mPosts;
    private PostsFeedAdapter mPostFeedAdapter;
    private PostsFeedAdapter.PostInteractionListener mPostTouchListener;
    private PostOverlayFragment.PostsOverlayListener mPostsOverlayListener;
    private transient View mView;
    //To pass into feed adapter (dynamic sizing)
    private float mScale;

    private transient SwipyRefreshLayout mRefreshTodayPosts;
    private boolean mIsViewingState;
    private boolean mIsApproving;
    private TextView noPostsMessage;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param posts a bundle containing a List<Post> of al posts
     * @return A new instance of fragment PostsCardsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostsCardsFragment newInstance(ArrayList<Post> posts, PostsFeedAdapter.PostInteractionListener postTouchListener, boolean isViewingState, PostOverlayFragment.PostsOverlayListener postsOverlayListener, boolean isApproving) {
        PostsCardsFragment fragment = new PostsCardsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_POSTS, posts);
        args.putSerializable(ARG_POSTS_LISTENER, postTouchListener);
        args.putSerializable(ARG_POSTS_OVERLAY_LISTENER, postsOverlayListener);
        args.putBoolean(ARG_IS_VIEWING_STATE, isViewingState);
        args.putBoolean(ARG_IS_APPROVING, isApproving);
        fragment.setArguments(args);
        return fragment;
    }

    public PostsFeedAdapter getmPostFeedAdapter() {
        return mPostFeedAdapter;
    }

    public PostsCardsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosts = getArguments().getParcelableArrayList(ARG_POSTS);
            mPostTouchListener = (PostsFeedAdapter.PostInteractionListener)getArguments().getSerializable(ARG_POSTS_LISTENER);
            mIsViewingState = getArguments().getBoolean(ARG_IS_VIEWING_STATE);
            mPostsOverlayListener = (PostOverlayFragment.PostsOverlayListener)getArguments().getSerializable(ARG_POSTS_OVERLAY_LISTENER);
            mIsApproving = getArguments().getBoolean(ARG_IS_APPROVING);
            mScale = getActivity().getResources().getDisplayMetrics().density;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null){
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_posts_cards, container, false);

            RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.posts_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            //Initialize and set the adapter
            mPostFeedAdapter = new PostsFeedAdapter(getActivity(), mPosts, mPostTouchListener, mIsViewingState, mScale, mIsApproving);
            recyclerView.setAdapter(mPostFeedAdapter);

            //the animation for the recycler view to slide in from the bottom of the view
            TranslateAnimation trans = new TranslateAnimation(0, 0, 1000, 0);
            trans.setDuration(500);
            trans.setInterpolator(new DecelerateInterpolator(1.0f));
            recyclerView.startAnimation(trans);

            mRefreshTodayPosts = (SwipyRefreshLayout)mView.findViewById(R.id.post_refresher);
            mRefreshTodayPosts.setColorSchemeResources(R.color.accent, R.color.primary);

            if (getParentFragment().getParentFragment() instanceof TodayFragment) //so the refresher is ONLY there if the user is viewing the today posts
                mRefreshTodayPosts.setOnRefreshListener(this);
            else
                mRefreshTodayPosts.setEnabled(false);

            noPostsMessage = (TextView)mView.findViewById(R.id.posts_no_posts_to_load);

            updateNoPostsTextMessage();
        }

        return mView;
    }

    public void updateNoPostsTextMessage() {
        if (mPosts.size() == 0){
            noPostsMessage.setVisibility(View.VISIBLE);
        } else {
            noPostsMessage.setVisibility(View.GONE);
        }
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
    public void fullPostProfile(Post clickedPost) {

    }

    @Override
    public void onCommentsOpened(Post postPressed) {

    }

    @Override
    public void onReturnToPosts() {

    }

    @Override
    public void profileComments(Post post) {

    }

    @Override
    public void visitCommentersProfile(User commenterToBeVisited) {

    }

    @Override
    public void refreshPosts() {
        mPostsOverlayListener.refreshPosts();
    }

    @Override
    public void openOrgProfileFromPosts(Organization organization) {
        mPostsOverlayListener.openOrgProfileFromPosts(organization);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if(direction == SwipyRefreshLayoutDirection.TOP)
            refreshPosts();
        else
            loadMorePosts();

    }

    private void loadMorePosts() {
        int startIndex = mPosts.size();
        int numberOfPostsToLoad = 10;
        TodayFragment todayFragment = ((TodayFragment)getParentFragment().getParentFragment());

        PostsDataSource.getRangeOfPostsForDay(todayFragment.mLoading, getActivity(), startIndex, numberOfPostsToLoad, todayFragment.mCurrentDateSelected, new FunctionCallback<ArrayList<Post>>() {
            @Override
            public void done(ArrayList<Post> posts, ParseException e) {
                mRefreshTodayPosts.setRefreshing(false);
                if (e == null){
                    if (posts.size() > 0){
                        mPosts.addAll(posts);
                        mPostFeedAdapter.notifyDataSetChanged();
                        updateNoPostsTextMessage();
                    } else {
                        Snackbar.make(mView, R.string.no_more_posts_message, Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
