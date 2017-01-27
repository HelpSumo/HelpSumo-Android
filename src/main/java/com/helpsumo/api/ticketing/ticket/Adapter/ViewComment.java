package com.helpsumo.api.ticketing.ticket.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.helpsumo.api.ticketing.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
public class ViewComment extends BaseAdapter {

    Context context;
    ArrayList<com.helpsumo.api.ticketing.ticket.ClassObjects.Comment> list = new ArrayList<com.helpsumo.api.ticketing.ticket.ClassObjects.Comment>();
    TextView textView, textViw, textdis, textstatus, textattach;
    ImageView imagedownload, attachimage;
    String filename;
    ListView listvw;
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    LinearLayout attachlayout;
    ArrayList<String> lis = new ArrayList<>();

    public ViewComment(Context ctx, ArrayList<com.helpsumo.api.ticketing.ticket.ClassObjects.Comment> List) {
        list.clear();
        context = ctx;
        this.list = List;
    }

    @Override
    public int getCount() {
        return list.size();

    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_view, parent, false);

        }
        textView = (TextView) convertView.findViewById(R.id.textView8);
        textViw = (TextView) convertView.findViewById(R.id.textView9);
        textdis = (TextView) convertView.findViewById(R.id.subject);
        textstatus = (TextView) convertView.findViewById(R.id.ticketstatus);
        imagedownload = (ImageView) convertView.findViewById(R.id.download);
        listvw = (ListView) convertView.findViewById(R.id.attachlist);
        ImageView image = (ImageView) convertView.findViewById(R.id.imageView);
        attachlayout = (LinearLayout) convertView.findViewById(R.id.attachlayout);

        try {
            textView.setText(list.get(position).getName());
            if (list.get(position).getDate() == null) {
                textViw.setText(list.get(position).getEmail());
            } else {
                String data=list.get(position).getDate();
                DateFormat df = new SimpleDateFormat("MMM d, yyyy  HH:mm", Locale.ENGLISH);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.ENGLISH);
                Date startDate;
                String newDateString = "";
                try {
                    startDate = sdf.parse(data);
                    newDateString = df.format(startDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                textViw.setText("replied on " + newDateString);
            }
            textdis.setText(list.get(position).getMessage());
            textdis.setMovementMethod(new ScrollingMovementMethod());

            String statu = list.get(position).getStatus();
            if (list.get(position).getStatus() == null) {
                textstatus.setVisibility(View.GONE);
            } else {
                if (statu.equals("1")) {
                    textstatus.setText("New");
                } else if (statu.equals("like")) {
                    textstatus.setText("Open");
                } else if (statu.equals("3")) {
                    textstatus.setText("Progress");
                } else if (statu.equals("4")) {
                    textstatus.setText("Fixed");
                } else if (statu.equals("5")) {
                    textstatus.setText("Closed");
                } else if (statu.equals("6")) {
                    textstatus.setText("Archive");
                }
            }

            if (list.get(position).getAttach().equals("false")) {
                attachlayout.setVisibility(View.GONE);
            } else {
                attachlayout.setVisibility(View.VISIBLE);
                String url = list.get(position).getAttach().replace("\\", "");
                String[] data = url.split(",");

                int a = data.length * 100;
                AttachmentAdapter adap = new AttachmentAdapter(context, data, list.get(position).getTicketId());
                listvw.setAdapter(adap);
                listvw.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, a));

            }

        } catch (Exception e) {
                e.printStackTrace();
        }
        return convertView;
    }

    public void file_download(String url, Context context) {
        try {
            DownloadManager downloadManager = (DownloadManager) ((Activity) context).getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle("HelpSumo")
                    .setDescription("Downloading " + filename)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir("/HelpSumo", filename);
            Log.e(Environment.getExternalStorageDirectory() + "/HelpSumo/", filename);

            downloadManager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

}
