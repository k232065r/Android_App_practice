package com.example.myapplication;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * 月別一覧のActivity
 */
public class MonthActivity extends AppCompatActivity {

    /**
     * menu\main.xmlの読み込み
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    /**
     * menu:menuの挙動設定
     * @param item
     * @return true
     */
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.item3:
                Intent editIntent = new Intent(getApplication(), EditActivity.class);
                startActivity(editIntent);
                return true;
        }
        return  super.onOptionsItemSelected(item);
    }

    /**
     * 初期画面（月別一覧画面）の作成
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 月別の一覧表示 */
        ArrayList<HashMap<String, String>> list_data = new ArrayList<HashMap<String, String>>();
        list_data = setMonthListData(list_data);

        final SimpleAdapter adapter = new SimpleAdapter(
                getApplicationContext(),
                list_data,
                R.layout.two_line_list_item,
                new String[]{"year", "month", "money"},
                new int[]{R.id.yearText, R.id.monthText, R.id.money_item}
                );

        ListView monthListView = (ListView)findViewById(R.id.monthListView);
        monthListView.setAdapter(adapter);

        monthListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    /**
                     *　件別詳細画面へ遷移
                     * @param parent
                     * @param view
                     * @param position
                     * @param id
                     */
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        Intent dateIntent = new Intent(getApplication(), DateActivity.class);
                        int year = Integer.parseInt(getTextViewValue(view, R.id.yearText));
                        int month = Integer.parseInt(getTextViewValue(view, R.id.monthText));
                        dateIntent.putExtra("year", year);
                        dateIntent.putExtra("month", month);
                        startActivity(dateIntent);
                    }
                }
        );
    }

    /**
     * 月別一覧のデータリストを作成する
     * @param list_data　空のデータリスト
     * @return　ArrayList　月別一覧のデータリスト
     */
    public ArrayList setMonthListData(ArrayList<HashMap<String, String>> list_data){
        TreeMap<String, int[]> hashtmp = new TreeMap<String, int[]>(new Comparator<String>() {
            /**
             * 降順に並べ替える
             * @param o1
             * @param o2
             * @return　比較結果
             */
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });
        UsedDatabase usedDatabase = new UsedDatabase();
        usedDatabase.init(getApplicationContext());
        boolean doMakeDatabase = usedDatabase.ReadDatabase();
        if (doMakeDatabase==false){
            return list_data;
        }
        final String[] columns = new String[]{"date", "cost"};
        usedDatabase.getRecords(columns, null, null, "date desc", null);

        try{
            while (usedDatabase.cursor.moveToNext()){
                String strDate = usedDatabase.cursor.getString(usedDatabase.cursor.getColumnIndex("date"));
                int cost = usedDatabase.cursor.getInt(usedDatabase.cursor.getColumnIndex("cost"));
                DateTimeFormatter databaseDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                DateTimeFormatter getDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
                LocalDate localDate = LocalDate.parse(strDate, databaseDateTimeFormatter);
                String yearMonth = getDateTimeFormatter.format(localDate);
                int year = localDate.getYear();
                int month = localDate.getMonthValue();
                if (hashtmp.containsKey(yearMonth)){
                    cost = hashtmp.get(yearMonth)[2] + cost;
                }
                hashtmp.put(yearMonth, new int[]{year, month, cost});
            }
        } finally {
            usedDatabase.closeCursor();
        }

        MonthListModel monthListClass;
        HashMap<String, String> data = new HashMap<String, String>();
        for (String key: hashtmp.keySet()){
            monthListClass = new MonthListModel();
            int[] dataList = hashtmp.get(key);
            monthListClass.setData(dataList[0], dataList[1], dataList[2]);
            data.put("month", monthListClass.getMonth());
            data.put("year", monthListClass.getYear());
            data.put("money", monthListClass.getMoneyTitle());
            list_data.add(new HashMap<String, String>(data));
            data.clear();
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
