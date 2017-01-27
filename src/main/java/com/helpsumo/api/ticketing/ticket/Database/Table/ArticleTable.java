package com.helpsumo.api.ticketing.ticket.Database.Table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ArticleTable {

    public static final String TABLE_ARTICLE = "articletable";
    public static final String ID = "id";
    public static final String ARTICLE_ID = "articleid";
    public static final String CATEGORY_ID = "categoryid";
    public static final String SUBCATEGORY_ID = "subcategoryid";
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String RATING = "rating";
    public static final String DESCRIPTION = "description";
    public static final String STATUS = "status";
    public static final String COMMENT_COUNT = "count";
    public static final String TOTALVIEWS = "totalview";
    public static final String ARTICLE_UNLIKE = "articleunlike";
    public static final String ARTICLE_LIKE = "articlelike";
    public static final String ARTICLE_UPDATE_TIME = "articleupdatetime";
    public static final String ARTICLE_LIKE_FLAG = "likeflag";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_ARTICLE
            + " ("
            + ID + " integer primary key autoincrement, "
            + ARTICLE_ID + " text not null, "
            + CATEGORY_ID + " text, "
            + SUBCATEGORY_ID + " text, "
            + TITLE + " text not null, "
            + DATE + " text, "
            + RATING + " text not null, "
            + DESCRIPTION + " text, "
            + COMMENT_COUNT + " text, "
            + STATUS + " text, "
            + ARTICLE_LIKE + " text, "
            + ARTICLE_UNLIKE + " text, "
            + ARTICLE_UPDATE_TIME + " text, "
            + ARTICLE_LIKE_FLAG + " text, "
            + TOTALVIEWS + " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(ArticleTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLE);
        onCreate(database);
    }
}
