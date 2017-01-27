package com.helpsumo.api.ticketing.ticket.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.helpsumo.api.ticketing.R;
import com.helpsumo.api.ticketing.ticket.Activities.ArticleView;
import com.helpsumo.api.ticketing.ticket.ClassObjects.Articledetails;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ArticleList extends BaseAdapter {

    Context context;
    TextView title, date, count, ratecount;
    RatingBar rate;
    ArrayList<Articledetails> list = new ArrayList<Articledetails>();

    public ArticleList(Context ctx, ArrayList<Articledetails> objects) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.articl_listrow, parent, false);

        }
        title = (TextView) convertView.findViewById(R.id.articletitle);
        date = (TextView) convertView.findViewById(R.id.articledate);
        ratecount = (TextView) convertView.findViewById(R.id.ratecount);
        count = (TextView) convertView.findViewById(R.id.articlecommentcount);
        rate = (RatingBar) convertView.findViewById(R.id.ratingBar);

        title.setText(list.get(position).getArthead());
        String data = list.get(position).getArtdate();
        DateFormat df = new SimpleDateFormat("MMM d, yyyy  HH:mm", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Date startDate;
        String newDateString = "";
        try {
            startDate = sdf.parse(data);
            newDateString = df.format(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date.setText("Posted on: " + newDateString);
        count.setText("(" + list.get(position).getArtcomment() + ")");
        String a = list.get(position).getArtrating();
        float b = Float.parseFloat(a);
        rate.setRating(b);
        ratecount.setText("(" + a + ")");
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String id = list.get(position).getArtid();
                    Intent i = new Intent(context, ArticleView.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("ArticleId", id);
                    context.startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return convertView;
    }
}
