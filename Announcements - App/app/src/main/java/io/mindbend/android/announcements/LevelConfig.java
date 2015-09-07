package io.mindbend.android.announcements;

import com.parse.ParseObject;

import java.io.Serializable;

import io.mindbend.android.announcements.cloudCode.ConfigDataSource;

/**
 * Created by Akshay Pall on 07/09/2015.
 */
public class LevelConfig implements Serializable {
    private String mObjectId;
    private String mLevelTitle;

    public LevelConfig(ParseObject object){
        if (object != null){
            mObjectId = object.getObjectId();
            mLevelTitle = object.getString(ConfigDataSource.LEVEL_CONFIG_TITLE);
        } else {
            mObjectId = "";
            mLevelTitle = "";
        }
    }

    public String getmObjectId() {
        return mObjectId;
    }

    public String getmLevelTitle() {
        return mLevelTitle;
    }
}
