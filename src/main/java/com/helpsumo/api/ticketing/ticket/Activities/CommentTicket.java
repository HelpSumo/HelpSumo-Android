package com.helpsumo.api.ticketing.ticket.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.helpsumo.api.ticketing.R;
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

public class CommentTicket extends AppCompatActivity {
    Spinner status;
    EditText commentmsg;
    ImageView attachmentimage;
    TextView attachmentname;
    String id, statsid, email, apikey;
    int statuid = 0;
    ArrayList<String[]> statusid = new ArrayList<>();
    ArrayList<String> statusvalue = new ArrayList<>();
    ArrayList<Uri> listuri = new ArrayList<Uri>();
    List<String> ticketdetails = new ArrayList<>();
    private String boundary = "SwA" + Long.toString(System.currentTimeMillis()) + "SwA";
    public final MediaType FormFile = MediaType.parse("multipart/form-data; boundary=" + boundary);
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanseState) {
        super.onCreate(savedInstanseState);
        setContentView(R.layout.comment_page);
        Intent i = getIntent();
        id = i.getStringExtra("TicketId");
        apikey = TicketList.Apikey.getString("Apikey", "");
        save = (Button) findViewById(R.id.save);
        status = (Spinner) findViewById(R.id.status);
        commentmsg = (EditText) findViewById(R.id.commentmsg);
        attachmentimage = (ImageView) findViewById(R.id.attachmentimage);
        attachmentname = (TextView) findViewById(R.id.attachname);
        email = TicketList.UserEmail.getString("UserEmail", "");
        commentmsg.setMovementMethod(new ScrollingMovementMethod());
        statusvalue.add("Please Select");
        statusvalue.add("Open");
        statusvalue.add("New");
        statusvalue.add("Progress");
        statusvalue.add("Fixed");
        statusvalue.add("Closed");
        statusvalue.add("Archive");
        String[] listvalue = {"1", "like", "3", "4", "5", "6"};
        ArrayAdapter<String> departAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statusvalue);
        departAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(departAdapter);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentAdd();
            }
        });
        attachmentimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Choose a file"), 1);
            }
        });
        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statuid = status.getSelectedItemPosition();
                statsid = String.valueOf(statuid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(CommentTicket.this, ViewTicket.class);
        i.putExtra("TicketId", id);
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri uri = data.getData();
            String filename = uri.getPath();
            String names = uri.getLastPathSegment();
            listuri.add(uri);
            attachmentname.setText(names);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void commentAdd() {
        boolean cancel = false;
        View focusView = null;
        String mcomment = commentmsg.getText().toString();
        View selectedView = status.getSelectedView();
        TextView Selected = (TextView) selectedView;
        if (mcomment.length() < 3 || mcomment.isEmpty()) {
            commentmsg.setError("This fields is required");
            focusView = commentmsg;
            cancel = true;
        }
        if (statsid.equals("0")) {
            Toast.makeText(CommentTicket.this, "Please select valid Status", Toast.LENGTH_SHORT).show();
            focusView = status;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else if (!cancel) {
            Map<String, String> map = new HashMap<>();
            map.put("ticket_id", id);
            map.put("status", statsid);
            map.put("comment", commentmsg.getText().toString());
            if (NetworkFunction.isOnline(getApplicationContext())) {
                CommentAddServer add = new CommentAddServer();
                add.execute(map);
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
                    jsonObject.put("ticket_id", paramMap.get("ticket_id"));
                    jsonObject.put("status", paramMap.get("status"));
                    jsonObject.put("comment", paramMap.get("comment"));
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
                String url = AppConstants.url + "ticketlog_add";
                File myFile = null;
                Uri uri = null;
                String filename = "";
                if (listuri.size() == 0) {
                    myFile = null;
                    filename = "";
                } else {
                    for (int k = 0; k < listuri.size(); k++) {
                        myFile = new File(listuri.get(k).getPath());
                        filename = (listuri.get(k).getLastPathSegment());
                        ticketdetails.add(filename);
                    }
                }
                String response = null;
                try {
                    response = doUploadRequest(url, myFile, filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("response", "comment: " + response);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Intent i = new Intent(CommentTicket.this, ViewTicket.class);
                i.putExtra("TicketId", id);
                startActivity(i);
            } else {

            }
        }
    }

    String doUploadRequest(String url, File myFile, String filename) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body;
        try {
            body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("apikey", apikey)
                    .addFormDataPart("email", email)
                    .addFormDataPart("ticket_id", id)
                    .addFormDataPart("ticket_status", statsid)
                    .addFormDataPart("reply_message", commentmsg.getText().toString())
                    .addFormDataPart("file", filename, RequestBody.create(FormFile, myFile))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("apikey", apikey)
                    .addFormDataPart("email", email)
                    .addFormDataPart("ticket_id", id)
                    .addFormDataPart("ticket_status", statsid)
                    .addFormDataPart("reply_message", commentmsg.getText().toString())
                    .addFormDataPart("file", filename)
                    .build();
        }
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(url).post(body).build();
        try {
            Response response = client.newCall(request).execute();
            return (response.body().string());
        } catch (IOException e) {
            CommentTicket.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Can't attach, Error in Nework speed, Recomment it", Toast.LENGTH_LONG).show();
                    Intent b = new Intent(CommentTicket.this, ViewTicket.class);
                    startActivity(b);
                }
            });
            e.printStackTrace();
            return null;
        }
    }
}
