package io.mindbend.android.announcements;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

import java.io.Serializable;

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
    private byte[] mPostImageBytes;
    private Bitmap mPostImageBitmap;
    //TODO: setup passing in club image for the post

    public Post(String objectId, String title, String timeSinceString, String details, String clubUsername, Bitmap imageBitmap){
        mObjectId = objectId;
        mPostTitle = title;
        mPostTimeSince = timeSinceString;
        mPostDetail = details;
        mPostClubUsername = clubUsername;
        mPostImageBitmap = imageBitmap;
    }

    public Post(ParseObject object){
        mObjectId = object.getObjectId();
        mPostTitle = object.getString(PostsDataSource.POST_TITLE);
        mPostDetail = object.getString(PostsDataSource.POST_BODY);
        mPosterOrg = new Organization(object.getParseObject(PostsDataSource.POST_ORGANIZATION));
        mPostClubUsername = mPosterOrg.getTitle();
        mPostTimeSince = object.getCreatedAt().toString();
        mPostImageBytes = null; //TODO: grab from Parse later
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
        dest.writeByteArray(mPostImageBytes);
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

    public byte[] getmPostImageBytes() {
        return mPostImageBytes;
    }

    public Bitmap getmPostImageBitmap() {
        return mPostImageBitmap;
    }
}
