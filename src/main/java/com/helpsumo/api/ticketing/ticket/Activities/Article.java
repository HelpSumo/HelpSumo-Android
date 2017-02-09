package com.helpsumo.api.ticketing.ticket.Activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.helpsumo.api.ticketing.R;
import com.helpsumo.api.ticketing.ticket.Adapter.ArticleList;
import com.helpsumo.api.ticketing.ticket.ClassObjects.ArticleCategory;
import com.helpsumo.api.ticketing.ticket.ClassObjects.ArticleSubcategory;
import com.helpsumo.api.ticketing.ticket.ClassObjects.Articledetails;
import com.helpsumo.api.ticketing.ticket.Database.ContentProvider.ArticleContentProvider;
import com.helpsumo.api.ticketing.ticket.Database.ContentProvider.CommonContentProvider;
import com.helpsumo.api.ticketing.ticket.Database.Table.ArticleTable;
import com.helpsumo.api.ticketing.ticket.Database.Table.CommonTable;
import com.helpsumo.api.ticketing.ticket.utills.AppConstants;
import com.helpsumo.api.ticketing.ticket.utills.NetworkFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Article extends AppCompatActivity {
    ArrayList<ArticleCategory> ArticleCategorydata = new ArrayList<ArticleCategory>();
    ArrayList<ArticleSubcategory> ArticleSubCategorydata = new ArrayList<ArticleSubcategory>();
    ArrayList<Articledetails> artdetail = new ArrayList<>();
    Spinner category, subcategory;
    String categoryid, subcategoryid;

    RelativeLayout progressview;
    SharedPreferences articleCategoryupdatetime;
    SharedPreferences Articleupdatetime;
    static SharedPreferences ApiKey;
    static SharedPreferences Useremail;
    ListView articlelist;
    ArrayList<String> cat = new ArrayList<>();
    ArrayList<String> subcat = new ArrayList<>();
    ArrayList<String> arrayarticleid = new ArrayList<>();
    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstansestate) {
        super.onCreate(savedInstansestate);
        setContentView(R.layout.article);
        Intent i = getIntent();
        final String apikey = i.getStringExtra("Apikey");
        category = (Spinner) findViewById(R.id.category);
        subcategory = (Spinner) findViewById(R.id.subcategory);
        articlelist = (ListView) findViewById(R.id.articleslist);
        progressview = (RelativeLayout) findViewById(R.id.listprogressview);
        articleCategoryupdatetime = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String updatetime = articleCategoryupdatetime.getString("articleCategoryupdatetime", "");
        Useremail = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       // String useremail = Useremail.getString("Useremail", "");
        ApiKey = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Articleupdatetime = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String articleupdatetime = Articleupdatetime.getString("Articleupdatetime", "");

        String mail = Useremail.getString("Useremail", "");
        if (mail.equals("")) {
            if(apikey.length()==0) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setMessage("Invalid ApiKey.");
                dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.setCanceledOnTouchOutside(false);
                b.show();
            }else {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.emailview, null);
                dialogBuilder.setView(dialogView);
                final EditText edt = (EditText) dialogView.findViewById(R.id.input_mail);
                final EditText nam = (EditText) dialogView.findViewById(R.id.input_name);
                nam.setVisibility(View.GONE);
                dialogBuilder.setTitle("Enter you Email Id..");
                dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String mail = edt.getText().toString();
                        SharedPreferences.Editor editor1 = Useremail.edit();
                        editor1.putString("Useremail", mail);
                        editor1.apply();
                        AppConstants.apikey = apikey;
                        SharedPreferences.Editor editor2 = ApiKey.edit();
                        editor2.putString("ApiKey", apikey);
                        editor2.apply();
                        Intent i = new Intent(Article.this, Article.class);
                        startActivity(i);
                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.setCanceledOnTouchOutside(false);
                b.show();
            }
        } else {
            String[] projection = {ArticleTable.ID, ArticleTable.ARTICLE_ID, ArticleTable.ARTICLE_LIKE, ArticleTable.ARTICLE_UNLIKE, ArticleTable.CATEGORY_ID, ArticleTable.SUBCATEGORY_ID, ArticleTable.TITLE,
                    ArticleTable.DATE, ArticleTable.RATING, ArticleTable.DESCRIPTION, ArticleTable.TOTALVIEWS, ArticleTable.STATUS, ArticleTable.COMMENT_COUNT};
            String orderBy = ArticleTable.ID + " ASC";
            Cursor c = getContentResolver().query(ArticleContentProvider.CONTENT_URI, projection, null, null, orderBy);
            assert c != null;
            if (c.moveToFirst()) {
                do {
                    arrayarticleid.add(c.getString(c.getColumnIndex(ArticleTable.ARTICLE_ID)));
                } while (c.moveToNext());
            }
            c.close();
            String apky = ApiKey.getString("ApiKey","");
            Log.e("sdf","sdf"+apky);
            String time = articleCategoryupdatetime.getString("articleCategoryupdatetime", "");
            Map<String, String> map1 = new HashMap<>();
            map1.put("apikey", apky);
            map1.put("action", "list");
            map1.put("modified_date", time);
            if (NetworkFunction.isOnline(getApplicationContext())) {
                SpinnerValueFromServer spin = new SpinnerValueFromServer();
                spin.execute(map1);
            } else {
                addview();
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("apikey", apky);
            map.put("action", "list");
            map.put("email" ,mail);
            map.put("modified_date", articleupdatetime);
            progressview.setVisibility(View.VISIBLE);
            if (NetworkFunction.isOnline(getApplicationContext())) {
                LoadArticle load = new LoadArticle();
                load.execute(map);
            } else {
                viewArticle();
            }
            category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    categoryid = ArticleCategorydata.get(pos).getCatgryid();
                    if (categoryid.equals("0")) {
                        subcat.clear();
                        String[] projectionjob = {
                                CommonTable.COLUMN_ID, CommonTable.COLUMN_NAME, CommonTable.COLUMN_SERVERID, String.valueOf(CommonTable.COLUMN_TYPE)};
                        String orderBy = CommonTable.COLUMN_ID + " ASC";
                        Cursor cursor = getContentResolver().query(CommonContentProvider.CONTENT_URI, projectionjob, CommonTable.COLUMN_PARENT_ID + " = ?", new String[]{"1"}, orderBy);
                        subcat.clear();
                        ArticleSubCategorydata.clear();
                        if (cursor != null && cursor.moveToFirst()) {
                            do {
                                ArticleSubcategory ls = new ArticleSubcategory();
                                ls.setSubcatgryidId(cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_SERVERID)));
                                ls.setSubcatgryame(cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_NAME)));
                                subcat.add(cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_NAME)));
                                ArticleSubCategorydata.add(ls);
                            }
                            while (cursor.moveToNext());
                        }
                        ArrayAdapter<String> priorAdapter = new ArrayAdapter<String>(Article.this, android.R.layout.simple_spinner_item, subcat);
                        priorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        subcategory.setAdapter(priorAdapter);
                    } else {
                        ArticleSubCategorydata.clear();
                        String[] projectionjob = {
                                CommonTable.COLUMN_ID, CommonTable.COLUMN_NAME, CommonTable.COLUMN_SERVERID, String.valueOf(CommonTable.COLUMN_TYPE)};
                        String orderBy = CommonTable.COLUMN_ID + " ASC";
                        Cursor cursor = getContentResolver().query(CommonContentProvider.CONTENT_URI, projectionjob, CommonTable.COLUMN_PARENT_ID + " = ?", new String[]{categoryid}, orderBy);
                        subcat.clear();
                        if (cursor != null && cursor.moveToFirst()) {
                            do {
                                ArticleSubcategory ls = new ArticleSubcategory();
                                ls.setSubcatgryidId(cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_SERVERID)));
                                ls.setSubcatgryame(cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_NAME)));
                                subcat.add(cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_NAME)));
                                ArticleSubCategorydata.add(ls);
                            }
                            while (cursor.moveToNext());
                        }
                    }
                    ArrayAdapter<String> priorAdapter = new ArrayAdapter<String>(Article.this, android.R.layout.simple_spinner_item, subcat);
                    priorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subcategory.setAdapter(priorAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
            subcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    try {
                        subcategoryid = ArticleSubCategorydata.get(pos).getSubcatgryid();
                        if (subcategoryid.equals("0")) {
                            artdetail.clear();
                            String[] projection = {ArticleTable.ID, ArticleTable.ARTICLE_ID, ArticleTable.ARTICLE_LIKE, ArticleTable.ARTICLE_UNLIKE, ArticleTable.CATEGORY_ID, ArticleTable.ARTICLE_LIKE_FLAG, ArticleTable.SUBCATEGORY_ID, ArticleTable.TITLE,
                                    ArticleTable.DATE, ArticleTable.RATING, ArticleTable.TOTALVIEWS, ArticleTable.DESCRIPTION, ArticleTable.STATUS, ArticleTable.COMMENT_COUNT};
                            String orderBy = ArticleTable.ID + " ASC";
                            Cursor c = getContentResolver().query(ArticleContentProvider.CONTENT_URI, projection, null, null, orderBy);
                            assert c != null;
                            if (c.moveToFirst()) {
                                do {
                                    Articledetails ls = new Articledetails();
                                    ls.setArtid(c.getString(c.getColumnIndex(ArticleTable.ARTICLE_ID)));
                                    ls.setArthead((c.getString(c.getColumnIndex(ArticleTable.TITLE))));
                                    ls.setArtdescrip(c.getString(c.getColumnIndex(ArticleTable.DESCRIPTION)));
                                    ls.setArtdate(c.getString(c.getColumnIndex(ArticleTable.DATE)));
                                    ls.setArtrating(c.getString(c.getColumnIndex(ArticleTable.RATING)));
                                    ls.setArtcomment(c.getString(c.getColumnIndex(ArticleTable.COMMENT_COUNT)));
                                    ls.setArtstatus(c.getString(c.getColumnIndex(ArticleTable.STATUS)));
                                    artdetail.add(ls);
                                } while (c.moveToNext());
                            }
                            c.close();
                            ArticleList adapter = new ArticleList(Article.this, artdetail);
                            articlelist.setAdapter(adapter);
                        } else {
                            artdetail.clear();
                            String[] projection = {ArticleTable.ID, ArticleTable.ARTICLE_ID, ArticleTable.ARTICLE_LIKE, ArticleTable.ARTICLE_UNLIKE, ArticleTable.ARTICLE_LIKE_FLAG, ArticleTable.CATEGORY_ID, ArticleTable.SUBCATEGORY_ID, ArticleTable.TITLE,
                                    ArticleTable.DATE, ArticleTable.RATING, ArticleTable.TOTALVIEWS, ArticleTable.DESCRIPTION, ArticleTable.STATUS, ArticleTable.COMMENT_COUNT};
                            String orderBy = ArticleTable.ID + " ASC";
                            Cursor c = getContentResolver().query(ArticleContentProvider.CONTENT_URI, projection, ArticleTable.SUBCATEGORY_ID + " =?", new String[]{subcategoryid}, orderBy);
                            assert c != null;
                            if (c.moveToFirst()) {
                                do {
                                    Articledetails ls = new Articledetails();
                                    ls.setArtid(c.getString(c.getColumnIndex(ArticleTable.ARTICLE_ID)));
                                    ls.setArthead((c.getString(c.getColumnIndex(ArticleTable.TITLE))));
                                    ls.setArtdescrip(c.getString(c.getColumnIndex(ArticleTable.DESCRIPTION)));
                                    ls.setArtdate(c.getString(c.getColumnIndex(ArticleTable.DATE)));
                                    ls.setArtrating(c.getString(c.getColumnIndex(ArticleTable.RATING)));
                                    ls.setArtcomment(c.getString(c.getColumnIndex(ArticleTable.COMMENT_COUNT)));
                                    ls.setArtstatus(c.getString(c.getColumnIndex(ArticleTable.STATUS)));
                                    artdetail.add(ls);
                                } while (c.moveToNext());
                            }
                            c.close();
                            ArticleList adapter = new ArticleList(Article.this, artdetail);
                            articlelist.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }
    }

    public class SpinnerValueFromServer extends AsyncTask<Map<String, String>, Void, Boolean> {
        @SafeVarargs
        @Override
        protected final Boolean doInBackground(Map<String, String>... maps) {
            try {
                Map<String, String> paramMap = maps[0];
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apikey", paramMap.get("apikey"));
                    jsonObject.put("action", paramMap.get("action"));
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
                    e.printStackTrace();
                }
                String jsonStr = jsonObject.toString();
                System.out.println("jsonString: " + jsonStr);
                String url = AppConstants.url + "article_categories";
                String response = doPostRequest(url, jsonStr);
                if (response != null) {
                    Log.e("article", "response: " + response.replaceAll("\\s", " "));
                    String catid = "", catname = "", catstatus = "", catparentid = "";
                    JSONObject obj = new JSONObject(response);
                    if (obj.has("ERROR")) {
                        final String toastmsg = obj.getString("ERROR");
                        Article.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), toastmsg, Toast.LENGTH_LONG).show();
                            }
                        });
                        return false;
                    } else {
                        JSONObject listPathObject = obj.getJSONObject("response");
                        JSONArray array = listPathObject.getJSONArray("ARTICLE_CATEGORY_SUBCATEGORY");
                        if (array.length() == 0) {
                        } else {
                            int b = getContentResolver().delete(CommonContentProvider.CONTENT_URI, CommonTable.COLUMN_TYPE + "= ?", new String[]{"5"});
                            int b1 = getContentResolver().delete(CommonContentProvider.CONTENT_URI, CommonTable.COLUMN_TYPE + "= ?", new String[]{"6"});
                            ContentValues valu = new ContentValues();
                            valu.put(CommonTable.COLUMN_TYPE, 5);
                            valu.put(CommonTable.COLUMN_SERVERID, "0");
                            valu.put(CommonTable.COLUMN_NAME, "Please Select");
                            valu.put(CommonTable.COLUMN_STATUS, "");
                            valu.put(CommonTable.COLUMN_PARENT_ID, "1");
                            getContentResolver().insert(CommonContentProvider.CONTENT_URI, valu);

                            ContentValues val = new ContentValues();
                            val.put(CommonTable.COLUMN_TYPE, 6);
                            val.put(CommonTable.COLUMN_SERVERID, "0");
                            val.put(CommonTable.COLUMN_NAME, "Please Select");
                            valu.put(CommonTable.COLUMN_PARENT_ID, "1");
                            val.put(CommonTable.COLUMN_STATUS, "");
                            getContentResolver().insert(CommonContentProvider.CONTENT_URI, val);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jobj = array.getJSONObject(i);
                                catid = jobj.getString("category_id");
                                catname = jobj.getString("category_title");
                                catstatus = jobj.getString("status");
                                catparentid = jobj.getString("parent_id");

                                if (catparentid.equals("0")) {
                                    ContentValues values = new ContentValues();
                                    values.put(CommonTable.COLUMN_TYPE, 5);
                                    values.put(CommonTable.COLUMN_SERVERID, catid);
                                    values.put(CommonTable.COLUMN_NAME, catname);
                                    values.put(CommonTable.COLUMN_STATUS, catstatus);
                                    values.put(CommonTable.COLUMN_PARENT_ID, catparentid);
                                    getContentResolver().insert(CommonContentProvider.CONTENT_URI, values);
                                } else {
                                    ContentValues values = new ContentValues();
                                    values.put(CommonTable.COLUMN_TYPE, 6);
                                    values.put(CommonTable.COLUMN_SERVERID, catid);
                                    values.put(CommonTable.COLUMN_NAME, catname);
                                    values.put(CommonTable.COLUMN_STATUS, catstatus);
                                    values.put(CommonTable.COLUMN_PARENT_ID, catparentid);
                                    getContentResolver().insert(CommonContentProvider.CONTENT_URI, values);
                                }
                            }
                        }
                        String time = listPathObject.getString("CurrentDateTime");
                        SharedPreferences.Editor editor = articleCategoryupdatetime.edit();
                        editor.putString("articleCategoryupdatetime", time);
                        editor.apply();
                    }
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                addview();
            } else {
            }
        }
    }

    public class LoadArticle extends AsyncTask<Map<String, String>, Void, Boolean> {
        @SafeVarargs
        @Override
        protected final Boolean doInBackground(Map<String, String>... maps) {
            try {
                Map<String, String> paramMap = maps[0];
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apikey", paramMap.get("apikey"));
                    jsonObject.put("action", paramMap.get("action"));
                    jsonObject.put("email",paramMap.get("email"));
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
                    e.printStackTrace();
                }
                String jsonStr = jsonObject.toString();
                System.out.println("jsonString: " + jsonStr);
                String url = AppConstants.url + "article";
                String response = doPostRequest(url, jsonStr);
                if (response != null) {
                    Log.e("response", "article: " + response.replaceAll("\\s", " "));
                    JSONObject obj = new JSONObject(response);
                    if (obj.has("ERROR")) {
                        final String toastmsg = obj.getString("ERROR");
                        Article.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), toastmsg, Toast.LENGTH_LONG).show();
                                progressview.setVisibility(View.GONE);
                            }
                        });
                        return false;
                    } else {
                        JSONObject listPathObject = obj.getJSONObject("response");
                        JSONArray array = listPathObject.getJSONArray("ARTICLE");
                        try {
                            for (int i = 0; i < array.length(); i++) {
                                String artid = "", artcat = "", artsub = "", arttitle = "", artdescrp = "",
                                        arttotalview = "", like = "", unlike = "",likeflag = "", commentcount = "", artdate = "";
                                JSONObject jObject = array.getJSONObject(i);
                                Iterator<String> keys = jObject.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    key.replaceAll("\\s", " ");
                                    String value = jObject.getString(key);
                                    if (key.equalsIgnoreCase("article_id")) {
                                        artid = value;
                                    } else if (key.equalsIgnoreCase("category_id")) {
                                        artcat = value;
                                    } else if (key.equalsIgnoreCase("subcategory_id")) {
                                        artsub = value;
                                    } else if (key.equalsIgnoreCase("article_title")) {
                                        arttitle = value;
                                    } else if (key.equalsIgnoreCase("article_description")) {
                                        artdescrp = value;
                                    } else if (key.equalsIgnoreCase("totalviews")) {
                                        arttotalview = value;
                                    } else if (key.equalsIgnoreCase("like")) {
                                        like = value;
                                    } else if (key.equalsIgnoreCase("unlike")) {
                                        unlike = value;
                                    } else if (key.equalsIgnoreCase("modified_at")) {
                                        artdate = value;
                                    } else if (key.equalsIgnoreCase("comment")) {
                                        commentcount = value;
                                    } else if (key.equalsIgnoreCase("check")) {
                                        likeflag = value;

                                    }
                                }
                                if (arrayarticleid.contains(artid)) {
                                    ContentValues values = new ContentValues();
                                    values.put(ArticleTable.ARTICLE_ID, artid);
                                    values.put(ArticleTable.CATEGORY_ID, artcat);
                                    values.put(ArticleTable.SUBCATEGORY_ID, artsub);
                                    values.put(ArticleTable.TITLE, arttitle);
                                    values.put(ArticleTable.DESCRIPTION, artdescrp);
                                    values.put(ArticleTable.STATUS, "");
                                    values.put(ArticleTable.ARTICLE_LIKE, like);
                                    values.put(ArticleTable.ARTICLE_UNLIKE, unlike);
                                    int lik = Integer.parseInt(like);
                                    int unlik = Integer.parseInt(unlike);
                                    float sum = lik + unlik;
                                    if (sum == 0) {
                                        values.put(ArticleTable.RATING, sum);
                                    } else {
                                        float average = ((lik / sum) * 5);
                                        BigDecimal bd = new BigDecimal(Float.toString(average));
                                        bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                                        values.put(ArticleTable.RATING, String.valueOf(bd));
                                    }
                                    values.put(ArticleTable.TOTALVIEWS, arttotalview);
                                    values.put(ArticleTable.COMMENT_COUNT, commentcount);
                                    values.put(ArticleTable.DATE, artdate);
                                    values.put(ArticleTable.ARTICLE_LIKE_FLAG, likeflag);
                                    getContentResolver().update(ArticleContentProvider.CONTENT_URI, values, ArticleTable.ARTICLE_ID + " =?", new String[]{artid});
                                } else {
                                    ContentValues values = new ContentValues();
                                    values.put(ArticleTable.ARTICLE_ID, artid);
                                    values.put(ArticleTable.CATEGORY_ID, artcat);
                                    values.put(ArticleTable.SUBCATEGORY_ID, artsub);
                                    values.put(ArticleTable.TITLE, arttitle);
                                    values.put(ArticleTable.DESCRIPTION, artdescrp);
                                    values.put(ArticleTable.STATUS, "");
                                    values.put(ArticleTable.ARTICLE_LIKE, like);
                                    values.put(ArticleTable.ARTICLE_UNLIKE, unlike);
                                    int lik = Integer.parseInt(like);
                                    int unlik = Integer.parseInt(unlike);
                                    float sum = lik + unlik;
                                    if (sum == 0) {
                                        values.put(ArticleTable.RATING, sum);
                                    } else {
                                        float average = ((lik / sum) * 5);
                                        BigDecimal bd = new BigDecimal(Float.toString(average));
                                        bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                                        values.put(ArticleTable.RATING, String.valueOf(bd));
                                    }
                                    values.put(ArticleTable.TOTALVIEWS, arttotalview);
                                    values.put(ArticleTable.COMMENT_COUNT, commentcount);
                                    values.put(ArticleTable.DATE, artdate);
                                    values.put(ArticleTable.ARTICLE_LIKE_FLAG, likeflag);
                                    getContentResolver().insert(ArticleContentProvider.CONTENT_URI, values);
                                }
                            }
                            String date = listPathObject.getString("CurrentDateTime");
                            SharedPreferences.Editor editor = Articleupdatetime.edit();
                            editor.putString("Articleupdatetime", date);
                            editor.apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public void onPostExecute(final Boolean success) {
            if (success) {
                viewArticle();
            } else {
            }
        }
    }

    void addview() {
        String[] projectionjob = {
                CommonTable.COLUMN_ID, CommonTable.COLUMN_NAME, CommonTable.COLUMN_SERVERID, String.valueOf(CommonTable.COLUMN_TYPE)};
        String orderBy = CommonTable.COLUMN_ID + " ASC";
        Cursor cursor = getContentResolver().query(CommonContentProvider.CONTENT_URI, projectionjob, CommonTable.COLUMN_TYPE + " = ?", new String[]{"5"}, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ArticleCategory ls = new ArticleCategory();
                ls.setCatgryidId(cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_SERVERID)));
                ls.setCatgryame(cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_NAME)));
                cat.add(cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_NAME)));
                ArticleCategorydata.add(ls);
            }
            while (cursor.moveToNext());
        }
        ArrayAdapter<String> departAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cat);
        departAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(departAdapter);
    }

    public void viewArticle() {
        artdetail.clear();
        String[] projection = {ArticleTable.ID, ArticleTable.ARTICLE_ID, ArticleTable.ARTICLE_UPDATE_TIME, ArticleTable.CATEGORY_ID, ArticleTable.ARTICLE_LIKE, ArticleTable.ARTICLE_UNLIKE, ArticleTable.SUBCATEGORY_ID, ArticleTable.TITLE,
                ArticleTable.DATE, ArticleTable.RATING, ArticleTable.ARTICLE_LIKE_FLAG, ArticleTable.TOTALVIEWS, ArticleTable.DESCRIPTION, ArticleTable.STATUS, ArticleTable.COMMENT_COUNT};
        String orderBy = ArticleTable.ID + " ASC";
        Cursor c = getContentResolver().query(ArticleContentProvider.CONTENT_URI, projection, null, null, orderBy);
        assert c != null;
        if (c.moveToFirst()) {
            do {
                Articledetails ls = new Articledetails();
                ls.setArtid(c.getString(c.getColumnIndex(ArticleTable.ARTICLE_ID)));
                ls.setArthead((c.getString(c.getColumnIndex(ArticleTable.TITLE))));
                ls.setArtdescrip(c.getString(c.getColumnIndex(ArticleTable.DESCRIPTION)));
                ls.setArtdate(c.getString(c.getColumnIndex(ArticleTable.DATE)));
                ls.setArtrating(c.getString(c.getColumnIndex(ArticleTable.RATING)));
                ls.setArtcomment(c.getString(c.getColumnIndex(ArticleTable.COMMENT_COUNT)));
                ls.setArtstatus(c.getString(c.getColumnIndex(ArticleTable.STATUS)));
                artdetail.add(ls);
            } while (c.moveToNext());
        }
        c.close();
        ArticleList adapter = new ArticleList(Article.this, artdetail);
        articlelist.setAdapter(adapter);
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
