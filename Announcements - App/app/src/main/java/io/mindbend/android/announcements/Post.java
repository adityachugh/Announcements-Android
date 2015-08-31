package io.mindbend.android.announcements;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import com.parse.ParseObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.mindbend.android.announcements.cloudCode.PostsDataSource;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class Post implements Serializable, Parcelable {
    private String mObjectId;
    private String mPostTitle;
    private String mPostTimeSince;
    private String mPostDetail;
    private String mPostClubUsername;
    private Organization mPosterOrg;
    private String  mPostImageURL = "";
    private int mPriority;
    //TODO: setup passing in club image for the post
    //private String mUrlToPicture;

    public Post(String objectId, String title, String timeSinceString, String details, String clubUsername, String imageURL){
        mObjectId = objectId;
        mPostTitle = title;
        mPostTimeSince = timeSinceString;
        mPostDetail = details;
        mPostClubUsername = clubUsername;
        mPostImageURL = imageURL;
    }

    public Post(Context context, ParseObject object){
        mObjectId = object.getObjectId();
        mPostTitle = object.getString(PostsDataSource.POST_TITLE);
        mPostDetail = object.getString(PostsDataSource.POST_BODY);
        mPosterOrg = new Organization(object.getParseObject(PostsDataSource.POST_ORGANIZATION));
        mPostClubUsername = mPosterOrg.getTitle();
        mPostTimeSince = DateUtils.getRelativeDateTimeString(context, object.getCreatedAt().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0).toString();
        if (object.getParseFile(PostsDataSource.POST_IMAGE) != null)
            mPostImageURL = object.getParseFile(PostsDataSource.POST_IMAGE).getUrl();
        else
            mPostImageURL = "";
        mPriority = object.getInt(PostsDataSource.POST_PRIORITY);

    }

    public Post (Parcel post) {
        mObjectId = post.readString();
        mPostTitle = post.readString();
        mPostTimeSince = post.readString();
        mPostDetail = post.readString();
        mPostClubUsername = post.readString();
        mPostImageURL = post.readString();
        mPriority = post.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mObjectId);
        dest.writeString(mPostTitle);
        dest.writeString(mPostTimeSince);
        dest.writeString(mPostDetail);
        dest.writeString(mPostClubUsername);
        dest.writeString(mPostImageURL);
        dest.writeInt(mPriority);
    }

    public static final Parcelable.Creator<Post> CREATOR
            = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public Organization getmPosterOrg() {
        return mPosterOrg;
    }

    public String getmObjectId() {
        return mObjectId;
    }

    public String getmPostTitle() {
        return mPostTitle;
    }

    public String getmPostTimeSince() {
        return mPostTimeSince;
    }

    public String getmPostDetail() {
        return mPostDetail;
    }

    public String getmPostClubUsername() {
        return mPostClubUsername;
    }

    public String getmPostImageURL() {
        return mPostImageURL;
    }

    public Drawable getPriorityDrawable(Context context) {
        Drawable priorityDrawable = context.getResources().getDrawable(R.drawable.fab);
        switch (mPriority){
            case PostsDataSource.LOW_PRIORITY:
                priorityDrawable = context.getResources().getDrawable(R.drawable.low_priority_indicator);
                break;
            case PostsDataSource.MEDIUM_PRIORITY:
                priorityDrawable = context.getResources().getDrawable(R.drawable.med_priority_indicator);
                break;
            case PostsDataSource.HIGH_PRIORITY:
                priorityDrawable = context.getResources().getDrawable(R.drawable.high_priority_indicator);
                break;
        }
        return priorityDrawable;
    }
}
