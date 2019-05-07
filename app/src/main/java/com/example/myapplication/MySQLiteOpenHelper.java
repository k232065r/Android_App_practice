package com.example.myapplication;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLヘルパーのクラス
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public final String TABLE_NAME = "DT_TRANSPORTATION_EXPENSES";

    // SQL文をStringに保持しておく
    private final String  CREATE_TABLE =  "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "date TEXT NOT NULL, station TEXT NOT NULL, " +
            "start_station TEXT NOT NULL, end_station TEXT NOT NULL, "  +
            "cost INTEGER NOT NULL, type TEXT NOT NULL, reason TEXT) ";

    private final String DROP_TABLE = "DROP TABLE " + TABLE_NAME;

    /**
     * コンストラクタ
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public MySQLiteOpenHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    /**
     * DBが作成されていない場合呼び出される関数
     * @param database
     */
    @Override
    public void onCreate(SQLiteDatabase database){
        database.execSQL(
                CREATE_TABLE
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          int oldVersion, int newVersion){
    }

    public void onDowngrade(SQLiteDatabase database,
                            int oldVersion, int newVersion){
    }
}