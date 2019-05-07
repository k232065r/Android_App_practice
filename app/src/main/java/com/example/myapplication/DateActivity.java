package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 件別詳細画面のActivity
 */
public class DateActivity extends AppCompatActivity {
    @Override
    /**
     * menu\main.xmlの読み込み
     */
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.date, menu);
        return true;
    }
    /**
     * menu:menuの挙動設定
     */
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.item1:
                return goNextIntent(MonthActivity.class);
            case R.id.item3:
                return goNextIntent(EditActivity.class);
        }
        return  super.onOptionsItemSelected(item);
    }

    /**
     * 次に移動する画面のIntentの作成して移動する関数
     * @param activityClass Activityのクラス
     * @return boolean true
     */
    private boolean goNextIntent(Class activityClass){
        Intent monthIntent = new Intent(getApplication(), activityClass);
        startActivity(monthIntent);
        return true;
    }

    /**
     * 件別詳細画面の作成画面
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_detail);
        ListView dateListView = (ListView)findViewById(R.id.dateListView);

        UsedDatabase usedDatabase = new UsedDatabase();
        usedDatabase.init(getApplicationContext());
        boolean doMakeDatabase = usedDatabase.WriteDatabase();
        if (doMakeDatabase==false){
            return;
        }

        // MainActivityで投げられた値でフィルタをかける
        Intent intent = getIntent();

        /* 件別の一覧表示 */
        setListViews(usedDatabase, intent);

        // ロングタップで削除
        dateListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            /**
             * ListViewのアイテムがロングタップされたら削除確認ダイアログを開く
             * @param parent
             * @param view
             * @param position
             * @param id
             * @return boolean
             */
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                final String deleteId = getTextViewValue(view, R.id.idRow);
                final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(DateActivity.this);
                deleteDialog.setTitle("削除確認");
                deleteDialog.setMessage("削除していいですか？");
                deleteDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    /**
                     * 削除確認ダイアログでOKをクリックされたら
                     * 選択された項目を削除してListViewを更新する
                     * @param dialog
                     * @param which
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UsedDatabase usedDatabase = new UsedDatabase();
                        usedDatabase.init(getApplicationContext());
                        usedDatabase.WriteDatabase();
                        usedDatabase.Delete(deleteId);

                        // リストから削除
                        Intent intent = getIntent();
                        setListViews(usedDatabase, intent);
                    }
                });
                deleteDialog.setNegativeButton("キャンセル", null);
                deleteDialog.show();
                return true;
            }
        });

        // 件別詳細画面へ遷移
        dateListView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                /**
                 * ListViewのアイテムがクリックされたら
                 * ListViewの情報を編集画面に引数として渡す
                 * @param parent
                 * @param view
                 * @param position
                 * @param id
                 */
                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    Intent editIntent = new Intent(getApplication(), EditActivity.class);
                    editIntent.putExtra("_id", getTextViewValue(view, R.id.idRow));
                    editIntent.putExtra("date", getTextViewValue(view, R.id.dateRow));
                    editIntent.putExtra("station", getTextViewValue(view, R.id.stationRow));
                    editIntent.putExtra("startStation", getTextViewValue(view, R.id.startStationRow));
                    editIntent.putExtra("endStation", getTextViewValue(view, R.id.endStationRow));
                    editIntent.putExtra("cost", getTextViewValue(view, R.id.costDataRow));
                    editIntent.putExtra("type", getTextViewValue(view, R.id.typeRow));
                    editIntent.putExtra("reason", getTextViewValue(view, R.id.reasonRow));
                    startActivity(editIntent);
                }
            }
        );
    }

    /**
     * ListViewを作成して、ListViewの情報を取得して描画する
     * @param usedDatabase データベースアクセスクラス
     * @param intent　表示する画面のIntent
     */
    private void setListViews(UsedDatabase usedDatabase, Intent intent){
        ListView dateListView = (ListView)findViewById(R.id.dateListView);
        ArrayList<HashMap<String, String>> list_data = new ArrayList<HashMap<String, String>>();
        list_data = setDateListData(usedDatabase, intent, list_data);
        SimpleAdapter adapter = new SimpleAdapter(
                getApplicationContext(),
                list_data,
                R.layout.row,
                new String[]{"_id", "_cost", "date", "station", "startStation", "endStation", "cost", "type", "reason"},
                new int[]{
                        R.id.idRow, R.id.costDataRow,
                        R.id.dateRow, R.id.stationRow,
                        R.id.startStationRow, R.id.endStationRow,
                        R.id.costRow, R.id.typeRow, R.id.reasonRow
                }
        );
        dateListView.setAdapter(adapter);
    }

    /**
     * SQLite3のデータベースファイルから指定された区間の交通費情報を取得して、ArrayListに格納する
     * @param usedDatabase　データベースアクセスクラス
     * @param intent　表示する画面のIntent
     * @param list_data　空のArrayList
     * @return ArrayList list_data DBの各明細の値を格納したリスト
     */
    private ArrayList setDateListData(UsedDatabase usedDatabase, Intent intent, ArrayList<HashMap<String, String>> list_data){
        int year = intent.getIntExtra("year", 0);
        int month = intent.getIntExtra("month", 0);
        LocalDate startDate = LocalDate.of(year, month, 1);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        int monthEndDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        LocalDate endDate = LocalDate.of(year, month, monthEndDate);

        final String[] columns = new String[]{"_id", "date", "station", "start_station","end_station", "cost", "type", "reason"};
        usedDatabase.getRecords(columns,null, null, "_id desc", null);

        HashMap<String, String> hashtmp = new HashMap<String, String>();
        try{
            while (usedDatabase.cursor.moveToNext()){
                String date = usedDatabase.cursor.getString(usedDatabase.cursor.getColumnIndex("date"));
                DateTimeFormatter databaseDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                LocalDate dateDate = LocalDate.parse(date, databaseDateTimeFormatter);
                if ( (dateDate.isBefore(startDate)) || (dateDate.isAfter(endDate)) ){
                    continue;
                }

                DateListModel tmp;
                tmp = new DateListModel();
                int idValue = usedDatabase.cursor.getInt(usedDatabase.cursor.getColumnIndex("_id"));
                String station = usedDatabase.cursor.getString(usedDatabase.cursor.getColumnIndex("station"));
                String startStation = usedDatabase.cursor.getString(usedDatabase.cursor.getColumnIndex("start_station"));
                String endStation = usedDatabase.cursor.getString(usedDatabase.cursor.getColumnIndex("end_station"));
                int cost = usedDatabase.cursor.getInt(usedDatabase.cursor.getColumnIndex("cost"));
                String type = usedDatabase.cursor.getString(usedDatabase.cursor.getColumnIndex("type"));
                String reason = usedDatabase.cursor.getString(usedDatabase.cursor.getColumnIndex("reason"));
                tmp.setData(
                        date, station, startStation, endStation,
                        cost, type, reason
                );
                hashtmp.put("_id", String.valueOf(idValue));
                hashtmp.put("_cost", String.valueOf(cost));
                hashtmp.put("date", tmp.getDate());
                hashtmp.put("station", tmp.getStation());
                hashtmp.put("startStation", tmp.getStartStation());
                hashtmp.put("endStation", tmp.getEndStation());
                hashtmp.put("cost", tmp.getCost());
                hashtmp.put("type", tmp.getType());
                hashtmp.put("reason", tmp.getReason());
                list_data.add(new HashMap<String, String>(hashtmp));
                hashtmp.clear();
            }
        } finally {
            usedDatabase.closeCursor();
        }
        return list_data;
    }

    /**
     * TextViewに記載しているテキストをStringで取得する
     * @param view 確認したいView
     * @param id 確認したいTextViewのid
     * @return String　TextViewの値
     */
    public String getTextViewValue(View view, int id){
        LinearLayout linear = (LinearLayout)view;
        TextView textView = (TextView)linear.findViewById(id);
        return  textView.getText().toString();
    }
}