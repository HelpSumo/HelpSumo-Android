package com.helpsumo.api.ticketing.ticket.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.helpsumo.api.ticketing.R;
import com.helpsumo.api.ticketing.ticket.ClassObjects.DepartmentDropDown;
import com.helpsumo.api.ticketing.ticket.ClassObjects.PriorityDropDown;
import com.helpsumo.api.ticketing.ticket.ClassObjects.TypeDropDown;
import com.helpsumo.api.ticketing.ticket.Database.ContentProvider.CommonContentProvider;
import com.helpsumo.api.ticketing.ticket.Database.ContentProvider.TicketContentProvider;
import com.helpsumo.api.ticketing.ticket.Database.Table.CommonTable;
import com.helpsumo.api.ticketing.ticket.Database.Table.TicketTable;
import com.helpsumo.api.ticketing.ticket.utills.AppConstants;
import com.helpsumo.api.ticketing.ticket.utills.NetworkFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewTickets extends AppCompatActivity {

    EditText ticketsubject, ticketmsg, email, username;
    TextView atttachview, usernametext, useremailtext;
    Button addticket;
    boolean editTicketState;
    ImageButton upload;
    String contuserid, cont, contx, apikey, updatetime, editticketid, attachmentdata;
    int departmentid = 0;
    int priorityid = 0;
    int typeid = 0;
    int FLAG_UPLOAD = 0;
    Spinner department, priority, type;
    List<String> departlist = new ArrayList<String>();
    List<String> priorlist = new ArrayList<String>();
    List<String> typelist = new ArrayList<String>();
    ArrayList<String> ticketno = new ArrayList<>();
    public static SharedPreferences UserId, TicketNo, Dropdown, Updatetime;
    ArrayList<TypeDropDown> typedata = new ArrayList<TypeDropDown>();
    ArrayList<DepartmentDropDown> departdata = new ArrayList<DepartmentDropDown>();
    ArrayList<PriorityDropDown> priordata = new ArrayList<PriorityDropDown>();
    ArrayList<Uri> listuri = new ArrayList<Uri>();
    List<String> ticketdetails = new ArrayList<>();
    public static final String EXTRA_PATHS = "nononsense.intent.PATHS";
    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String boundary = "SwA" + Long.toString(System.currentTimeMillis()) + "SwA";
    public final MediaType FormFile = MediaType.parse("multipart/form-data; boundary=" + boundary);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_form);
        UserId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String Userid = UserId.getString("UserId", "0");
        Dropdown = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String dropdown = Dropdown.getString("Dropdown", "0");
        Updatetime = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String updatetime = Updatetime.getString("Updatetime", "");
        TicketNo = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ticketno.getClass().getFields();
        String Ticketno = TicketNo.getString("TicketNo", "0");
        ticketmsg = (EditText) findViewById(R.id.ticketmessage);
        ticketsubject = (EditText) findViewById(R.id.ticket_subject);
        email = (EditText) findViewById(R.id.useremail);
        username = (EditText) findViewById(R.id.username);
        addticket = (Button) findViewById(R.id.buttonadd);
        upload = (ImageButton) findViewById(R.id.imageButton);
        department = (Spinner) findViewById(R.id.departmentspinner);
        priority = (Spinner) findViewById(R.id.priorityspinner);
        type = (Spinner) findViewById(R.id.typespinner);
        atttachview = (TextView) findViewById(R.id.attachview);
        usernametext = (TextView) findViewById(R.id.name);
        useremailtext = (TextView) findViewById(R.id.email);
        try {
            Intent i = getIntent();
            editticketid = i.getStringExtra("TicketId");
            if (editticketid != null) {
                editTicket();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserId = PreferenceManager.getDefaultSharedPreferences(this);
        contuserid = UserId.getString("UserId", "0");
        TicketNo = PreferenceManager.getDefaultSharedPreferences(this);
        contx = TicketNo.getString("TicketNo", "0");
        cont = TicketList.UserEmail.getString("UserEmail", "");
        if (!cont.equals("") || !contx.equals("0")) {
            usernametext.setVisibility(View.GONE);
            useremailtext.setVisibility(View.GONE);
            username.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        }
        updatetime = Updatetime.getString("Updatetime", "");
        apikey = TicketList.Apikey.getString("Apikey", "");
        Map<String, String> map = new HashMap<String, String>();
        map.put("apikey", apikey);
        map.put("datetime", updatetime);
        if (NetworkFunction.isOnline(getApplicationContext())) {
            SpinnerFromServer spin = new SpinnerFromServer();
            spin.execute(map);
        } else {
            addview();
        }
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Choose a file"), 1);
                FLAG_UPLOAD = 1;
            }
        });

        addticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTicket();

            }
        });
        department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                departmentid = Integer.valueOf(departdata.get(pos).getDptId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        priority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    priorityid = Integer.valueOf(priordata.get(position).getpriorId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    typeid = Integer.valueOf(typedata.get(position).getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (editticketid == null) {
            Intent i = new Intent(NewTickets.this, TicketList.class);
            startActivity(i);
        } else {
            Intent i = new Intent(NewTickets.this, ViewTicket.class);
            i.putExtra("TicketId", editticketid);
            startActivity(i);
        }
    }

    public void setEditTicketState(boolean editTicket) {
        this.editTicketState = editTicket;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        listuri.clear();
        Uri uri = data.getData();
        String filename = uri.getLastPathSegment();
        listuri.add(uri);
        atttachview.setText(filename);
    }

    void editTicket() {
        String[] projection = new String[]{
                TicketTable.TICKET_DATE, TicketTable.TICKET_OFFLINE_FLAG,
                TicketTable.TICKET_STATUS, TicketTable.TICKET_NO, TicketTable.TICKET_SERVERID,
                TicketTable.STAFF_NAME, TicketTable.TICKET_ATTACHMENT, TicketTable.TYPE_ID,
                TicketTable.PRIORITY_ID, TicketTable.DEPARTMENT_ID, TicketTable.TICKET_DESCRIPTION, TicketTable.TICKET_UPDATE_TIME,
                TicketTable.TICKET_SUBJECT, TicketTable.ID};
        String orderBy = TicketTable.ID;
        Cursor c = getContentResolver().query(TicketContentProvider.CONTENT_URI, projection, TicketTable.TICKET_SERVERID + " =?", new String[]{editticketid}, orderBy);
        String tktsub = "", tktdes = "", tktattach = "";
        assert c != null;
        if (c.moveToFirst()) {
            do {
                tktsub = c.getString(c.getColumnIndex(TicketTable.TICKET_SUBJECT));
                tktdes = c.getString(c.getColumnIndex(TicketTable.TICKET_DESCRIPTION));
                tktattach = c.getString(c.getColumnIndex(TicketTable.TICKET_ATTACHMENT));
            } while (c.moveToNext());
        }
        c.close();
        EditText ticketmsg = (EditText) findViewById(R.id.ticketmessage);
        EditText ticketsubject = (EditText) findViewById(R.id.ticket_subject);
        TextView attachview = (TextView) findViewById(R.id.attachview);
        ticketsubject.setText(tktsub);
        ticketmsg.setText(tktdes);
        ticketmsg.setMovementMethod(new ScrollingMovementMethod());
        if (tktattach.equals("false")) {
            attachview.setText("No AttachmentAdapter");
        } else {
            String[] name = tktattach.split("/");
            String filename = name[name.length - 1];
            attachview.setText(filename);
        }
        attachmentdata = tktattach;
        setEditTicketState(true);
    }

    void addTicket() {
        boolean cancel = false;
        View focusView = null;
        ticketmsg.setError(null);
        ticketsubject.setError(null);
        String hdepart = department.getSelectedItem().toString();
        String hpriority = priority.getSelectedItem().toString();
        String htype = type.getSelectedItem().toString();
        String mTktsubjct = ticketsubject.getText().toString();
        String mTicktmsg = ticketmsg.getText().toString();
        String memail = email.getText().toString();
        String musername = username.getText().toString();
        View selectedView = department.getSelectedView();
        View selected = priority.getSelectedView();
        View viewSelected = type.getSelectedView();
        TextView selectedTextView = (TextView) selectedView;
        TextView Selected = (TextView) selected;
        TextView Viewselected = (TextView) viewSelected;
        if (mTktsubjct.trim().length() < 3 || mTktsubjct.length() == 0) {
            ticketsubject.setError("This field is required");
            focusView = ticketsubject;
            cancel = true;
        }
        if (mTicktmsg.trim().length() < 3 || mTicktmsg.length() == 0) {
            ticketmsg.setError("This field is required");
            focusView = ticketmsg;
            cancel = true;
        }
        if (hdepart.equalsIgnoreCase("Please select")) {
            selectedTextView.setError("Select Department");
            focusView = department;
            cancel = true;
        }
        if (hpriority.equalsIgnoreCase("Please select")) {
            Selected.setError("Select Priority");
            focusView = priority;
            cancel = true;
        }
        if (htype.equalsIgnoreCase("Please select")) {
            Viewselected.setError("Select Type");
            focusView = type;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else if (!cancel) {
            Map<String, String> map = new HashMap<String, String>();
            TicketList.UserEmail = PreferenceManager.getDefaultSharedPreferences(this);
            String mail = TicketList.UserEmail.getString("UserEmail", "");
            TicketList.RequesterName = PreferenceManager.getDefaultSharedPreferences(this);
            String name = TicketList.RequesterName.getString("RequesterName", "");
            map.put("apikey", apikey);
            if (editticketid != null) {
                map.put("ticket_id", editticketid);
                map.put("action", "update");
            } else {
                map.put("ticket_id", "");
                map.put("action", "add");
            }
            map.put("email", mail);
            map.put("requestername", name);
            map.put("subject", ticketsubject.getText().toString());
            map.put("message", ticketmsg.getText().toString());
            map.put("assign_department_id", String.valueOf(departmentid));
            map.put("ticket_priority_id", String.valueOf(priorityid));
            map.put("ticket_type_id", String.valueOf(typeid));
            if (NetworkFunction.isOnline(getApplicationContext())) {
                CreatenewTicket exe = new CreatenewTicket();
                exe.execute(map);
            }
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
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
                    jsonObject.put("datetime", paramMap.get("datetime"));
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
                String url = AppConstants.url + "ticketlookup";
                String response = null;
                try {
                    response = doPostRequest(url, jsonStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    Log.e("response", "response: " + response.replaceAll("\\s", " "));
                    String departid, departname, priorityid, priorityname, typeid, typename;
                    JSONObject rootJsonObj = new JSONObject(response);
                    if (rootJsonObj.has("ERROR")) {
                        final String toastmsg = rootJsonObj.getString("ERROR");
                        NewTickets.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), toastmsg, Toast.LENGTH_LONG).show();
                            }
                        });
                        return false;
                    } else {
                        JSONArray wArray = rootJsonObj.optJSONArray("department");
                        if (wArray.length() == 0) {
                        } else {
                            int a = getContentResolver().delete(CommonContentProvider.CONTENT_URI, CommonTable.COLUMN_TYPE + "= ?", new String[]{"1"});
                            ContentValues val = new ContentValues();
                            val.put(CommonTable.COLUMN_TYPE, 1);
                            val.put(CommonTable.COLUMN_SERVERID, "0");
                            val.put(CommonTable.COLUMN_NAME, "Please Select");
                            val.put(CommonTable.COLUMN_STATUS, "");
                            getContentResolver().insert(CommonContentProvider.CONTENT_URI, val);
                            for (int i = 0; i < wArray.length(); i++) {
                                JSONObject weatherJsonObj = wArray.getJSONObject(i);
                                departid = weatherJsonObj.getString("department_id");
                                departname = weatherJsonObj.getString("department_name");
                                departlist.add(departname);
                                ContentValues values = new ContentValues();
                                values.put(CommonTable.COLUMN_TYPE, 1);
                                values.put(CommonTable.COLUMN_SERVERID, departid);
                                values.put(CommonTable.COLUMN_NAME, departname);
                                values.put(CommonTable.COLUMN_STATUS, "");
                                getContentResolver().insert(CommonContentProvider.CONTENT_URI, values);
                            }
                        }
                        JSONArray xArray = rootJsonObj.optJSONArray("priority");
                        if (xArray.length() == 0) {
                        } else {
                            int b = getContentResolver().delete(CommonContentProvider.CONTENT_URI, CommonTable.COLUMN_TYPE + "= ?", new String[]{"like"});
                            ContentValues valu = new ContentValues();
                            valu.put(CommonTable.COLUMN_TYPE, 2);
                            valu.put(CommonTable.COLUMN_SERVERID, "0");
                            valu.put(CommonTable.COLUMN_NAME, "Please Select");
                            valu.put(CommonTable.COLUMN_STATUS, "");
                            getContentResolver().insert(CommonContentProvider.CONTENT_URI, valu);
                            for (int i = 0; i < xArray.length(); i++) {
                                JSONObject weatherJsonObj = xArray.getJSONObject(i);
                                priorityid = weatherJsonObj.getString("id");
                                priorityname = weatherJsonObj.getString("priority_name");
                                priorlist.add(priorityname);
                                ContentValues values = new ContentValues();
                                values.put(CommonTable.COLUMN_TYPE, 2);
                                values.put(CommonTable.COLUMN_SERVERID, priorityid);
                                values.put(CommonTable.COLUMN_NAME, priorityname);
                                values.put(CommonTable.COLUMN_STATUS, "");
                                getContentResolver().insert(CommonContentProvider.CONTENT_URI, values);
                            }
                        }
                        JSONArray yArray = rootJsonObj.optJSONArray("tickettype");
                        if (yArray.length() == 0) {
                        } else {
                            int c = getContentResolver().delete(CommonContentProvider.CONTENT_URI, CommonTable.COLUMN_TYPE + "= ?", new String[]{"3"});
                            ContentValues value = new ContentValues();
                            value.put(CommonTable.COLUMN_TYPE, 3);
                            value.put(CommonTable.COLUMN_SERVERID, "0");
                            value.put(CommonTable.COLUMN_NAME, "Please Select");
                            value.put(CommonTable.COLUMN_STATUS, "");
                            getContentResolver().insert(CommonContentProvider.CONTENT_URI, value);

                            for (int i = 0; i < yArray.length(); i++) {
                                JSONObject weatherJsonObj = yArray.getJSONObject(i);
                                typeid = weatherJsonObj.getString("ticket_type_id");
                                typename = weatherJsonObj.getString("type_name");
                                typelist.add(typename);
                                ContentValues values = new ContentValues();
                                values.put(CommonTable.COLUMN_TYPE, 3);
                                values.put(CommonTable.COLUMN_SERVERID, typeid);
                                values.put(CommonTable.COLUMN_NAME, typename);
                                values.put(CommonTable.COLUMN_STATUS, "");
                                getContentResolver().insert(CommonContentProvider.CONTENT_URI, values);
                            }
                        }
                        JSONObject time = rootJsonObj.getJSONObject("datetime");
                        updatetime = time.getString("CurrentDateTime");
                        SharedPreferences.Editor editor = Updatetime.edit();
                        editor.putString("Updatetime", updatetime);
                        editor.apply();
                        SharedPreferences.Editor editor3 = Dropdown.edit();
                        editor3.putString("Dropdown", "1");
                        editor3.apply();
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

    public class CreatenewTicket extends AsyncTask<Map<String, String>, Void, Boolean> {
        @SafeVarargs
        @Override
        protected final Boolean doInBackground(Map<String, String>... maps) {
            try {
                Map<String, String> paramMap = maps[0];
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apikey", paramMap.get("apikey"));
                    jsonObject.put("ticket_id", paramMap.get("ticket_id"));
                    jsonObject.put("action", paramMap.get("action"));
                    jsonObject.put("email", paramMap.get("email"));
                    jsonObject.put("requestername", paramMap.get("requestername"));
                    jsonObject.put("subject", paramMap.get("subject"));
                    jsonObject.put("message", paramMap.get("message"));
                    jsonObject.put("assign_department_id", paramMap.get("assign_department_id"));
                    jsonObject.put("ticket_priority_id", paramMap.get("ticket_priority_id"));
                    jsonObject.put("ticket_type_id", paramMap.get("ticket_type_id"));
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
                Log.e("create ticket", "new: " + response.replaceAll("\\s", " "));
                String usersid, usersemail = "", usersname = "", ticketsno, ticketsid, dptid, prtyid, typid, staffid,
                        ticketstatus, ticketsdate, ticketsubject, ticketdiscription;
                if (editticketid != null) {
                    JSONObject obj1 = new JSONObject(response.replaceAll("\\s", " "));
                    JSONObject listPathObject1 = obj1.getJSONObject("response");
                    JSONObject updatetkt = listPathObject1.getJSONObject("ticket");
                    for (int i = 0; i < updatetkt.length(); i++) {
                        String ticket = updatetkt.getString("ticket_no");
                        String ticketsub = updatetkt.getString("subject");
                        String ticketmsg = updatetkt.getString("message");
                        String ticketdate = updatetkt.getString("tickets_posted_date");
                        AppConstants.TicketId = editticketid;
                        AppConstants.TicketNo = ticket;
                    }
                } else {
                    JSONObject obj = new JSONObject(response);
                    if (obj.has("ERROR")) {
                        final String err = obj.getString("ERROR");
                        NewTickets.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                            }
                        });
                        return false;
                    } else if (obj.has("SUCCESS")) {
                        JSONArray wArray = null;
                        JSONArray xArray = null;
                        JSONObject listPathObject = obj.getJSONObject("response");
                        if (listPathObject.has("ticket")) {
                            xArray = listPathObject.optJSONArray("ticket");
                        }
                        if (listPathObject.has("user")) {
                            wArray = listPathObject.getJSONArray("user");
                        }
                        if (wArray != null) {
                            for (int i = 0; i < wArray.length(); i++) {
                                JSONObject weatherJsonObj = wArray.getJSONObject(i);
                                usersid = weatherJsonObj.getString("user_id");
                                AppConstants.userId = usersid;
                                SharedPreferences.Editor editor5 = NewTickets.UserId.edit();
                                editor5.putString("UserId", AppConstants.userId);
                                editor5.commit();
                                usersemail = weatherJsonObj.getString("email");
                                AppConstants.UserEmail = usersemail;
                                SharedPreferences.Editor editor1 = TicketList.UserEmail.edit();
                                editor1.putString("UserEmail", usersemail);
                                editor1.commit();
                                usersname = weatherJsonObj.getString("first_name");
                                SharedPreferences.Editor editor2 = TicketList.RequesterName.edit();
                                editor2.putString("RequesterName", usersname);
                                editor2.commit();
                                AppConstants.RequesterName = usersname;
                            }
                        }
                        if (xArray != null) {
                            for (int i = 0; i < xArray.length(); i++) {
                                JSONObject weatherJsonObj = xArray.getJSONObject(i);
                                ticketsno = weatherJsonObj.getString("ticket_no");
                                AppConstants.TicketNo = ticketsno;
                                ticketsid = weatherJsonObj.getString("ticket_id");
                                AppConstants.TicketId = ticketsid;
                                dptid = weatherJsonObj.getString("assign_department_id");
                                AppConstants.DepartId = dptid;
                                prtyid = weatherJsonObj.getString("ticket_priority_id");
                                AppConstants.PriorityId = prtyid;
                                typid = weatherJsonObj.getString("ticket_type_id");
                                AppConstants.TypeId = typid;
                                staffid = weatherJsonObj.getString("assign_staff_id");
                                AppConstants.StaffId = staffid;
                                ticketstatus = weatherJsonObj.getString("ticket_status");
                                AppConstants.TicketStatus = ticketstatus;
                                ticketsdate = weatherJsonObj.getString("tickets_posted_date");
                                AppConstants.TicketDate = ticketsdate;
                                ticketdiscription = weatherJsonObj.getString("message");
                                AppConstants.TicketMessage = ticketdiscription;
                                ticketsubject = weatherJsonObj.getString("subject");
                                AppConstants.TicketSubject = ticketsubject;
                            }
                        }
                    } else {
                        JSONObject objj = new JSONObject(response.replaceAll("\\s", " "));
                        JSONObject listPathObject = objj.getJSONObject("response");
                        JSONArray xArray = listPathObject.optJSONArray("ticket");
                        if (xArray.length() != 0) {
                            for (int i = 0; i < xArray.length(); i++) {
                                JSONObject weatherJsonObj = xArray.getJSONObject(i);
                                ticketsno = weatherJsonObj.getString("ticket_no");
                                AppConstants.TicketNo = ticketsno;
                                ticketsid = weatherJsonObj.getString("ticket_id");
                                AppConstants.TicketId = ticketsid;
                                dptid = weatherJsonObj.getString("assign_department_id");
                                AppConstants.DepartId = dptid;
                                prtyid = weatherJsonObj.getString("ticket_priority_id");
                                AppConstants.PriorityId = prtyid;
                                typid = weatherJsonObj.getString("ticket_type_id");
                                AppConstants.TypeId = typid;
                                staffid = weatherJsonObj.getString("assign_staff_id");
                                AppConstants.StaffId = staffid;
                                ticketstatus = weatherJsonObj.getString("ticket_status");
                                AppConstants.TicketStatus = ticketstatus;
                                ticketsdate = weatherJsonObj.getString("tickets_posted_date");
                                AppConstants.TicketDate = ticketsdate;
                                ticketdiscription = weatherJsonObj.getString("message");
                                AppConstants.TicketMessage = ticketdiscription;
                                ticketsubject = weatherJsonObj.getString("subject");
                                AppConstants.TicketSubject = ticketsubject;
                            }
                        }
                    }
                    SharedPreferences.Editor editor = NewTickets.TicketNo.edit();
                    editor.putString("TicketNo", AppConstants.TicketNo);
                    editor.commit();
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                if (listuri.size() != 0) {
                    Map<String, String> map = new HashMap<>();
                    map.put("dfsdf", "sdfsd");
                    Fileupload upload = new Fileupload();
                    upload.execute(map);
                } else {
                    Intent i = new Intent(NewTickets.this, TicketList.class);
                    startActivity(i);
                }

            } else {
            }
        }
    }

    public class Fileupload extends AsyncTask<Map<String, String>, Void, Boolean> {
        @SafeVarargs
        @Override
        protected final Boolean doInBackground(Map<String, String>... maps) {
            try {
                Map<String, String> paramMap = maps[0];
                JSONObject jsonObject = new JSONObject();
                if (editticketid == null || FLAG_UPLOAD == 1) {
                    File myFile = null;
                    Uri uri = null;
                    String filename = "";
                    for (int k = 0; k < listuri.size(); k++) {
                        myFile = new File(listuri.get(k).getPath());
                        filename = (listuri.get(k).getLastPathSegment());
                        ticketdetails.add(filename);
                    }
                    String jsonStr = jsonObject.toString();
                    System.out.println("jsonString: " + jsonStr);
                    String url = AppConstants.url + "ticketattachment";
                    String response = doUploadRequest(url, myFile, filename);
                    Log.e("response", "ticket " + response.replaceAll("\\s", " "));

                    if (response == null) {
                        Toast.makeText(NewTickets.this, "Weak Signal Strength, Attach Again", Toast.LENGTH_SHORT).show();
                        return false;
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
                Intent i = new Intent(NewTickets.this, TicketList.class);
                startActivity(i);
            } else {
            }
        }
    }

    void addview() {
        departlist.clear();
        priorlist.clear();
        typelist.clear();
        String[] projectionjob = {
                CommonTable.COLUMN_ID, CommonTable.COLUMN_NAME, CommonTable.COLUMN_SERVERID, String.valueOf(CommonTable.COLUMN_TYPE)};
        String orderBy = CommonTable.COLUMN_ID + " ASC";
        Cursor cursor = getContentResolver().query(CommonContentProvider.CONTENT_URI, projectionjob, CommonTable.COLUMN_TYPE + " = ?", new String[]{"1"}, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                DepartmentDropDown ls = new DepartmentDropDown();
                ls.setDptId(cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_SERVERID)));
                ls.setDptname(cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_NAME)));
                departlist.add(cursor.getString(cursor.getColumnIndex(CommonTable.COLUMN_NAME)));
                departdata.add(ls);
            }
            while (cursor.moveToNext());
        }
        Cursor cursor1 = getContentResolver().query(CommonContentProvider.CONTENT_URI, projectionjob, CommonTable.COLUMN_TYPE + " = ?", new String[]{"2"}, orderBy);
        if (cursor1 != null && cursor1.moveToFirst()) {
            do {
                PriorityDropDown ls = new PriorityDropDown();
                ls.setpriorId(cursor1.getString(cursor1.getColumnIndex(CommonTable.COLUMN_SERVERID)));
                ls.setpriorName(cursor1.getString(cursor1.getColumnIndex(CommonTable.COLUMN_NAME)));
                priorlist.add(cursor1.getString(cursor1.getColumnIndex(CommonTable.COLUMN_NAME)));
                priordata.add(ls);
            }
            while (cursor1.moveToNext());
        }
        Cursor cursor2 = getContentResolver().query(CommonContentProvider.CONTENT_URI, projectionjob, CommonTable.COLUMN_TYPE + " = ?", new String[]{"3"}, orderBy);
        if (cursor2 != null && cursor2.moveToFirst()) {
            do {
                TypeDropDown ls = new TypeDropDown();
                ls.setId(cursor2.getString(cursor2.getColumnIndex(CommonTable.COLUMN_SERVERID)));
                ls.setTypename(cursor2.getString(cursor2.getColumnIndex(CommonTable.COLUMN_NAME)));
                typelist.add(cursor2.getString(cursor2.getColumnIndex(CommonTable.COLUMN_NAME)));
                typedata.add(ls);
            }
            while (cursor2.moveToNext());
        }
        addAdapter();
    }

    void addAdapter() {
        ArrayAdapter<String> departAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, departlist);
        departAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        department.setAdapter(departAdapter);
        ArrayAdapter<String> priorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, priorlist);
        priorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priority.setAdapter(priorAdapter);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typelist);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(typeAdapter);
    }

    String doPostRequest(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    String doUploadRequest(String url, File myFile, String filename) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("apikey", apikey)
                .addFormDataPart("ticket_no", AppConstants.TicketNo)
                .addFormDataPart("ticket_id", AppConstants.TicketId)
                .addFormDataPart("file", filename, RequestBody.create(FormFile, myFile))
                .build();
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(url).post(body).build();
        try {
            Response response = client.newCall(request).execute();
            return (response.body().string());

        } catch (IOException e) {
            NewTickets.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Can't attach, Error in Nework speed, update ticket for attachment", Toast.LENGTH_LONG).show();
                    Intent b = new Intent(NewTickets.this, TicketList.class);
                    startActivity(b);
                }
            });
            e.printStackTrace();
            return null;
        }
    }
}
