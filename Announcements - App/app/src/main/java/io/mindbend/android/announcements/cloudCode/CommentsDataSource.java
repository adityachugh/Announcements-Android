package io.mindbend.android.announcements.cloudCode;

import android.content.Context;
import android.nfc.Tag;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.mindbend.android.announcements.Comment;

/**
 * Created by Avik Hasija on 8/22/2015.
 */
public class CommentsDataSource {

    public static final String COMMENT_TEXT = "comment";
    public static final String COMMENT_USER = "createUser";

    public static void getRangeOfCommentsForPost(final RelativeLayout loadingLayout, final Context context, int startIndex, int numberOfComments, String postObjectId, final FunctionCallback<ArrayList<Comment>> callback){
        loadingLayout.setVisibility(View.VISIBLE);
        HashMap<String, Object> params = new HashMap<>();
        params.put("postObjectId", postObjectId);
        params.put("startIndex", startIndex);
        params.put("numberOfComments", numberOfComments);

        ParseCloud.callFunctionInBackground("getRangeOfCommentsForPost", params, new FunctionCallback<List<ParseObject>>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                int i = 0;
                //parseobject to comments
                ArrayList<Comment> comments = new ArrayList<>();
                if (e == null){
                    if (parseObjects != null && parseObjects.size() > 0){
                        for (ParseObject object : parseObjects){
                            comments.add(new Comment(context, object));
                        }
                    }
                }
                loadingLayout.setVisibility(View.GONE);
                callback.done(comments, e);

            }
        });
    }

    public static void postCommentAsUserOnPost(final RelativeLayout loadingLayout, final Context context, String postObjectId, String commentText, final FunctionCallback<Comment> callback){
        loadingLayout.setVisibility(View.VISIBLE);
        final HashMap<String, String> params = new HashMap<>();
        params.put("commentText", commentText);
        params.put("postObjectId", postObjectId);

        ParseCloud.callFunctionInBackground("postCommentAsUserOnPost", params, new FunctionCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                loadingLayout.setVisibility(View.GONE);
                Comment comment = null;
                if (e == null)
                    comment = new Comment(context, parseObject);
                callback.done(comment, e);
            }
        });
    }

    public static void deleteComment (final RelativeLayout loadingLayout, String commentObjectId, final FunctionCallback<Boolean> callback){
        loadingLayout.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        params.put("commentObjectId", commentObjectId);

        ParseCloud.callFunctionInBackground("deleteComment", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean success, ParseException e) {
                loadingLayout.setVisibility(View.GONE);
                if (e == null){
                    Snackbar.make(loadingLayout, "Deleted comment", Snackbar.LENGTH_SHORT).show();
                    callback.done(success, e);
                } else {
                    e.printStackTrace();
                    Snackbar.make(loadingLayout, "Error deleting comment.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}