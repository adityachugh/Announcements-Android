package io.mindbend.android.announcements;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class Post implements Serializable, Parcelable {
    private String mObjectId;
    private String mPostTitle;
    private String mPostTimeSince;
    private String mPostDetail;
    private String mPostClubUsername;
    //TODO: setup passing in club image for the post
    //private String mUrlToPicture;

    public Post(String objectId, String title, String timeSinceString, String details, String clubUsername){
        mObjectId = objectId;
        mPostTitle = title;
        mPostTimeSince = timeSinceString;
        mPostDetail = details;
        mPostClubUsername = clubUsername;
    }

    public Post (Parcel post) {
        mObjectId = post.readString();
        mPostTitle = post.readString();
        mPostTimeSince = post.readString();
        mPostDetail = post.readString();
        mPostClubUsername = post.readString();
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
//        dest.writeString(mUrlToPicture);
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
}
