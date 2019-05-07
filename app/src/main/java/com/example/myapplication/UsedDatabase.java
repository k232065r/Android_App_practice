package com.example.myapplication;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

/**
 * データベースにアクセスするクラス
 */
public class UsedDatabase {
    private SQLiteDatabase database;
    private static final String DATABASE_NAME = "TransportationExpensesDB.sqlite3";
    private static final int DATABASE_VERSION = 1;
    private MySQLiteOpenHelper helper;
    private Context context;
    Cursor cursor;

    /**
     * SQLヘルパーにアクセスして、内部に保持する
     * @param context
     */
    public void init(Context context) {
        this.context = context;
        this.helper = new MySQLiteOpenHelper(
                context, DATABASE_NAME,
                null, DATABASE_VERSION
        );
    }

    /**
     * 読み取り専用でデータベースを開く
     * @return boolean アクセスの成功の可否
     */
    public boolean ReadDatabase() {
        try {
            this.database = this.helper.getReadableDatabase();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * 読み書きできるデータベースを開く
     * @return boolean アクセスの成功の可否
     */
    public boolean WriteDatabase() {
        try {
            this.database = this.helper.getWritableDatabase();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * データベースに新規追加する
     * @param values　新規追加する値
     * @return boolean 新規追加の成功の可否
     */
    public boolean Insert(ContentValues values) {
        long id = this.database.insert(this.helper.TABLE_NAME, null, values);
        Toast toast;
        if (id < 0) {
            toast = Toast.makeText(
                    this.context,
                    "登録できませんでした",
                    Toast.LENGTH_LONG
            );
            toast.show();
            return false;
        } else {
            toast = Toast.makeText(
                    this.context,
                    "登録できました",
                    Toast.LENGTH_LONG
            );
            toast.show();
            return true;
        }
    }

    /**
     * データベースの値を更新する
     * @param values　更新する値
     * @return boolean 更新の成功の可否
     */
    public boolean Update(ContentValues values) {
        String id = values.getAsString("_id");
        int doUpdate = this.database.update(this.helper.TABLE_NAME, values, "_id=?", new String[]{id});
        Toast toast;
        if (doUpdate < 0) {
            toast = Toast.makeText(
                    this.context,
                    "更新できませんでした",
                    Toast.LENGTH_LONG
            );
            toast.show();
            return false;
        } else {
            toast = Toast.makeText(
                    this.context,
                    "更新できました",
                    Toast.LENGTH_LONG
            );
            toast.show();
            return true;
        }
    }

    /**
     * データベースの値を削除する
     * @param id　削除するアイテムのID
     * @return boolean 削除の成功の可否
     */
    public boolean Delete(String id) {
        int doDelete = this.database.delete(this.helper.TABLE_NAME, "_id=?",  new String[]{id});
        Toast toast;
        if (doDelete < 0) {
            toast = Toast.makeText(
                    this.context,
                    "削除できませんでした",
                    Toast.LENGTH_LONG
            );
            toast.show();
            return false;
        } else {
            toast = Toast.makeText(
                    this.context,
                    "削除できました",
                    Toast.LENGTH_LONG
            );
            toast.show();
            return true;
        }
    }

    /**
     * データベースの値を取得する（Select文）
     * @param columns　取得するテーブルの列名
     * @param selection　Where句（defalut null）
     * @param arguments　Where句の引数（defalut null）
     * @param orderBy　Order句（defalut null）
     * @param limit　limit数（defalut null）
     */
    public void getRecords(String[] columns, String selection, String[] arguments, String orderBy, String limit){
        this.cursor = database.query(
                this.helper.TABLE_NAME, columns, selection, arguments,
                null, null, orderBy, limit
        );
    }

    /**
     * cursorを終了する
     */
    public void closeCursor(){
        this.cursor.close();
    }
}