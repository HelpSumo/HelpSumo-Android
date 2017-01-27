package com.helpsumo.api.ticketing.ticket.Adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.helpsumo.api.ticketing.R;

public class AttachmentAdapter extends BaseAdapter {
    Context context;
    String[] list;
    TextView title;
    ImageView imageattach;
    ImageButton image, download;
    String id;
    String filename;

    public AttachmentAdapter(Context ctx, String[] objects, String id) {
        context = ctx;
        this.list = objects;
        this.id = id;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return this.list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fileattachment, parent, false);
        }

        title = (TextView) convertView.findViewById(R.id.fileattach);
        title.setTextSize(15);
        image = (ImageButton) convertView.findViewById(R.id.imageButton);
        download = (ImageButton) convertView.findViewById(R.id.download);
        imageattach = (ImageView) convertView.findViewById(R.id.imageattach);

        final String url = list[position];
        final String data = url.replace("\\", "");
        convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        String split = list[position];
        String[] name = split.split("/");
        filename = name[name.length - 1];
        title.setText(filename);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String urls = data.trim();
                String[] name = urls.split("/");
                final String filename = name[name.length - 1];

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setMessage("Do you want to download " + filename + "?");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        file_download(urls, context, filename);
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
        return convertView;
    }

    public void file_download(String url, Context context, String filename) {
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
    }
}
