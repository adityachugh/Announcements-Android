package io.mindbend.android.announcements.reusableFrags;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.mindbend.android.announcements.R;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class PostsFeedAdapter extends RecyclerView.Adapter<PostsFeedAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder{

        //TODO: create private fields for the elements within a single feed item

        public ViewHolder(View itemView){
            super(itemView);
            //set up card
        }
    }

    //TODO: create private fields for the list
    private List<Post> mPosts;
    private Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_feed_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0; //TODO: return the list.size()
    }

    public PostsFeedAdapter(Context context, posts){
        //save the LIST private field as what is passed in!
        mContext = context;
        mPosts = posts;
    }
}
