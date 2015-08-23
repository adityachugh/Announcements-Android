package io.mindbend.android.announcements.cloudCode;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.mindbend.android.announcements.Comment;

/**
 * Created by Avik Hasija on 8/22/2015.
 */
public class CommentsDataSource {

    public static final String COMMENT_TEXT = "comment";
    public static final String COMMENT_USER = "CreateUser";

    public static void getRangeOfCommentsForPost(final RelativeLayout loadingLayout, final Context context, int startIndex, int numberOfComments, String postObjectId, final FunctionCallback<ArrayList<Comment>> callback){
        loadingLayout.setVisibility(View.VISIBLE);
        HashMap<String, Object> params = new HashMap<>();
        params.put("startIndex", startIndex);
        params.put("numberOfComments", numberOfComments);
        params.put("postObjectId", postObjectId);

        ParseCloud.callFunctionInBackground("getRangeOfCommentsForPost", params, new FunctionCallback<List<ParseObject>>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {

                //parseobject to comments
                ArrayList<Comment> comments = new ArrayList<Comment>();
                if (e == null){
                    for (ParseObject object : parseObjects){
                        comments.add(new Comment(context, object));
                    }
                }
                loadingLayout.setVisibility(View.GONE);
                callback.done(comments, e);

            }
        });
    }
}
