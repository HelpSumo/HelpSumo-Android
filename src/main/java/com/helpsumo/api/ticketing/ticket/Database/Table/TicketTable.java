package com.helpsumo.api.ticketing.ticket.Database.Table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TicketTable {

    public static final String TABLE_TICKET = "tickettable";
    public static final String ID = "id";
    public static final String TICKET_SUBJECT = "ticketsubject";
    public static final String TICKET_DESCRIPTION = "ticketdiscription";
    public static final String TICKET_ATTACHMENT = "ticketattachment";
    public static final String DEPARTMENT_ID = "ticketdepartment";
    public static final String PRIORITY_ID = "ticketpriority";
    public static final String TYPE_ID = "tickettype";
    public static final String STAFF_NAME = "staffname";
    public static final String TICKET_STATUS = "ticketstatus";
    public static final String TICKET_NO = "ticketno";
    public static final String TICKET_SERVERID = "ticketserverid";
    public static final String TICKET_DATE = "ticketscheduledon";
    public static final String TICKET_OFFLINE_FLAG = "ticketofflineflag";
    public static final String TICKET_UPDATE_TIME = "ticketupdatetime";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_TICKET
            + " ("
            + ID + " integer primary key autoincrement, "
            + TICKET_SUBJECT + " text, "
            + TICKET_DESCRIPTION + " text, "
            + DEPARTMENT_ID + " text, "
            + PRIORITY_ID + " text, "
            + TYPE_ID + " text, "
            + TICKET_ATTACHMENT + " text, "
            + STAFF_NAME + " text, "
            + TICKET_SERVERID + " text not null, "
            + TICKET_NO + " text not null, "
            + TICKET_STATUS + " text not null, "
            + TICKET_OFFLINE_FLAG + " text, "
            + TICKET_UPDATE_TIME + " text, "
            + TICKET_DATE + " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TicketTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKET);
        onCreate(database);
    }
//ticketlog = 0 - ticket, 1= ticketlog
}
