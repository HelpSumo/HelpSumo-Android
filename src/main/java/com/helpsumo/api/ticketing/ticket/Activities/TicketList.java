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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.helpsumo.api.ticketing.R;
import com.helpsumo.api.ticketing.ticket.Adapter.ListAdapter;
import com.helpsumo.api.ticketing.ticket.ClassObjects.TicketObject;
import com.helpsumo.api.ticketing.ticket.Database.ContentProvider.CommentContentProvider;
import com.helpsumo.api.ticketing.ticket.Database.ContentProvider.TicketContentProvider;
import com.helpsumo.api.ticketing.ticket.Database.Table.CommentTable;
import com.helpsumo.api.ticketing.ticket.Database.Table.TicketTable;
import com.helpsumo.api.ticketing.ticket.utills.AppConstants;
import com.helpsumo.api.ticketing.ticket.utills.NetworkFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TicketList extends AppCompatActivity {

    ListView listView;
    RelativeLayout progressview;
    public static SharedPreferences UserEmail, RequesterName, Apikey;
    static SharedPreferences listmodifieddate;
    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    FloatingActionButton newticket;
    ArrayList<TicketObject> arrayList = new ArrayList<TicketObject>();
    ArrayList<String> arrayticket = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_list);
        listView = (ListView) findViewById(R.id.listViewtickettttt);
        progressview = (RelativeLayout) findViewById(R.id.listprogressview);
        Intent i = getIntent();
        final String apikey = i.getStringExtra("Apikey");

        listmodifieddate = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String listmodifieddat = listmodifieddate.getString("listmodifieddate", "");
        Apikey = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String apiKey = Apikey.getString("Apikey", "");
        UserEmail = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String Useremail = UserEmail.getString("UserEmail", "");
        RequesterName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String Requestername = RequesterName.getString("RequesterName", "");

        if (UserEmail.getString("UserEmail", "").equals("")) {
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
                dialogBuilder.setTitle("Enter you details..");
                dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String mail = edt.getText().toString();
                        String name = nam.getText().toString();
                        SharedPreferences.Editor editor1 = TicketList.UserEmail.edit();
                        editor1.putString("UserEmail", mail);
                        editor1.apply();
                        SharedPreferences.Editor editor = TicketList.RequesterName.edit();
                        editor.putString("RequesterName", name);
                        editor.apply();
                        AppConstants.apikey = apikey;
                        SharedPreferences.Editor editor2 = TicketList.Apikey.edit();
                        editor2.putString("Apikey", apikey);
                        editor2.apply();
                        Intent i = new Intent(TicketList.this, TicketList.class);
                        startActivity(i);

                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.setCanceledOnTouchOutside(false);
                b.show();
            }
        } else {
            String[] projection = new String[]{
                    TicketTable.TICKET_DATE, TicketTable.TICKET_OFFLINE_FLAG,
                    TicketTable.TICKET_STATUS, TicketTable.TICKET_NO, TicketTable.TICKET_SERVERID,
                    TicketTable.STAFF_NAME, TicketTable.TICKET_ATTACHMENT, TicketTable.TYPE_ID,
                    TicketTable.PRIORITY_ID, TicketTable.DEPARTMENT_ID, TicketTable.TICKET_DESCRIPTION, TicketTable.TICKET_UPDATE_TIME,
                    TicketTable.TICKET_SUBJECT, TicketTable.ID};
            String orderBy = TicketTable.ID + " ASC";
            Cursor c = getContentResolver().query(TicketContentProvider.CONTENT_URI, projection, null, null, orderBy);
            assert c != null;
            if (c.moveToFirst()) {
                do {
                    arrayticket.add(c.getString(c.getColumnIndex(TicketTable.TICKET_SERVERID)));
                } while (c.moveToNext());
            }
            c.close();

            String mail = UserEmail.getString("UserEmail", "");
            String apky = Apikey.getString("Apikey", "");
            Map<String, String> map = new HashMap<>();
            map.put("apikey", apky);
            map.put("action", "list");
            map.put("email", mail);
            map.put("modified_date", listmodifieddat);

            progressview.setVisibility(View.VISIBLE);
            if (NetworkFunction.isOnline(getApplicationContext())) {
                ListSync sync = new ListSync();
                sync.execute(map);
            } else {
                populateList();
            }
            newticket = (FloatingActionButton) findViewById(R.id.newticket);
            newticket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(TicketList.this, NewTickets.class);
                    startActivity(i);
                }
            });
        }
    }

    public class ListSync extends AsyncTask<Map<String, String>, Void, Boolean> {

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(Map<String, String>... maps) {
            try {
                Map<String, String> paramMap = maps[0];
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apikey", paramMap.get("apikey"));
                    jsonObject.put("action", paramMap.get("action"));
                    jsonObject.put("email", paramMap.get("email"));
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
                String url = AppConstants.url + "ticket";
                String response = null;
                try {
                    response = doPostRequest(url, jsonStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    Log.e("list", "response: " + response.replaceAll("\\s", " "));
                    JSONObject jobj = new JSONObject(response.replaceAll("\\s", " "));
                    if (jobj.has("ERROR")) {
                        final String toastmsg = jobj.getString("ERROR");
                        TicketList.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), toastmsg, Toast.LENGTH_LONG).show();
                                progressview.setVisibility(View.GONE);
                            }
                        });

                        return false;
                    } else {
                        JSONObject resobj = jobj.getJSONObject("response");
                        JSONArray array = resobj.optJSONArray("Ticket");
                        String delete = "", ticketsno = "", ticketsid = "", dptid = "", prtyid = "", typid = "", staffname = "", stafflast = "",
                                ticketstatus = "", ticketsdate = "", ticketsubject = "", ticketdiscription = "", ticketattach = "";
                        try {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jObject = array.getJSONObject(i);
                                Iterator<String> keys = jObject.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    key.replaceAll("\\s", " ");
                                    String value = jObject.getString(key);
                                    if (key.equalsIgnoreCase("ticket_no")) {
                                        ticketsno = value;
                                    } else if (key.equalsIgnoreCase("ticket_id")) {
                                        ticketsid = value;
                                    } else if (key.equalsIgnoreCase("assign_department_id")) {
                                        dptid = value;
                                    } else if (key.equalsIgnoreCase("ticket_priority_id")) {
                                        prtyid = value;
                                    } else if (key.equalsIgnoreCase("ticket_type_id")) {
                                        typid = value;
                                    } else if (key.equalsIgnoreCase("first_name")) {
                                        staffname = value;
                                    } else if (key.equalsIgnoreCase("last_name")) {
                                        stafflast = value;
                                    } else if (key.equalsIgnoreCase(("ticket_status"))) {
                                        ticketstatus = value;
                                    } else if (key.equalsIgnoreCase(("tickets_posted_date"))) {
                                        ticketsdate = value;
                                    } else if (key.equalsIgnoreCase(("message"))) {
                                        ticketdiscription = value;
                                    } else if (key.equalsIgnoreCase(("subject"))) {
                                        ticketsubject = value;
                                    } else if (key.equalsIgnoreCase(("delete"))) {
                                        delete = value;
                                    } else if (key.equalsIgnoreCase(("ticket_files"))) {
                                        String a = value.replace('[', ' ');
                                        String b = a.replace(']', ' ');
                                        ticketattach = b.replace('"', ' ');
                                    }
                                }
                                if (arrayticket.contains(ticketsid)) {
                                    if (delete.equals("1")) {
                                        int a = getContentResolver().delete(TicketContentProvider.CONTENT_URI, TicketTable.TICKET_SERVERID + " = ?", new String[]{ticketsid});
                                        int b = getContentResolver().delete(CommentContentProvider.CONTENT_URI, CommentTable.TICKET_ID + " = ?", new String[]{ticketsid});
                                    } else {
                                        ContentValues values = new ContentValues();
                                        values.put(TicketTable.TICKET_SERVERID, ticketsid);
                                        values.put(TicketTable.TICKET_NO, ticketsno);
                                        values.put(TicketTable.TICKET_SUBJECT, ticketsubject);
                                        values.put(TicketTable.TICKET_DESCRIPTION, ticketdiscription);
                                        values.put(TicketTable.DEPARTMENT_ID, dptid);
                                        values.put(TicketTable.PRIORITY_ID, prtyid);
                                        values.put(TicketTable.TYPE_ID, typid);
                                        values.put(TicketTable.TICKET_OFFLINE_FLAG, "0");
                                        values.put(TicketTable.STAFF_NAME, staffname + stafflast);
                                        values.put(TicketTable.TICKET_STATUS, ticketstatus);
                                        values.put(TicketTable.TICKET_DATE, ticketsdate);
                                        if (!ticketattach.contains("http")) {
                                            values.put(TicketTable.TICKET_ATTACHMENT, "false");
                                        } else {
                                            values.put(TicketTable.TICKET_ATTACHMENT, ticketattach);
                                        }
                                        getContentResolver().update(TicketContentProvider.CONTENT_URI, values,
                                                TicketTable.TICKET_SERVERID + " =? ", new String[]{ticketsid});
                                    }
                                } else {
                                    ContentValues values = new ContentValues();
                                    values.put(TicketTable.TICKET_SERVERID, ticketsid);
                                    values.put(TicketTable.TICKET_NO, ticketsno);
                                    values.put(TicketTable.TICKET_SUBJECT, ticketsubject);
                                    values.put(TicketTable.TICKET_DESCRIPTION, ticketdiscription);
                                    values.put(TicketTable.DEPARTMENT_ID, dptid);
                                    values.put(TicketTable.PRIORITY_ID, prtyid);
                                    values.put(TicketTable.TYPE_ID, typid);
                                    values.put(TicketTable.TICKET_OFFLINE_FLAG, "0");
                                    values.put(TicketTable.STAFF_NAME, staffname + stafflast);
                                    values.put(TicketTable.TICKET_STATUS, ticketstatus);
                                    values.put(TicketTable.TICKET_DATE, ticketsdate);
                                    if (!ticketattach.contains("http")) {
                                        values.put(TicketTable.TICKET_ATTACHMENT, "false");
                                    } else {
                                        values.put(TicketTable.TICKET_ATTACHMENT, ticketattach);
                                    }
                                    values.put(TicketTable.TICKET_UPDATE_TIME, "");
                                    getContentResolver().insert(TicketContentProvider.CONTENT_URI, values);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                        AppConstants.TicketsModifiedDate = resobj.getString("CurrentDateTime");
                        SharedPreferences.Editor editor5 = TicketList.listmodifieddate.edit();
                        editor5.putString("listmodifieddate", AppConstants.TicketsModifiedDate);
                        editor5.apply();
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
                populateList();
            } else {
            }
        }
    }

    String doPostRequest(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private void populateList() {
        try {
            arrayList.clear();
            String[] projection = new String[]{
                    TicketTable.TICKET_DATE, TicketTable.TICKET_OFFLINE_FLAG,
                    TicketTable.TICKET_STATUS, TicketTable.TICKET_NO, TicketTable.TICKET_SERVERID,
                    TicketTable.STAFF_NAME, TicketTable.TICKET_ATTACHMENT, TicketTable.TYPE_ID,
                    TicketTable.PRIORITY_ID, TicketTable.DEPARTMENT_ID, TicketTable.TICKET_DESCRIPTION, TicketTable.TICKET_UPDATE_TIME,
                    TicketTable.TICKET_SUBJECT, TicketTable.ID};
            String orderBy = TicketTable.ID + " DESC";
            Cursor c = getContentResolver().query(TicketContentProvider.CONTENT_URI, projection, null, null, orderBy);
            assert c != null;
            if (c.moveToFirst()) {
                do {
                    TicketObject ls = new TicketObject();
                    ls.setStatus(c.getString(c.getColumnIndex(TicketTable.TICKET_STATUS)));
                    ls.setMessage(c.getString(c.getColumnIndex(TicketTable.TICKET_SUBJECT)));
                    ls.setDate(c.getString(c.getColumnIndex(TicketTable.TICKET_DATE)));
                    ls.setStaffname(c.getString(c.getColumnIndex(TicketTable.STAFF_NAME)));
                    ls.setTicketId(c.getString(c.getColumnIndex(TicketTable.TICKET_SERVERID)));
                    arrayList.add(ls);
                } while (c.moveToNext());
            }
            c.close();
            ListAdapter adapter = new ListAdapter(getApplicationContext(), arrayList);
            listView.setAdapter(adapter);
            progressview.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
