package com.helpsumo.api.ticketing.ticket.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.helpsumo.api.ticketing.R;
import com.helpsumo.api.ticketing.ticket.ClassObjects.Comment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ArticleComment extends BaseAdapter {
    Context context;
    TextView textView, textViw, textdis, textstatus;
    ListView listvw;
    LinearLayout attachlayout;
    ArrayList<Comment> list = new ArrayList<>();

    public ArticleComment(Context ctx, ArrayList<Comment> objects) {
        context = ctx;
        this.list = objects;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_view, parent, false);
        }
        textView = (TextView) convertView.findViewById(R.id.textView8);
        textViw = (TextView) convertView.findViewById(R.id.textView9);
        textdis = (TextView) convertView.findViewById(R.id.subject);
        textstatus = (TextView) convertView.findViewById(R.id.ticketstatus);
        listvw = (ListView) convertView.findViewById(R.id.attachlist);
        ImageView image = (ImageView) convertView.findViewById(R.id.imageView);
        attachlayout = (LinearLayout) convertView.findViewById(R.id.attachlayout);

        image.setVisibility(View.GONE);
        textstatus.setVisibility(View.GONE);
        listvw.setVisibility(View.GONE);
        attachlayout.setVisibility(View.GONE);
        textView.setText(list.get(position).getName());
        String date = list.get(position).getDate();
        DateFormat df = new SimpleDateFormat("MMM d, yyyy  HH:mm", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Date startDate;
        String newDateString = "";
        try {
            startDate = sdf.parse(date);
            newDateString = df.format(startDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        textViw.setText("Commented on: " + newDateString);
        textdis.setText("   " + list.get(position).getMessage());
        return convertView;
    }
}
