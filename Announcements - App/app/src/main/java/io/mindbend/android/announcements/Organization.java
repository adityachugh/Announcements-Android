package io.mindbend.android.announcements;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.io.Serializable;

import io.mindbend.android.announcements.cloudCode.ConfigDataSource;
import io.mindbend.android.announcements.cloudCode.OrgsDataSource;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class Organization implements Serializable, Parcelable {
    //This class is used in order to populate a gridview of organizations/clubs.
    //this is used in the discover tab and on people's profiles.
    private String mObjectId;
    private String mTitle;
    private String mDescription;
    private int mFollowers;
    private String mTag;
    private boolean mPrivateOrg;
    private boolean mNewOrg;
    private String mProfileImageURL;
    private boolean mIsChildless;
    private String mCoverImageURL;
    private String mConfigId;
    private LevelConfig mMainLevel;
    private LevelConfig mChildLevel;
    private boolean mHasAccessCode;
    private Integer mAccessCode;
    private LevelConfig mParentLevel;
    private LevelConfig mLevelConfig;
    private LevelConfig mChildConfig;
    private LevelConfig mParentConfig;


    public Organization(String objectId, String title, String description, int followers, String tag, boolean privateOrg, boolean newOrg) {
        mObjectId = objectId;
        mTitle = title;
        mDescription = description;
        mFollowers = followers;
        mTag = tag;
        mPrivateOrg = privateOrg;
        mNewOrg = newOrg;
        mProfileImageURL = "";
        mCoverImageURL = "";
        mIsChildless = false;
        mConfigId = "";
    }

    public Organization(ParseObject object) {
        try {
            object.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mObjectId = object.getObjectId();
        mTitle = object.getString(OrgsDataSource.ORG_TITLE);
        mDescription = object.getString(OrgsDataSource.ORG_DESCRIPTION);
        mFollowers = object.getInt(OrgsDataSource.ORG_FOLLOWER_COUNT);
        mPrivateOrg = object.getString(OrgsDataSource.ORG_TYPE).equals(OrgsDataSource.ORG_TYPES_PRIVATE);
        mNewOrg = OrgsDataSource.isNew(object);
        if (object.getParseFile(OrgsDataSource.ORG_PROFILE_IMAGE) != null)
            mProfileImageURL = object.getParseFile(OrgsDataSource.ORG_PROFILE_IMAGE).getUrl();
        else
            mProfileImageURL = "";

        if (object.getParseFile(OrgsDataSource.ORG_COVER_IMAGE) != null)
            mCoverImageURL = object.getParseFile(OrgsDataSource.ORG_COVER_IMAGE).getUrl();
        else
            mCoverImageURL = "";

        mTag = object.getString(OrgsDataSource.ORG_TAG);
        mIsChildless = object.get(ConfigDataSource.ORG_CHILD_LEVEL_CONFIG) == null;
        if (!mIsChildless){
            try {
                mChildConfig = new LevelConfig(object.getParseObject(ConfigDataSource.ORG_CHILD_LEVEL_CONFIG).fetchIfNeeded());
            } catch (ParseException e) {
                e.printStackTrace();
                mChildConfig = null;
            }
        }

        if (object.getParseObject(ConfigDataSource.ORG_LEVEL_CONFIG) != null){
            try {
                mLevelConfig = new LevelConfig(object.getParseObject(ConfigDataSource.ORG_LEVEL_CONFIG).fetchIfNeeded());
            } catch (ParseException e) {
                e.printStackTrace();
                mLevelConfig = null;
            }
        }

        if (object.getParseObject(ConfigDataSource.ORG_PARENT_LEVEL_CONFIG) != null) {
            try {
                mParentConfig = new LevelConfig(object.getParseObject(ConfigDataSource.ORG_PARENT_LEVEL_CONFIG).fetchIfNeeded());
            } catch (ParseException e) {
                e.printStackTrace();
                mParentConfig = null;
            }
        }

        mConfigId = object.getParseObject(ConfigDataSource.ORG_CONFIG) != null ? object.getParseObject(ConfigDataSource.ORG_CONFIG).getObjectId() : null;

        mHasAccessCode = object.getBoolean(OrgsDataSource.HAS_ACCESS_CODE);
        if (mHasAccessCode)
            mAccessCode = object.getInt("accessCode");
    }

    public static LevelConfig getLevel(String typeOfLevel, ParseObject organization) {
        ParseObject config = organization.getParseObject(typeOfLevel);
        try {
            if (config != null) {
                config.fetchIfNeeded();
                return new LevelConfig(config);
            } else {
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Organization(Parcel in) {
        mObjectId = in.readString();
        mTitle = in.readString();
        mDescription = in.readString();
        mFollowers = in.readInt();
        mTag = in.readString();

        mPrivateOrg = in.readInt() != 0;

        mNewOrg = in.readInt() != 0;

        mProfileImageURL = in.readString();

        mIsChildless = in.readInt() != 0;

        mChildLevel = (LevelConfig) in.readSerializable();
        mMainLevel = (LevelConfig) in.readSerializable();

        mConfigId = in.readString();

        mHasAccessCode = in.readInt() == 1;
        if (hasAccessCode())
            mAccessCode = in.readInt();

        mParentLevel = (LevelConfig)in.readSerializable();


        //for the configs
        mParentConfig = (LevelConfig)in.readSerializable();
        mLevelConfig = (LevelConfig)in.readSerializable();
        mChildConfig = (LevelConfig)in.readSerializable();
    }

    public String getmObjectId() {
        return mObjectId;
    }

    public String getmProfileImageURL() {
        return mProfileImageURL;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getFollowers() {
        return mFollowers;
    }

    public String getTag() {
        return mTag;
    }

    public boolean isPrivateOrg() {
        return mPrivateOrg;
    }

    public boolean isNewOrg() {
        return mNewOrg;
    }

    public boolean isChildless() {
        return mIsChildless;
    }

    //for updating an org after modifying it

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setmFollowers(int mFollowers) {
        this.mFollowers = mFollowers;
    }

    public void setmPrivateOrg(boolean mPrivateOrg) {
        this.mPrivateOrg = mPrivateOrg;
    }

    public void setmHasAccessCode(boolean mHasAccessCode) {
        this.mHasAccessCode = mHasAccessCode;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    public void setmLevel(LevelConfig mainLevel) {
        mMainLevel = mainLevel;
    }

    public LevelConfig getmMainLevel() {
        return mMainLevel;
    }

    public void setmChildLevel(LevelConfig childLevel) {
        mChildLevel = childLevel;
    }

    public LevelConfig getmChildLevel() {
        return mChildLevel;
    }

    public String getmConfigId() {
        return mConfigId;
    }

    public boolean hasAccessCode() {
        return mHasAccessCode;
    }

    public Integer getmAccessCode() {
        return mAccessCode;
    }

    public void setmParentLevel(LevelConfig mParentLevel) {
        this.mParentLevel = mParentLevel;
    }

    public LevelConfig getmParentLevel() {
        return mParentLevel;
    }

    public LevelConfig getmLevelConfig() {
        return mLevelConfig;
    }

    public LevelConfig getmChildConfig() {
        return mChildConfig;
    }

    public LevelConfig getmParentConfig() {
        return mParentConfig;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mObjectId);
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeInt(mFollowers);
        dest.writeString(mTag);
        //a boolean cannot be written and read by a parcel
        //thus an int has to be used
        //0 = false, 1 = true
        if (mPrivateOrg) dest.writeInt(1);
        else dest.writeInt(0);

        if (mNewOrg) dest.writeInt(1);
        else dest.writeInt(0);

        dest.writeString(mProfileImageURL);

        if (mIsChildless) dest.writeInt(1);
        else dest.writeInt(0);

        dest.writeSerializable(mChildLevel);
        dest.writeSerializable(mMainLevel);

        dest.writeString(mConfigId);

        if (mHasAccessCode) dest.writeInt(1);
        else dest.writeInt(0);

        if (hasAccessCode())
            dest.writeInt(mAccessCode);

        dest.writeSerializable(mParentLevel);

        dest.writeSerializable(mParentConfig);
        dest.writeSerializable(mLevelConfig);
        dest.writeSerializable(mChildConfig);
    }

    public static final Parcelable.Creator<Organization> CREATOR
            = new Parcelable.Creator<Organization>() {
        public Organization createFromParcel(Parcel in) {
            return new Organization(in);
        }

        public Organization[] newArray(int size) {
            return new Organization[size];
        }
    };

    public String getmCoverImageURL() {
        return mCoverImageURL;
    }
}
