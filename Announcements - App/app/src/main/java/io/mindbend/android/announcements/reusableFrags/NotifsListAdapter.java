package io.mindbend.android.announcements.reusableFrags;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.mindbend.android.announcements.Notification;
import io.mindbend.android.announcements.R;

/**
 * Created by Akshay Pall on 06/08/2015.
 */
public class NotifsListAdapter extends RecyclerView.Adapter<NotifsListAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mText;

        public ViewHolder(View itemView) {
            super(itemView);
            //getting all the elements part of the card, aside from the image
            mText = (TextView) itemView.findViewById(R.id.notif_text);
        }
    }

    //TODO: create private fields for the list
    private List<Notification> mNotifs;
    private Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notifs_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        //TODO: pass data from notif to the elements of the feed item
        Notification notif = mNotifs.get(i);
//        viewHolder.mText.setText(mNotifs.getText());
    }

    @Override
    public int getItemCount() {
        return mNotifs.size();
    }

    public NotifsListAdapter (Context context, List<Notification> notifs) {
        //save the mPosts private field as what is passed in
        mContext = context;
        mNotifs = notifs;
    }
}
