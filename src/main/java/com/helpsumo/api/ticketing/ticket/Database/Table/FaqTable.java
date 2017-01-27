package com.helpsumo.api.ticketing.ticket.Database.Table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FaqTable {

    public static final String TABLE_FAQ = "faqtable";
    public static final String ID = "id";
    public static final String FAQ_ID = "faqserverid";
    public static final String CATEGORY_ID = "categoryname";
    public static final String QUESTIONS = "question";
    public static final String ANSWERS = "answer";
    public static final String STATUS = "status";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_FAQ
            + " ("
            + ID + " integer primary key autoincrement, "
            + FAQ_ID + " text not null, "
            + CATEGORY_ID + " text, "
            + QUESTIONS + " text, "
            + ANSWERS + " text, "
            + STATUS + " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(FaqTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_FAQ);
        onCreate(database);
    }
}
