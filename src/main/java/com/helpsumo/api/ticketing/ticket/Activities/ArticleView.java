package com.helpsumo.api.ticketing.ticket.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.helpsumo.api.ticketing.R;
import com.helpsumo.api.ticketing.ticket.Adapter.ArticleComment;
import com.helpsumo.api.ticketing.ticket.ClassObjects.Articledetails;
import com.helpsumo.api.ticketing.ticket.ClassObjects.Comment;
import com.helpsumo.api.ticketing.ticket.Database.ContentProvider.ArticleContentProvider;
import com.helpsumo.api.ticketing.ticket.Database.ContentProvider.CommentContentProvider;
import com.helpsumo.api.ticketing.ticket.Database.Table.ArticleTable;
import com.helpsumo.api.ticketing.ticket.Database.Table.CommentTable;
import com.helpsumo.api.ticketing.ticket.utills.AppConstants;
import com.helpsumo.api.ticketing.ticket.utills.NetworkFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ArticleView extends AppCompatActivity {
    TextView title, date, views, description, inputlike, inputunlike, rangcount;
    RatingBar rate;
    ListView commentlist;
    RelativeLayout progressview;
    EditText postcomment;
    SharedPreferences commentupdatetime, likes, unlikes;
    ImageView like, likees, unlike, unlikees, sendcomment;
    ArrayList<Comment> arrayList = new ArrayList<>();
    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    String articleid, articletitle, articledate, articledescription, likeflag, articleview,
            articleupdatetime, likecount, unlikecount, articlerate, apky, mail;

    @Override
    protected void onCreate(Bundle savedInstanseState) {
        super.onCreate(savedInstanseState);
        setContentView(R.layout.article_view);
        Intent i = getIntent();
        articleid = i.getStringExtra("ArticleId");
        title = (TextView) findViewById(R.id.arttitle);
        date = (TextView) findViewById(R.id.date);
        views = (TextView) findViewById(R.id.views);
        description = (TextView) findViewById(R.id.description);
        rangcount = (TextView) findViewById(R.id.rangecount);
        rate = (RatingBar) findViewById(R.id.viewrating);
        progressview = (RelativeLayout) findViewById(R.id.listprogressview);
        like = (ImageView) findViewById(R.id.like);
        unlike = (ImageView) findViewById(R.id.unlike);
        likees = (ImageView) findViewById(R.id.likee);
        unlikees = (ImageView) findViewById(R.id.unlikee);
        inputlike = (TextView) findViewById(R.id.like_input);
        inputunlike = (TextView) findViewById(R.id.unlike_input);
        commentlist = (ListView) findViewById(R.id.commentlist);
        sendcomment = (ImageView) findViewById(R.id.commentsend);
        postcomment = (EditText) findViewById(R.id.postcomment);

        apky = Article.ApiKey.getString("ApiKey", "");
        mail = Article.Useremail.getString("Useremail", "");
        commentupdatetime = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String updatetime = commentupdatetime.getString("commentupdatetime", "");
        likes = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String likee = likes.getString("likes", "0");
        unlikes = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String unlikee = unlikes.getString("unlikes", "0");

        String[] projection = {ArticleTable.ID, ArticleTable.ARTICLE_ID, ArticleTable.ARTICLE_LIKE,
                ArticleTable.ARTICLE_UNLIKE, ArticleTable.CATEGORY_ID, ArticleTable.SUBCATEGORY_ID, ArticleTable.TITLE,
                ArticleTable.DATE, ArticleTable.ARTICLE_UPDATE_TIME, ArticleTable.RATING, ArticleTable.DESCRIPTION, ArticleTable.ARTICLE_LIKE_FLAG,
                ArticleTable.TOTALVIEWS, ArticleTable.STATUS, ArticleTable.COMMENT_COUNT};
        String orderBy = ArticleTable.ID + " ASC";
        Cursor c = getContentResolver().query(ArticleContentProvider.CONTENT_URI, projection, ArticleTable.ARTICLE_ID + " =?", new String[]{articleid}, orderBy);
        assert c != null;
        if (c.moveToFirst()) {
            do {
                Articledetails ls = new Articledetails();
                articletitle = c.getString(c.getColumnIndex(ArticleTable.TITLE));
                articledescription = c.getString(c.getColumnIndex(ArticleTable.DESCRIPTION));
                articleupdatetime = c.getString(c.getColumnIndex(ArticleTable.ARTICLE_UPDATE_TIME));
                articlerate = c.getString(c.getColumnIndex(ArticleTable.RATING));
                articledate = c.getString(c.getColumnIndex(ArticleTable.DATE));
                articleview = c.getString(c.getColumnIndex(ArticleTable.TOTALVIEWS));
                likecount = c.getString(c.getColumnIndex(ArticleTable.ARTICLE_LIKE));
                unlikecount = c.getString(c.getColumnIndex(ArticleTable.ARTICLE_UNLIKE));
                likeflag = c.getString(c.getColumnIndex(ArticleTable.ARTICLE_LIKE_FLAG));
            } while (c.moveToNext());
        }
        c.close();
        title.setText(articletitle);
        DateFormat df = new SimpleDateFormat("MMM d, yyyy  HH:mm", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Date startDate;
        String newDateString = "";
        try {
            startDate = sdf.parse(articledate);
            newDateString = df.format(startDate);
            System.out.println(newDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date.setText(newDateString);
        description.setText(articledescription);
        description.setFocusable(true);
        views.setText("Views (" + articleview + ")");
        rangcount.setText("(" + articlerate + ")");
        inputlike.setText(likecount + "   ");
        inputunlike.setText("   " + unlikecount);
        rate.setRating(Float.parseFloat(articlerate));
        if (articleupdatetime == null) {
            articleupdatetime = "";
        }
        Map<String, String> map = new HashMap<>();
        map.put("apikey", apky);
        map.put("action", "comment_list");
        map.put("article_id", articleid);
        map.put("modified_date", articleupdatetime);
        progressview.setVisibility(View.VISIBLE);
        if (NetworkFunction.isOnline(getApplicationContext())) {
            LoadComment load = new LoadComment();
            load.execute(map);
        } else {
            populatedata();
        }
        sendcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });
        String lk = likes.getString("likes", "");
        String unlk = unlikes.getString("unlikes", "");
        switch (likeflag) {
            case "0":
                like.setImageResource(R.drawable.like_blur);
                unlike.setImageResource(R.drawable.unlike_blur);

                break;
            case "1":
                like.setVisibility(View.GONE);
                likees.setVisibility(View.VISIBLE);
                unlike.setVisibility(View.VISIBLE);
                unlikees.setVisibility(View.GONE);

                break;
            case "2":

                unlike.setVisibility(View.GONE);
                unlikees.setVisibility(View.VISIBLE);
                like.setVisibility(View.VISIBLE);
                likees.setVisibility(View.GONE);
                break;
        }
    }

    public void likefn(View v) {
        if (like.isEnabled()) {
            Map<String, String> map = new HashMap<>();
            map.put("apikey", apky);
            map.put("action", "article_feedback");
            map.put("email", mail);
            map.put("article_id", articleid);
            map.put("rating", "1");
            if (NetworkFunction.isOnline(getApplicationContext())) {
                Ratings load = new Ratings();
                load.execute(map);
                like.setVisibility(View.GONE);
                likees.setVisibility(View.VISIBLE);
                unlike.setVisibility(View.VISIBLE);
                unlikees.setVisibility(View.GONE);
                ContentValues values = new ContentValues();
                values.put(ArticleTable.ARTICLE_LIKE_FLAG, "1");
                getContentResolver().update(ArticleContentProvider.CONTENT_URI, values, ArticleTable.ARTICLE_ID + "=?", new String[]{articleid});
            }
        } else {
        }
    }

    public void unlikefn(View v) {
        if (unlike.isEnabled()) {
            Map<String, String> map = new HashMap<>();
            map.put("apikey", apky);
            map.put("action", "article_feedback");
            map.put("email", mail);
            map.put("article_id", articleid);
            map.put("rating", "2");
            if (NetworkFunction.isOnline(getApplicationContext())) {
                Ratings load = new Ratings();
                load.execute(map);
                unlike.setVisibility(View.GONE);
                unlikees.setVisibility(View.VISIBLE);
                like.setVisibility(View.VISIBLE);
                likees.setVisibility(View.GONE);
                ContentValues values = new ContentValues();
                values.put(ArticleTable.ARTICLE_LIKE_FLAG, "2");
                getContentResolver().update(ArticleContentProvider.CONTENT_URI, values, ArticleTable.ARTICLE_ID + "=?", new String[]{articleid});
            }
        } else {
        }
    }

    void addComment() {
        boolean cancel = false;
        View focusView = null;
        String mcomment = postcomment.getText().toString();
        if (mcomment.length() < 3 || mcomment.isEmpty()) {
            postcomment.setError("Enter Comment before send");
            focusView = postcomment;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else if (!cancel) {

            Map<String, String> map = new HashMap<>();
            map.put("article_id", articleid);
            map.put("apikey", apky);
            map.put("email", mail);
            map.put("action", "comment_add");
            map.put("article_comments", postcomment.getText().toString());
            if (NetworkFunction.isOnline(getApplicationContext())) {
                CommentAddServer add = new CommentAddServer();
                add.execute(map);
                postcomment.setText("");
            }
        }

    }

    public class CommentAddServer extends AsyncTask<Map<String, String>, Void, Boolean> {

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(Map<String, String>... maps) {
            try {
                Map<String, String> paramMap = maps[0];
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("article_id", paramMap.get("article_id"));
                    jsonObject.put("email", paramMap.get("email"));
                    jsonObject.put("apikey", paramMap.get("apikey"));
                    jsonObject.put("action", paramMap.get("action"));
                    jsonObject.put("article_comments", paramMap.get("article_comments"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                JSONObject ParentObj = new JSONObject();
                try {
                    ParentObj.put("Current Location", jsonArray);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String jsonStr = jsonObject.toString();
                System.out.println("jsonString: " + jsonStr);
                String url = AppConstants.url + "article";
                String response = null;
                try {
                    response = doPostRequest(url, jsonStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("response", "article comment: " + response.replaceAll("\\s", " "));
                if (response.length() != 0) {
                    JSONObject obj = new JSONObject(response);
                    if (obj.has("SUCCESS")) {
                        final String a = obj.getString("SUCCESS");
                        ArticleView.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), a, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    if (obj.has("ERROR")) {
                        final String a = obj.getString("ERROR");
                        ArticleView.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), a, Toast.LENGTH_LONG).show();
                            }
                        });
                        return false;
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                postcomment.setText("");
            } else {
            }
        }
    }

    public class Ratings extends AsyncTask<Map<String, String>, Void, Boolean> {
        @SafeVarargs
        @Override
        protected final Boolean doInBackground(Map<String, String>... maps) {
            try {
                Map<String, String> paramMap = maps[0];
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("article_id", paramMap.get("article_id"));
                    jsonObject.put("email", paramMap.get("email"));
                    jsonObject.put("apikey", paramMap.get("apikey"));
                    jsonObject.put("action", paramMap.get("action"));
                    jsonObject.put("rating", paramMap.get("rating"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                JSONObject ParentObj = new JSONObject();
                try {
                    ParentObj.put("Current Location", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String jsonStr = jsonObject.toString();
                System.out.println("jsonString: " + jsonStr);
                String url = AppConstants.url + "article";
                String response = null;
                try {
                    response = doPostRequest(url, jsonStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("response", "article like: " + response.replaceAll("\\s", " "));
                if (response.length() != 0) {
                    JSONObject obj = new JSONObject();
                    if (obj.has("SUCCESS")) {
                        final String a = obj.getString("SUCCESS");
                        ArticleView.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), a, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
            } else {
            }
        }
    }

    public class LoadComment extends AsyncTask<Map<String, String>, Void, Boolean> {

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(Map<String, String>... maps) {
            try {
                Map<String, String> paramMap = maps[0];
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apikey", paramMap.get("apikey"));
                    jsonObject.put("action", paramMap.get("action"));
                    jsonObject.put("article_id", paramMap.get("article_id"));
                    jsonObject.put("modified_date", paramMap.get("modified_date"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonArray = new JSONArray();

                jsonArray.put(jsonObject);

                JSONObject ParentObj = new JSONObject();
                try {
                    ParentObj.put("Current Location", jsonArray);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String jsonStr = jsonObject.toString();
                System.out.println("jsonString: " + jsonStr);
                String url = AppConstants.url + "article";
                String response = null;
                try {
                    response = doPostRequest(url, jsonStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    Log.e("view", "response: " + response.replaceAll("\\s", " "));
                    String articlecomment = "", date = "", firstname = "", lastname = "", modifiedtime = "";
                    JSONObject jobj = new JSONObject(response.replaceAll("\\s", " "));
                    JSONObject resobj = jobj.getJSONObject("response");
                    if (jobj.has("ERROR")) {
                        final String toastmsg = jobj.getString("ERROR");
                        ArticleView.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), toastmsg, Toast.LENGTH_LONG).show();
                                progressview.setVisibility(View.GONE);
                            }
                        });
                        return false;
                    } else {
                        JSONArray array = resobj.optJSONArray("ARTICLE_COMMENT");
                        modifiedtime = resobj.getString("CurrentDateTime");
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONObject jObject = array.getJSONObject(i);
                                Iterator<String> keys = jObject.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    key.replaceAll("\\s", " ");
                                    String value = jObject.getString(key);
                                    if (key.equalsIgnoreCase("article_comments")) {
                                        articlecomment = value;
                                    } else if (key.equalsIgnoreCase("date")) {
                                        date = value;
                                    } else if (key.equalsIgnoreCase("first_name")) {
                                        firstname = value;
                                    } else if (key.equalsIgnoreCase("last_name")) {
                                        lastname = value;
                                    }
                                }
                                ContentValues values = new ContentValues();
                                values.put(CommentTable.TICKET_LOG, "");
                                values.put(CommentTable.LOG_MESSAGE, articlecomment);
                                values.put(CommentTable.LOG_STATUS, "");
                                values.put(CommentTable.LOG_ATTACHMENT, "");
                                values.put(CommentTable.LOG_DATE, date);
                                values.put(CommentTable.LOG_NAME, firstname + lastname);
                                values.put(CommentTable.TICKET_ID, articleid);
                                values.put(CommentTable.COMMENT_TYPE, "");
                                getContentResolver().insert(CommentContentProvider.CONTENT_URI, values);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                        ContentValues value = new ContentValues();
                        value.put(ArticleTable.ARTICLE_UPDATE_TIME, modifiedtime);
                        getContentResolver().update(ArticleContentProvider.CONTENT_URI, value,
                                ArticleTable.ARTICLE_ID + " = ?", new String[]{String.valueOf(articleid)});
                        SharedPreferences.Editor editor = commentupdatetime.edit();
                        editor.putString("commentupdatetime", modifiedtime);
                        editor.apply();
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                populatedata();
            }
        }

    }

    void populatedata() {
        String[] projection = new String[]{
                CommentTable.ID, CommentTable.TICKET_LOG, CommentTable.LOG_MESSAGE, CommentTable.LOG_STATUS, CommentTable.COMMENT_TYPE,
                CommentTable.LOG_NAME, CommentTable.TICKET_ID, CommentTable.LOG_DATE, CommentTable.LOG_ATTACHMENT};
        String orderBy = CommentTable.ID + " ASC";
        Cursor c = getContentResolver().query(CommentContentProvider.CONTENT_URI, projection, CommentTable.TICKET_ID + " = ? ", new String[]{articleid}, orderBy);
        assert c != null;
        if (c.moveToFirst()) {
            do {
                Comment ls = new Comment();
                ls.setName(c.getString(c.getColumnIndex(CommentTable.LOG_NAME)));
                ls.setDate(c.getString(c.getColumnIndex(CommentTable.LOG_DATE)));
                ls.setMessage(c.getString(c.getColumnIndex(CommentTable.LOG_MESSAGE)));
                arrayList.add(ls);
            } while (c.moveToNext());
        }
        c.close();
        ArticleComment adapter = new ArticleComment(ArticleView.this, arrayList);
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, commentlist);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) commentlist.getLayoutParams();
        int height = totalHeight;
        lp.height = height;
        commentlist.setLayoutParams(lp);
        commentlist.setAdapter(adapter);
        progressview.setVisibility(View.GONE);
    }

    String doPostRequest(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
