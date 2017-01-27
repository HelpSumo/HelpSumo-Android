package com.helpsumo.api.ticketing.ticket.Activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.helpsumo.api.ticketing.R;
import com.helpsumo.api.ticketing.ticket.Adapter.ViewComment;
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

public class ViewTicket extends AppCompatActivity {
    TextView status, date, subject;
    ListView viewticket;
    LinearLayout layout;
    View nshadow;
    ImageView delete;
    RelativeLayout progressview;
    FloatingActionsMenu multiple_actions_left;
    FloatingActionButton edit, comment;
    String dateofticket, ticketstatus, ticketsubject, ticketdate, id, ticketername, ticketeremail, ticketno, apikey;
    ArrayList<com.helpsumo.api.ticketing.ticket.ClassObjects.Comment> arrayList = new ArrayList<com.helpsumo.api.ticketing.ticket.ClassObjects.Comment>();
    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle SavedInstatnceState) {
        super.onCreate(SavedInstatnceState);
        setContentView(R.layout.ticket_view);
        Intent i = getIntent();
        id = i.getStringExtra("TicketId");
        status = (TextView) findViewById(R.id.status);
        date = (TextView) findViewById(R.id.date);
        subject = (TextView) findViewById(R.id.subject);
        viewticket = (ListView) findViewById(R.id.viewticket);
        edit = (FloatingActionButton) findViewById(R.id.edit);
        comment = (FloatingActionButton) findViewById(R.id.comment);
        delete = (ImageView) findViewById(R.id.delete);
        multiple_actions_left = (FloatingActionsMenu) findViewById(R.id.multiple_actions_left);
        layout = (LinearLayout) findViewById(R.id.layout);
        progressview = (RelativeLayout) findViewById(R.id.listprogressview);
        nshadow = (View) findViewById(R.id.mshadowView);
        ticketername = TicketList.RequesterName.getString("RequesterName", "");
        ticketeremail = TicketList.UserEmail.getString("UserEmail", "");
        apikey = TicketList.Apikey.getString("Apikey", "");
        String[] projection = new String[]{
                TicketTable.TICKET_DATE, TicketTable.TICKET_OFFLINE_FLAG,
                TicketTable.TICKET_STATUS, TicketTable.TICKET_NO, TicketTable.TICKET_SERVERID,
                TicketTable.STAFF_NAME, TicketTable.TICKET_ATTACHMENT, TicketTable.TYPE_ID,
                TicketTable.PRIORITY_ID, TicketTable.DEPARTMENT_ID, TicketTable.TICKET_DESCRIPTION, TicketTable.TICKET_UPDATE_TIME,
                TicketTable.TICKET_SUBJECT, TicketTable.ID};
        String orderBy = TicketTable.ID + " DESC";
        Cursor c = getContentResolver().query(TicketContentProvider.CONTENT_URI, projection, TicketTable.TICKET_SERVERID + " =?", new String[]{id}, orderBy);
        arrayList.clear();
        assert c != null;
        if (c.moveToFirst()) {
            do {
                com.helpsumo.api.ticketing.ticket.ClassObjects.Comment ls = new com.helpsumo.api.ticketing.ticket.ClassObjects.Comment();
                ticketstatus = (c.getString(c.getColumnIndex(TicketTable.TICKET_STATUS)));
                ticketsubject = c.getString(c.getColumnIndex(TicketTable.TICKET_SUBJECT));
                ticketdate = (c.getString(c.getColumnIndex(TicketTable.TICKET_DATE)));
                ls.setMessage(c.getString(c.getColumnIndex(TicketTable.TICKET_DESCRIPTION)));
                ls.setName(ticketername);
                dateofticket = (c.getString(c.getColumnIndex(TicketTable.TICKET_UPDATE_TIME)));
                ls.setEmail(ticketeremail);
                ls.setAttach(c.getString(c.getColumnIndex(TicketTable.TICKET_ATTACHMENT)));
                ticketno = c.getString(c.getColumnIndex(TicketTable.TICKET_NO));
                arrayList.add(ls);
            } while (c.moveToNext());
        }
        c.close();
        ViewComment adapter = new ViewComment(ViewTicket.this, arrayList);
        viewticket.setAdapter(adapter);

        switch (ticketstatus) {
            case "1":
                status.setText("Status: New");
                break;
            case "like":
                status.setText("Status: Open");
                break;
            case "3":
                status.setText("Status: Progress");
                break;
            case "4":
                status.setText("Status: Fixed");
                break;
            case "5":
                status.setText("Status: Closed");
                break;
            case "6":
                status.setText("Status: Archive");
                break;
        }
        subject.setText("Subject:  " + ticketsubject);

        DateFormat df = new SimpleDateFormat("MMM d, yyyy HH:mm", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Date startDate;
        String newDateString = "";
        try {
            startDate = sdf.parse(ticketdate);
            newDateString = df.format(startDate);
            System.out.println(newDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date.setText("Created on " + newDateString);
        if (dateofticket.isEmpty()) {
            dateofticket = "";
        }
        Map<String, String> map = new HashMap<>();
        map.put("apikey", apikey);
        map.put("ticket_id", id);
        map.put("action", "list");
        map.put("modified_datetime", dateofticket);
        progressview.setVisibility(View.VISIBLE);
        if (NetworkFunction.isOnline(getApplicationContext())) {
            LoadData load = new LoadData();
            load.execute(map);
        } else {
            populatedata();
        }
        multiple_actions_left.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                nshadow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                nshadow.setVisibility(View.GONE);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewTicket.this, NewTickets.class);
                i.putExtra("TicketId", id);
                startActivity(i);
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewTicket.this, CommentTicket.class);
                i.putExtra("TicketId", id);
                startActivity(i);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewTicket.this);
                alertDialog.setMessage("Do you want to delete " + "'" + ticketsubject + "'" + " ?");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (NetworkFunction.isOnline(getApplicationContext())) {
                            Map<String, String> map = new HashMap<>();
                            map.put("apikey", apikey);
                            map.put("ticket_id", id);
                            map.put("ticket_no", ticketno);
                            map.put("action", "delete");
                            TicketDelete delete = new TicketDelete();
                            delete.execute(map);
                        }
                    }
                });

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ViewTicket.this, TicketList.class);
        startActivity(i);
    }

    public class LoadData extends AsyncTask<Map<String, String>, Void, Boolean> {
        @SafeVarargs
        @Override
        protected final Boolean doInBackground(Map<String, String>... maps) {
            try {
                Map<String, String> paramMap = maps[0];
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apikey", paramMap.get("apikey"));
                    jsonObject.put("action", paramMap.get("action"));
                    jsonObject.put("ticket_id", paramMap.get("ticket_id"));
                    jsonObject.put("modified_datetime", paramMap.get("modified_datetime"));
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
                String url = AppConstants.url + "ticketlog";
                String response = null;
                try {
                    response = doPostRequest(url, jsonStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    Log.e("list", "response: " + response.replaceAll("\\s", " "));
                    String logid = "", ticketid = "", replymsg = "", status = "", replydate = "", firstname = "", lastname = "", modifiedtime = "", attach = "";
                    JSONObject jobj = new JSONObject(response.replaceAll("\\s", " "));
                    JSONObject resobj = jobj.getJSONObject("response");
                    if (jobj.has("ERROR")) {
                        final String toastmsg = jobj.getString("ERROR");
                        ViewTicket.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), toastmsg, Toast.LENGTH_LONG).show();
                                progressview.setVisibility(View.GONE);
                            }
                        });
                        return false;
                    } else {
                        JSONArray array = resobj.optJSONArray("TicketLog");
                        modifiedtime = resobj.getString("CurrentDateTime");
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONObject jObject = array.getJSONObject(i);
                                Iterator<String> keys = jObject.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    key.replaceAll("\\s", " ");
                                    String value = jObject.getString(key);
                                    if (key.equalsIgnoreCase("log_id")) {
                                        logid = value;
                                    } else if (key.equalsIgnoreCase("ticket_id")) {
                                        ticketid = value;
                                    } else if (key.equalsIgnoreCase("reply_message")) {
                                        replymsg = Html.fromHtml(value).toString();
                                    } else if (key.equalsIgnoreCase("ticket_status")) {
                                        status = value;
                                    } else if (key.equalsIgnoreCase("reply_date")) {
                                        replydate = value;
                                    } else if (key.equalsIgnoreCase("first_name")) {
                                        firstname = value;
                                    } else if (key.equalsIgnoreCase("last_name")) {
                                        lastname = value;
                                    } else if (key.equalsIgnoreCase("attachfiles")) {
                                        String a = value.replace('[', ' ');
                                        String b = a.replace(']', ' ');
                                        attach = b.replace('"', ' ');
                                    }
                                }
                                ContentValues values = new ContentValues();
                                values.put(CommentTable.TICKET_LOG, logid);
                                values.put(CommentTable.LOG_MESSAGE, replymsg);
                                values.put(CommentTable.LOG_STATUS, status);
                                if (!attach.contains("http")) {
                                    values.put(CommentTable.LOG_ATTACHMENT, "false");
                                } else {
                                    values.put(CommentTable.LOG_ATTACHMENT, attach);
                                }
                                values.put(CommentTable.LOG_DATE, replydate);
                                values.put(CommentTable.LOG_NAME, firstname + lastname);
                                values.put(CommentTable.TICKET_ID, ticketid);
                                values.put(CommentTable.COMMENT_TYPE, "1");
                                getContentResolver().insert(CommentContentProvider.CONTENT_URI, values);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                        ContentValues value = new ContentValues();
                        value.put(TicketTable.TICKET_UPDATE_TIME, modifiedtime);
                        getContentResolver().update(TicketContentProvider.CONTENT_URI, value,
                                TicketTable.TICKET_SERVERID + " = ?", new String[]{String.valueOf(id)});
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

    public class TicketDelete extends AsyncTask<Map<String, String>, Void, Boolean> {
        @SafeVarargs
        @Override
        protected final Boolean doInBackground(Map<String, String>... maps) {
            try {
                Map<String, String> paramMap = maps[0];
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apikey", paramMap.get("apikey"));
                    jsonObject.put("action", paramMap.get("action"));
                    jsonObject.put("ticket_id", paramMap.get("ticket_id"));
                    jsonObject.put("ticket_no", paramMap.get("ticket_no"));
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
                assert response != null;
                Log.e("response", "" + response.replaceAll("\\s", " "));
                JSONObject obj = new JSONObject(response);
                if (obj.has("SUCCESS")) {
                    int a = getContentResolver().delete(TicketContentProvider.CONTENT_URI, TicketTable.TICKET_SERVERID + " = ?", new String[]{id});
                    int b = getContentResolver().delete(CommentContentProvider.CONTENT_URI, CommentTable.TICKET_ID + " = ?", new String[]{id});
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        protected void onPostExecute(final Boolean success) {
            if (success) {
                Toast.makeText(ViewTicket.this, "Your Ticket has been Deleted", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ViewTicket.this, TicketList.class);
                startActivity(i);
            } else {
            }
        }
    }

    void populatedata() {
        String[] projection = new String[]{
                CommentTable.ID, CommentTable.TICKET_LOG, CommentTable.LOG_MESSAGE, CommentTable.LOG_STATUS, CommentTable.COMMENT_TYPE,
                CommentTable.LOG_NAME, CommentTable.TICKET_ID, CommentTable.LOG_DATE, CommentTable.LOG_ATTACHMENT};
        String orderBy = CommentTable.ID + " ASC";
        Cursor c = getContentResolver().query(CommentContentProvider.CONTENT_URI, projection, CommentTable.TICKET_ID + " = ? ", new String[]{id}, orderBy);
        assert c != null;
        if (c.moveToFirst()) {
            do {
                com.helpsumo.api.ticketing.ticket.ClassObjects.Comment ls = new com.helpsumo.api.ticketing.ticket.ClassObjects.Comment();
                ls.setName(c.getString(c.getColumnIndex(CommentTable.LOG_NAME)));
                ls.setDate(c.getString(c.getColumnIndex(CommentTable.LOG_DATE)));
                ls.setMessage(c.getString(c.getColumnIndex(CommentTable.LOG_MESSAGE)));
                ls.setStatus(c.getString(c.getColumnIndex(CommentTable.LOG_STATUS)));
                ls.setAttach(c.getString(c.getColumnIndex(CommentTable.LOG_ATTACHMENT)));
                arrayList.add(ls);
            } while (c.moveToNext());
        }
        c.close();
        ViewComment adapter = new ViewComment(ViewTicket.this, arrayList);
        viewticket.setAdapter(adapter);
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