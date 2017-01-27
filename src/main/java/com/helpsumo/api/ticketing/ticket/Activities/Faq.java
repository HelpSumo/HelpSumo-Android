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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.helpsumo.api.ticketing.R;
import com.helpsumo.api.ticketing.ticket.Adapter.FaqAdapter;
import com.helpsumo.api.ticketing.ticket.ClassObjects.FaqCategoryDropdown;
import com.helpsumo.api.ticketing.ticket.ClassObjects.Faqdetails;
import com.helpsumo.api.ticketing.ticket.Database.ContentProvider.CommonContentProvider;
import com.helpsumo.api.ticketing.ticket.Database.ContentProvider.FaqContentProvider;
import com.helpsumo.api.ticketing.ticket.Database.Table.CommonTable;
import com.helpsumo.api.ticketing.ticket.Database.Table.FaqTable;
import com.helpsumo.api.ticketing.ticket.HelpsumoConfig;
import com.helpsumo.api.ticketing.ticket.utills.AppConstants;
import com.helpsumo.api.ticketing.ticket.utills.NetworkFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Faq extends AppCompatActivity {
    HelpsumoConfig hConfig;
    Spinner faqcategory;
    RelativeLayout progressview;
    ExpandableListView faqlist;
    SharedPreferences Categoryupdatetime, Faqupdatetime, ApikeY;
    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    ArrayList<Faqdetails> list = new ArrayList<>();
    List<String> Categorynamelist = new ArrayList<String>();
    List<String> Categoryidlist = new ArrayList<String>();
    ArrayList<String> arrayfaqid = new ArrayList<>();
    ArrayList<FaqCategoryDropdown> FaqCategorydata = new ArrayList<FaqCategoryDropdown>();

    @Override
    protected void onCreate(Bundle savedInstanseState) {
        super.onCreate(savedInstanseState);
        setContentView(R.layout.faq_view);
        Intent i = getIntent();
        final String apikey = i.getStringExtra("Apikey");
        faqcategory = (Spinner) findViewById(R.id.category);
        faqlist = (ExpandableListView) findViewById(R.id.faqlist);
        progressview = (RelativeLayout) findViewById(R.id.listprogressview);
        Categoryupdatetime = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String updatetime = Categoryupdatetime.getString("Categoryupdatetime", "");
        Faqupdatetime = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String faqupdatetime = Faqupdatetime.getString("Faqupdatetime", "");
        ApikeY = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String apkey = ApikeY.getString("ApikeY", "");
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
            SharedPreferences.Editor editor = ApikeY.edit();
            editor.putString("ApikeY", apikey);
            editor.apply();

            String[] projection = {FaqTable.ID, FaqTable.FAQ_ID, FaqTable.CATEGORY_ID, FaqTable.QUESTIONS,
                    FaqTable.ANSWERS, FaqTable.STATUS};
            String orderBy = FaqTable.ID + " ASC";
            Cursor c1 = getContentResolver().query(FaqContentProvider.CONTENT_URI, projection, null, null, orderBy);
            if (c1.moveToFirst()) {
                do {
                    arrayfaqid.add(c1.getString(c1.getColumnIndex(FaqTable.FAQ_ID)));
                } while (c1.moveToNext());
            }
            String apky = ApikeY.getString("ApikeY", "");
//category spool
            Map<String, String> map1 = new HashMap<>();
            map1.put("apikey", apky);
            map1.put("action", "list");
            map1.put("modified_date", updatetime);
            if (NetworkFunction.isOnline(getApplicationContext())) {
                SpinnerFromServer spin = new SpinnerFromServer();
                spin.execute(map1);
            } else {
                addview();
            }

//faq spool
            Map<String, String> map = new HashMap<String, String>();
            map.put("apikey", apky);
            map.put("action", "list");
            map.put("modified_date", faqupdatetime);
            progressview.setVisibility(View.VISIBLE);
            if (NetworkFunction.isOnline(getApplicationContext())) {
                LoadFaq load = new LoadFaq();
                load.execute(map);
            } else {
                viewFaq();
            }
        }
        faqcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String catid = FaqCategorydata.get(pos).getCatgryid();
                String a = Categoryidlist.get(pos);
                if (a.equals("0") || catid.equals("0")) {
                    list.clear();
                    String[] projection = {FaqTable.ID, FaqTable.FAQ_ID, FaqTable.CATEGORY_ID, FaqTable.QUESTIONS,
                            FaqTable.ANSWERS, FaqTable.STATUS};
                    String orderBy = FaqTable.ID + " ASC";
                    Cursor c = getContentResolver().query(FaqContentProvider.CONTENT_URI, projection, null, null, orderBy);
                    if (c.moveToFirst()) {
                        do {
                            Faqdetails ls = new Faqdetails();
                            ls.setFaq(c.getString(c.getColumnIndex(FaqTable.QUESTIONS)));
                            ls.setFaqanswer(c.getString(c.getColumnIndex(FaqTable.ANSWERS)));
                            ls.setStatus(c.getString(c.getColumnIndex(FaqTable.STATUS)));
                            ls.setFaqid(c.getString(c.getColumnIndex(FaqTable.FAQ_ID)));
                            list.add(ls);
                        } while (c.moveToNext());
                    }
                    c.close();
                    FaqAdapter adapter = new FaqAdapter(Faq.this, list);
                    faqlist.setAdapter(adapter);
                } else {
                    list.clear();
                    String[] projection = {FaqTable.ID, FaqTable.FAQ_ID, FaqTable.CATEGORY_ID, FaqTable.QUESTIONS,
                            FaqTable.ANSWERS, FaqTable.STATUS};
                    String orderBy = FaqTable.ID + " ASC";
                    Cursor c = getContentResolver().query(FaqContentProvider.CONTENT_URI, projection, FaqTable.CATEGORY_ID + " =?", new String[]{a}, orderBy);
                    assert c != null;
                    if (c.moveToFirst()) {
                        do {
                            Faqdetails ls = new Faqdetails();
                            ls.setFaq(c.getString(c.getColumnIndex(FaqTable.QUESTIONS)));
                            ls.setFaqanswer(c.getString(c.getColumnIndex(FaqTable.ANSWERS)));
                            ls.setStatus(c.getString(c.getColumnIndex(FaqTable.STATUS)));
                            ls.setFaqid(c.getString(c.getColumnIndex(FaqTable.FAQ_ID)));
                            list.add(ls);
                        } while (c.moveToNext());
                    }
                    c.close();
                    FaqAdapter adapter = new FaqAdapter(Faq.this, list);
                    faqlist.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public class SpinnerFromServer extends AsyncTask<Map<String, String>, Void, Boolean> {
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
                String url = AppConstants.url + "faqcategory";
                String response = null;
                try {
                    response = doPostRequest(url, jsonStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    Log.e("response", "faq: " + response.replaceAll("\\s", " "));
                    String catid = "", catname = "", catstatus = "";
                    JSONObject obj = new JSONObject(response);
                    if (obj.has("ERROR")) {
                        final String toastmsg = obj.getString("ERROR");
                        Faq.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), toastmsg, Toast.LENGTH_LONG).show();
                                progressview.setVisibility(View.GONE);
                            }
                        });
                        return false;
                    } else {
                        JSONObject listPathObject = obj.getJSONObject("response");
                        JSONArray array = listPathObject.getJSONArray("FAQ_CATEGORY");
                        if (array.length() == 0) {
                        } else {
                            int b = getContentResolver().delete(CommonContentProvider.CONTENT_URI, CommonTable.COLUMN_TYPE + "= ?", new String[]{"4"});
                            ContentValues valu = new ContentValues();
                            valu.put(CommonTable.COLUMN_TYPE, 4);
                            valu.put(CommonTable.COLUMN_SERVERID, "0");
                            valu.put(CommonTable.COLUMN_NAME, "Please Select");
                            valu.put(CommonTable.COLUMN_STATUS, "");
                            getContentResolver().insert(CommonContentProvider.CONTENT_URI, valu);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jobj = array.getJSONObject(i);
                                catid = jobj.getString("category_id");
                                catname = jobj.getString("category_title");
                                catstatus = jobj.getString("status");
                                ContentValues values = new ContentValues();
                                values.put(CommonTable.COLUMN_TYPE, 4);
                                values.put(CommonTable.COLUMN_SERVERID, catid);
                                values.put(CommonTable.COLUMN_NAME, catname);
                                values.put(CommonTable.COLUMN_STATUS, catstatus);
                                getContentResolver().insert(CommonContentProvider.CONTENT_URI, values);
                            }
                        }
                        String time = listPathObject.getString("CurrentDateTime");
                        SharedPreferences.Editor editor = Categoryupdatetime.edit();
                        editor.putString("Categoryupdatetime", time);
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

    void addview() {
        Categorynamelist.clear();
        Categoryidlist.clear();
        FaqCategorydata.clear();
        String[] projectionjob = {
                CommonTable.COLUMN_ID, CommonTable.COLUMN_NAME, CommonTable.COLUMN_SERVERID, String.valueOf(CommonTable.COLUMN_TYPE), CommonTable.COLUMN_STATUS};
        String orderBy = CommonTable.COLUMN_ID + " ASC";
        Cursor cursor = getContentResolver().query(CommonContentProvider.CONTENT_URI, projectionjob, CommonTable.COLUMN_TYPE + " = ?", new String[]{"4"}, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                FaqCategoryDropdown ls = new FaqCategoryDropdown();
                ls.setCatgryidId(cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_SERVERID)));
                ls.setCatgryame(cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_NAME)));
                String status = cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_STATUS));
                if (status.equals("0")) {
                } else {
                    String a = cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_NAME));
                    Categorynamelist.add(a);
                    String b = cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_SERVERID));
                    Categoryidlist.add(b);
                }
                FaqCategorydata.add(ls);
            } while (cursor.moveToNext());
        }
        ArrayAdapter<String> departAdapter = new ArrayAdapter<String>(Faq.this, android.R.layout.simple_spinner_item, Categorynamelist);
        departAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        faqcategory.setAdapter(departAdapter);
    }

    public class LoadFaq extends AsyncTask<Map<String, String>, Void, Boolean> {
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
                String url = AppConstants.url + "faq";
                String response = null;
                try {
                    response = doPostRequest(url, jsonStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    Log.e("response", "faq: " + response.replaceAll("\\s", " "));
                    JSONObject obj = new JSONObject(response);
                    if (obj.has("ERROR")) {
                        final String toastmsg = obj.getString("ERROR");
                        Faq.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), toastmsg, Toast.LENGTH_LONG).show();
                                progressview.setVisibility(View.GONE);
                            }
                        });
                        return false;
                    } else {
                        JSONObject listPathObject = obj.getJSONObject("response");
                        JSONArray array = listPathObject.getJSONArray("FAQ");
                        try {
                            for (int i = 0; i < array.length(); i++) {
                                String faqid = "", faqquestion = "", faqanswer = "", faqstatus = "", faqcategoryid = "";
                                JSONObject jObject = array.getJSONObject(i);
                                Iterator<String> keys = jObject.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    key.replaceAll("\\s", " ");
                                    String value = jObject.getString(key);
                                    if (key.equalsIgnoreCase("faq_id")) {
                                        faqid = value;
                                    } else if (key.equalsIgnoreCase("questions")) {
                                        faqquestion = value;
                                    } else if (key.equalsIgnoreCase("answer")) {
                                        faqanswer = value;
                                    } else if (key.equalsIgnoreCase("status")) {
                                        faqstatus = value;
                                    } else if (key.equalsIgnoreCase("category_id")) {
                                        faqcategoryid = value;
                                    }
                                }
                                if (arrayfaqid.contains(faqid)) {
                                    ContentValues values = new ContentValues();
                                    values.put(FaqTable.FAQ_ID, faqid);
                                    values.put(FaqTable.QUESTIONS, faqquestion);
                                    values.put(FaqTable.ANSWERS, faqanswer);
                                    values.put(FaqTable.STATUS, faqstatus);
                                    values.put(FaqTable.CATEGORY_ID, faqcategoryid);
                                    getContentResolver().update(FaqContentProvider.CONTENT_URI, values,
                                            FaqTable.FAQ_ID + " =? ", new String[]{faqid});
                                } else {
                                    ContentValues values = new ContentValues();
                                    values.put(FaqTable.FAQ_ID, faqid);
                                    values.put(FaqTable.QUESTIONS, faqquestion);
                                    values.put(FaqTable.ANSWERS, faqanswer);
                                    values.put(FaqTable.STATUS, faqstatus);
                                    values.put(FaqTable.CATEGORY_ID, faqcategoryid);
                                    getContentResolver().insert(FaqContentProvider.CONTENT_URI, values);
                                }
                            }
                            String date = listPathObject.getString("CurrentDateTime");
                            SharedPreferences.Editor editor = Faqupdatetime.edit();
                            editor.putString("Faqupdatetime", date);
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
                viewFaq();
            } else {
            }
        }
    }

    public void viewFaq() {
        list.clear();
        String[] projection = {FaqTable.ID, FaqTable.FAQ_ID, FaqTable.CATEGORY_ID, FaqTable.QUESTIONS,
                FaqTable.ANSWERS, FaqTable.STATUS};
        String orderBy = FaqTable.ID + " ASC";
        Cursor c = getContentResolver().query(FaqContentProvider.CONTENT_URI, projection, null, null, orderBy);
        assert c != null;
        if (c.moveToFirst()) {
            do {
                Faqdetails ls = new Faqdetails();
                ls.setFaq(c.getString(c.getColumnIndex(FaqTable.QUESTIONS)));
                ls.setFaqanswer((c.getString(c.getColumnIndex(FaqTable.ANSWERS))));
                ls.setFaqid(c.getString(c.getColumnIndex(FaqTable.FAQ_ID)));
                ls.setStatus(c.getString(c.getColumnIndex(FaqTable.STATUS)));
                list.add(ls);
            } while (c.moveToNext());
        }
        c.close();
        FaqAdapter adapter = new FaqAdapter(Faq.this, list);
        faqlist.setAdapter(adapter);
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
