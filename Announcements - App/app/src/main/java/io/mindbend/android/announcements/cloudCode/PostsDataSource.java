package io.mindbend.android.announcements.cloudCode;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;

/**
 * Created by Akshay Pall on 21/08/2015.
 */
public class PostsDataSource {

    public static final String POST_TITLE = "title";
    public static final String POST_BODY = "body";
    public static final String POST_IMAGE = "image";
    public static final String POST_START_DATE = "postStartDate";
    public static final String POST_END_DATE = "postEndDate";
    public static final String POST_PRIORITY = "priority";
    public static final String POST_ORGANIZATION = "Organization";

    public static void getRangeOfPostsForDay (final ProgressBar loader, final Context context, int startIndex, int numberOfPosts, Date date, final FunctionCallback<ArrayList<Post>> callback){
        loader.setVisibility(View.VISIBLE);
        HashMap<String, Object> params = new HashMap<>();
        params.put("startIndex", startIndex);
        params.put("numberOfPosts", numberOfPosts);
        params.put("date", date);
        ParseCloud.callFunctionInBackground("getRangeOfPostsForDay", params, new FunctionCallback<List<ParseObject>>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                loader.setVisibility(View.GONE);
                //convert all parseobjects to posts
                ArrayList<Post> posts = new ArrayList<Post>();
                if (e == null){
                    for (ParseObject object : parseObjects){
                        posts.add(new Post(context, object));
                    }
                }
                //to allow each frag to do it specifically
                callback.done(posts, e);
            }
        });
    }
}
