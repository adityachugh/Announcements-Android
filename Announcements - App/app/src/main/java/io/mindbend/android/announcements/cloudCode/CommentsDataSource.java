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

import io.mindbend.android.announcements.App;
import io.mindbend.android.announcements.Comment;
import io.mindbend.android.announcements.R;

/**
 * Created by Avik Hasija on 8/22/2015.
 */
public class CommentsDataSource {

    public static final String COMMENT_TEXT = "comment";
    public static final String COMMENT_USER = "createUser";

    public static void getRangeOfCommentsForPost(final View view, final RelativeLayout loadingLayout, int viewToRemoveId, final Context context, int startIndex, int numberOfComments, String postObjectId, final FunctionCallback<ArrayList<Comment>> callback){
        loadingLayout.setVisibility(View.VISIBLE);
        final View layoutView = view.findViewById(viewToRemoveId);
        layoutView.setVisibility(View.INVISIBLE);

        if (!App.hasNetworkConnection(context)){
            loadingLayout.setVisibility(View.GONE);
            layoutView.setVisibility(View.VISIBLE);
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("postObjectId", postObjectId);
            params.put("startIndex", startIndex);
            params.put("numberOfComments", numberOfComments);

            ParseCloud.callFunctionInBackground("getRangeOfCommentsForPost", params, new FunctionCallback<List<ParseObject>>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    int i = 0;
                    loadingLayout.setVisibility(View.GONE);
                    layoutView.setVisibility(View.VISIBLE);
                    //parseobject to comments
                    ArrayList<Comment> comments = new ArrayList<>();
                    if (e == null){
                        if (parseObjects != null && parseObjects.size() > 0){
                            for (ParseObject object : parseObjects){
                                comments.add(new Comment(context, object));
                            }
                            callback.done(comments, e);
                        }
                    }

                    else { //e == null
                        if (e.getCode() == ParseException.INCORRECT_TYPE)
                            Snackbar.make(view, R.string.no_more_comments_message, Snackbar.LENGTH_SHORT).show();
                        else
                            Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();

                        e.printStackTrace();

                    }

                }
            });
        }

    }

    public static void postCommentAsUserOnPost(final View view, final RelativeLayout loadingLayout, final Context context, String postObjectId, String commentText, final FunctionCallback<Comment> callback){
        loadingLayout.setVisibility(View.VISIBLE);

        if (!App.hasNetworkConnection(context)){
            loadingLayout.setVisibility(View.GONE);
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
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
                    else {
                        Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                    callback.done(comment, e);
                }
            });
        }

    }

    public static void deleteComment (final View view, Context context, final RelativeLayout loadingLayout, String commentObjectId, final FunctionCallback<Boolean> callback){
        loadingLayout.setVisibility(View.VISIBLE);

        if (!App.hasNetworkConnection(context)){
            loadingLayout.setVisibility(View.GONE);
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put("commentObjectId", commentObjectId);

            ParseCloud.callFunctionInBackground("deleteComment", params, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean success, ParseException e) {
                    loadingLayout.setVisibility(View.GONE);
                    if (e == null) {
                        Snackbar.make(view, "Deleted comment", Snackbar.LENGTH_SHORT).show();
                        callback.done(success, e);
                    } else {
                        e.printStackTrace();
                        Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}