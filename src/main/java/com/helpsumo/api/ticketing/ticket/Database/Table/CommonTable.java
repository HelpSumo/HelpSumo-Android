package com.helpsumo.api.ticketing.ticket.Database.Table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//This table is to load data for dropdwn while creating new ticket
public class CommonTable {

    public static final String TABLE_COMMON = "commontable";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "dropdowntype";
    public static final String COLUMN_SERVERID = "serverid";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STATUS = "ststus";
    public static final String COLUMN_PARENT_ID = "parentid";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_COMMON
            + " ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TYPE + " integer, "
            + COLUMN_SERVERID + " text not null, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_STATUS + " text, "
            + COLUMN_PARENT_ID + " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(CommonTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMON);
        onCreate(database);
    }
//COLUMN_TYPE----department = 1 , priority = like , type = 3 , faqcategory = 4 , articlecategory =5 , articlesubcategory = 6----------
}
