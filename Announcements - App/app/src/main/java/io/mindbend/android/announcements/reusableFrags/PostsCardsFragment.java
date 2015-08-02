package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;

public class PostsCardsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private PostsFeedAdapter mPostFeedAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostsCardsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostsCardsFragment newInstance(String param1, String param2) {
        //TODO: in the future, the arguments passed in should be a query of the posts THEMSELVES.
        PostsCardsFragment fragment = new PostsCardsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PostsCardsFragment() {
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
        View v = inflater.inflate(R.layout.fragment_posts_cards, container, false);

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.posts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //TODO: query posts from parse, pass into list, then set adapter
        List<Post> posts = new ArrayList<>();

        //THE FOLLOWING IS A FAKE TEST POST
        Post testPost1 = new Post("testID", "Test Title 1", "2 hours ago", "This is a test post with fake data", "Mindbend Studio");
        posts.add(testPost1);

        //Initialize and set the adapter
        mPostFeedAdapter = new PostsFeedAdapter(getActivity(), posts);
        recyclerView.setAdapter(mPostFeedAdapter);


        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
