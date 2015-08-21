package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.tabbedFragments.TodayFragment;

public class PostsCardsFragment extends Fragment implements Serializable, SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSTS = "posts";
    private static final String ARG_POSTS_LISTENER = "post_touch_listener";
    private static final String ARG_IS_VIEWING_STATE = "is_viewing_announcements_state";

    // TODO: Rename and change types of parameters
    private List<Post> mPosts;
    private PostsFeedAdapter mPostFeedAdapter;
    private PostsFeedAdapter.PostInteractionListener mPostTouchListener;
    private transient View mView;
    //To pass into feed adapter (dynamic sizing)
    private float mScale;
    private transient SwipeRefreshLayout mRefreshTodayPosts;
    private boolean mIsViewingState;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param posts a bundle containing a List<Post> of al posts
     * @return A new instance of fragment PostsCardsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostsCardsFragment newInstance(ArrayList<Post> posts, PostsFeedAdapter.PostInteractionListener postTouchListener, boolean isViewingState) {
        PostsCardsFragment fragment = new PostsCardsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_POSTS, posts);
        args.putSerializable(ARG_POSTS_LISTENER, postTouchListener);
        args.putBoolean(ARG_IS_VIEWING_STATE, isViewingState);
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
            mPostFeedAdapter = new PostsFeedAdapter(getActivity(), mPosts, mPostTouchListener, mIsViewingState, mScale);
            recyclerView.setAdapter(mPostFeedAdapter);

            //the animation for the recycler view to slide in from the bottom of the view
            TranslateAnimation trans = new TranslateAnimation(0, 0, 1000, 0);
            trans.setDuration(500);
            trans.setInterpolator(new DecelerateInterpolator(1.0f));
            recyclerView.startAnimation(trans);

            if (getParentFragment().getParentFragment() instanceof TodayFragment){ //so the refresher is ONLY there if the user is viewing the today posts
                mRefreshTodayPosts = (SwipeRefreshLayout)mView.findViewById(R.id.post_refresher);
                mRefreshTodayPosts.setColorSchemeResources(R.color.accent, R.color.primary);
                mRefreshTodayPosts.setOnRefreshListener(this);
            }
        }

        return mView;
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
    public void onRefresh() {
        //TODO: reload today's posts into this fragment
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 1000ms
                mRefreshTodayPosts.setRefreshing(false);
            }
        }, 1000);
    }
}
