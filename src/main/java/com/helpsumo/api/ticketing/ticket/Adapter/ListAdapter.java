package com.helpsumo.api.ticketing.ticket.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.helpsumo.api.ticketing.R;
import com.helpsumo.api.ticketing.ticket.Activities.ViewTicket;
import com.helpsumo.api.ticketing.ticket.ClassObjects.TicketObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ListAdapter extends BaseAdapter {

    Context context;
    ArrayList<TicketObject> list = new ArrayList<TicketObject>();
    TextView user, status, message, date, staff;

    public ListAdapter(Context ctx, ArrayList<TicketObject> objects) {
        context = ctx;
        this.list = objects;

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
            convertView = inflater.inflate(R.layout.ticket_list_row, parent, false);
        }

        status = (TextView) convertView.findViewById(R.id.status);
        user = (TextView) convertView.findViewById(R.id.user);
        message = (TextView) convertView.findViewById(R.id.message);
        date = (TextView) convertView.findViewById(R.id.reportdate);
        staff = (TextView) convertView.findViewById(R.id.staffname);
        user.setVisibility(View.GONE);
        message.setText(list.get(position).getMessage());
        String data= list.get(position).getDate();
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
        date.setText("on "+newDateString);
        staff.setText("Assigned to: "+list.get(position).getStaffname());
        String statu = list.get(position).getStatus();
        if (statu.equals("1")) {
            status.setText("New");
        } else if (statu.equals("like")) {
            status.setText("Open");
        } else if (statu.equals("3")) {
            status.setText("Progress");
        } else if (statu.equals("4")) {
            status.setText("Fixed");
        } else if (statu.equals("5")) {
            status.setText("Closed");
        } else if (statu.equals("6")) {
            status.setText("Archive");
        }

        final String id = list.get(position).getTicketId();
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(context, ViewTicket.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("TicketId", id);
                    context.startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return convertView;
    }
}
