package com.helpsumo.api.ticketing.ticket.Database.Table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CommentTable {

    public static final String TABLE_COMMENT = "commenttable";
    public static final String ID = "id";
    public static final String TICKET_LOG = "logserverid";
    public static final String TICKET_ID = "ticketid";
    public static final String LOG_MESSAGE = "logmessage";
    public static final String LOG_STATUS = "logstatus";
    public static final String LOG_ATTACHMENT = "logattachment";
    public static final String COMMENT_TYPE = "commentype";
    public static final String LOG_DATE = "logdate";
    public static final String LOG_NAME = "logname";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_COMMENT
            + " ("
            + ID + " integer primary key autoincrement, "
            + TICKET_LOG + " text not null, "
            + TICKET_ID + " text, "
            + LOG_NAME + " text, "
            + LOG_MESSAGE + " text not null, "
            + LOG_STATUS + " text not null, "
            + LOG_DATE + " text not null, "
            + COMMENT_TYPE + " text, "
            + LOG_ATTACHMENT + " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(CommentTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENT);
        onCreate(database);
    }
}
//Comment_type------ 1 = ticket comment, 2 = Article comment.