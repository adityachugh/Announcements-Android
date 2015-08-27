package com.example.akshaypall.testapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String COMMENT_TEXT = "comment";
    public static final String COMMENT_USER = "createUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "S1dL5D6QCSqsC1FyYTiyS5V4Yv2zcK47TeybVtEf", "llxS4kjhRdSv46DvGi9HyPapsRaFDoU9155Nh88V");
        Log.i("test", "Parse initialized");

        HashMap<String, Object> params = new HashMap<>();
        params.put("postObjectId", "FxGS0ECxMZ");
        params.put("startIndex", 0);
        params.put("numberOfComments", 10);

        ParseCloud.callFunctionInBackground("getRangeOfCommentsForPost", params, new FunctionCallback<List<ParseObject>>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                //parseobject to comments
                Log.wtf("test", "working");
                int i = 0;
//                    Log.d("Parseobject", object.toString() + "");
//                        ParseUser user = (ParseUser) object.get(COMMENT_USER);
//                        Log.d("COMMENTS", user + ": "+object.getString("comment"));


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
