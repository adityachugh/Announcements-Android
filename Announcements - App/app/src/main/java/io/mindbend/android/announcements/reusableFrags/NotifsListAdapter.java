package io.mindbend.android.announcements.reusableFrags;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import io.mindbend.android.announcements.Notification;
import io.mindbend.android.announcements.R;

/**
 * Created by Akshay Pall on 06/08/2015.
 */
public class NotifsListAdapter extends RecyclerView.Adapter<NotifsListAdapter.ViewHolder> implements Serializable {
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mText;
        private final LinearLayout mNotifLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            //getting all the elements part of the card, aside from the image
            mText = (TextView) itemView.findViewById(R.id.notif_text);
            mNotifLayout = (LinearLayout)itemView.findViewById(R.id.notif_list);
        }
    }

    //TODO: create private fields for the list
    private List<Notification> mNotifs;
    private Context mContext;
    private NotifInteractionListener mListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notifs_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        //TODO: pass data from notif to the elements of the feed item
        final Notification notif = mNotifs.get(i);
//        viewHolder.mText.setText(mNotifs.getText());
        viewHolder.mNotifLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.notifPressed(notif);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotifs.size();
    }

    public NotifsListAdapter (Context context, List<Notification> notifs, NotifInteractionListener listener) {
        //save the mPosts private field as what is passed in
        mContext = context;
        mNotifs = notifs;
        mListener = listener;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public interface NotifInteractionListener extends Serializable {
        void notifPressed (Notification notifPressed);
    }
}
