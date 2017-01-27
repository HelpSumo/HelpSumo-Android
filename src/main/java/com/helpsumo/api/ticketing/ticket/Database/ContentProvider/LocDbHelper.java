package com.helpsumo.api.ticketing.ticket.Database.ContentProvider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.helpsumo.api.ticketing.ticket.Database.Table.ArticleTable;
import com.helpsumo.api.ticketing.ticket.Database.Table.CommentTable;
import com.helpsumo.api.ticketing.ticket.Database.Table.CommonTable;
import com.helpsumo.api.ticketing.ticket.Database.Table.FaqTable;
import com.helpsumo.api.ticketing.ticket.Database.Table.TicketTable;

public class LocDbHelper extends SQLiteOpenHelper {
    public static final String DEBUG_TAG = "location table";
    private static final String DATABASE_NAME = "loctable.db";
    private static final int DATABASE_VERSION = 1;

    public LocDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        TicketTable.onCreate(database);
        CommonTable.onCreate(database);
        CommentTable.onCreate(database);
        FaqTable.onCreate(database);
        ArticleTable.onCreate(database);

    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        TicketTable.onUpgrade(database, oldVersion, newVersion);
        CommonTable.onUpgrade(database, oldVersion, newVersion);
        CommentTable.onUpgrade(database, oldVersion, newVersion);
        FaqTable.onUpgrade(database, oldVersion, newVersion);
        ArticleTable.onUpgrade(database, oldVersion, newVersion);
    }
}