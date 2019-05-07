package com.example.myapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 編集画面のActivity
 */
public class EditActivity extends AppCompatActivity {
    /**
     * menu\main.xmlの読み込み
     * @param menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);
        return true;
    }
    /**
     * menu:menuの挙動設定
     * @param item
     * @return boolean
     */
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.item1:
                Intent monthIntent = new Intent(getApplication(), MonthActivity.class);
                startActivity(monthIntent);
                return true;
            case R.id.item3:
                Intent editIntent = new Intent(getApplication(), EditActivity.class);
                startActivity(editIntent);
                return true;
            case R.id.save:
                UsedDatabase usedDatabase = new UsedDatabase();
                usedDatabase.init(getApplicationContext());
                boolean doDatabaseMake = usedDatabase.WriteDatabase();
                if (doDatabaseMake == false){
                    return false;
                }
                final TextView idText = (TextView) findViewById(R.id.idText);
                final EditText dateEdit = (EditText) findViewById(R.id.dateText);
                final EditText stationEdit = (EditText) findViewById(R.id.stationText);
                final EditText startStationEdit = (EditText) findViewById(R.id.startStationText);
                final EditText endStationEdit = (EditText) findViewById(R.id.endStationText);
                final EditText costEdit = (EditText) findViewById(R.id.costText);
                final Spinner typeSpinner = findViewById(R.id.selectType);
                final EditText reasonEdit = (EditText) findViewById(R.id.reasonText);

                ContentValues values = new ContentValues();
                String id = idText.getText().toString();
                values.put("date", dateEdit.getText().toString());
                values.put("station", stationEdit.getText().toString());
                values.put("start_station", startStationEdit.getText().toString());
                values.put("end_station", endStationEdit.getText().toString());
                values.put("cost", costEdit.getText().toString());
                values.put("type", typeSpinner.getSelectedItem().toString());
                values.put("reason", reasonEdit.getText().toString());

                Intent dateIntent = new Intent(getApplication(), DateActivity.class);
                if (id == ""){
                    boolean doInsert = usedDatabase.Insert(values);
                    if (doInsert == true){
                        startActivity(dateIntent);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    values.put("_id", id);
                    boolean doUpdate = usedDatabase.Update(values);
                    if (doUpdate == true){
                        startActivity(dateIntent);
                        return true;
                    } else {
                        return false;
                    }
                }
        }
        return  super.onOptionsItemSelected(item);
    }

    /**
     * 編集画面の作成
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_detail);
        final TextView idText = (TextView) findViewById(R.id.idText);
        final EditText dateEdit = (EditText) findViewById(R.id.dateText);
        final EditText stationEdit = (EditText) findViewById(R.id.stationText);
        final EditText startStationEdit = (EditText) findViewById(R.id.startStationText);
        final EditText endStationEdit = (EditText) findViewById(R.id.endStationText);
        final EditText costEdit = (EditText) findViewById(R.id.costText);
        final Spinner typeSpinner = findViewById(R.id.selectType);
        final EditText reasonEdit = (EditText) findViewById(R.id.reasonText);

        // 申請区分の作成
        String[] typeDropDownItems = {
                "通勤",
                "出張",
                "その他"
        };
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                typeDropDownItems
        );
        typeSpinner.setAdapter(typeAdapter);
        if (typeSpinner.getSelectedItem().toString() == ""){
            int spinnerPosition = typeAdapter.getPosition("通勤");
            typeSpinner.setSelection(spinnerPosition);
        }

        // 更新の場合は値をデフォルト値に設定する
        Intent intent = getIntent();
        String id = intent.getStringExtra("_id");
        if (id != ""){
            idText.setText(id);
            dateEdit.setText(intent.getStringExtra("date"));
            stationEdit.setText(intent.getStringExtra("station"));
            startStationEdit.setText(intent.getStringExtra("startStation"));
            endStationEdit.setText(intent.getStringExtra("endStation"));
            costEdit.setText(intent.getStringExtra("cost"));
            int spinnerPosition = typeAdapter.getPosition(intent.getStringExtra("type"));
            typeSpinner.setSelection(spinnerPosition);
            reasonEdit.setText(intent.getStringExtra("reason"));
        }

        dateEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * 日付のバリデーションチェック
             * @param v
             * @param hasFocus
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!(hasFocus)){
                    String dateText = dateEdit.getText().toString();
                    try {
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                        dateTimeFormatter.format(LocalDate.parse(dateText, dateTimeFormatter));
                    } catch (DateTimeParseException e) {
                        errorHanding(
                                dateEdit,
                                "YYYY/MM/DD形式で入力してください",
                                "日付入力エラー"
                        );
                    }
                }
            }
        });
        costEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * 金額のバリデーションチェック
             * @param v
             * @param hasFocus
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!(hasFocus)){
                    String costText = costEdit.getText().toString();
                    try {
                        Integer.parseInt(costText);
                    } catch (NumberFormatException e) {
                        errorHanding(
                                costEdit,
                                "数値を入力してください",
                                "金額入力エラー"
                        );
                    }
                }
            }
        });

        // ボタンについて
        // 機関の履歴ボタン
        UsedDatabase usedDatabase = new UsedDatabase();
        usedDatabase.init(getApplicationContext());
        boolean doDatabaseMake = usedDatabase.ReadDatabase();
        if (doDatabaseMake == false){
            return;
        }
        final String[] columns = new String[]{"station", "start_station","end_station", "reason"};
        usedDatabase.getRecords(columns, null, null,  null, null);
        HashMap<String, ArrayList<String>> temporaryHistoryMap = new HashMap<String, ArrayList<String>>();
        temporaryHistoryMap.put("station", new ArrayList<String>());
        temporaryHistoryMap.put("start_station", new ArrayList<String>());
        temporaryHistoryMap.put("end_station", new ArrayList<String>());
        temporaryHistoryMap.put("reason", new ArrayList<String>());

        try{
            while (usedDatabase.cursor.moveToNext()){
                temporaryHistoryMap = addHistoryMap(usedDatabase, temporaryHistoryMap, "station");
                temporaryHistoryMap = addHistoryMap(usedDatabase, temporaryHistoryMap, "start_station");
                temporaryHistoryMap = addHistoryMap(usedDatabase, temporaryHistoryMap, "end_station");
                temporaryHistoryMap = addHistoryMap(usedDatabase, temporaryHistoryMap, "reason");

            }
        } finally {
            usedDatabase.closeCursor();
        }
        final HashMap<String, ArrayList<String>> historyMap = temporaryHistoryMap;
        Button stationDialogButton = (Button)findViewById(R.id.stationButton);
        stationDialogButton.setOnClickListener(new View.OnClickListener() {
            /**
             * 鉄道機関の履歴ボタンを押されたら履歴ダイアログを表示する
             * @param v
             */
            @Override
            public void onClick(View v) {
                makeDialog(historyMap, R.id.stationText, "station");
            }
        });
        Button startStationDialogButton = (Button)findViewById(R.id.startStationButton);
        startStationDialogButton.setOnClickListener(new View.OnClickListener() {
            /**
             * 区間（開始）の履歴ボタンを押されたら履歴ダイアログを表示する
             * @param v
             */
            @Override
            public void onClick(View v) {
                makeDialog(historyMap, R.id.startStationText, "start_station");
            }
        });
        Button endStationDialogButton = (Button)findViewById(R.id.endStationButton);
        endStationDialogButton.setOnClickListener(new View.OnClickListener() {
            /**
             * 区間（開始）の履歴ボタンを押されたら履歴ダイアログを表示する
             * @param v
             */
            @Override
            public void onClick(View v) {
                makeDialog(historyMap, R.id.endStationText, "end_station");
            }
        });
        Button reasonDialogButton = (Button)findViewById(R.id.reasonButton);
        reasonDialogButton.setOnClickListener(new View.OnClickListener() {
            /**
             * 理由の履歴ボタンを押されたら履歴ダイアログを表示する
             * @param v
             */
            @Override
            public void onClick(View v) {
                makeDialog(historyMap, R.id.reasonText , "reason");
            }
        });
    }

    /**
     * viewIdのTextViewにValueの値をセットする
     * @param viewId TextViewのID
     * @param value TextViewにセットする値
     */
    public void setTextView(int viewId, String value){
        TextView textView = (TextView) findViewById(viewId);
        textView.setText(value);
    }

    /**
     * 重複なしの値を５つ取得したArrayListをキーごとに格納する
     * @param usedDatabase　データベースアクセスクラス
     * @param historyMap　鉄道タイプ、区間（開始・終了）、理由ｂの4種のキーにリストを格納したHashMap
     * @param key HashMapのキー
     * @return　historyMap　鉄道タイプ、区間（開始・終了）、理由ｂの4種のキーにリストを格納したHashMap
     */
    public HashMap<String, ArrayList<String>> addHistoryMap(UsedDatabase usedDatabase, HashMap<String, ArrayList<String>> historyMap, String key){
        ArrayList<String> historyList = historyMap.get(key);
        if (historyList.size() == 5){
            return historyMap;
        }
        String value;
        value = usedDatabase.cursor.getString(usedDatabase.cursor.getColumnIndex(key));
        if (historyList.indexOf(value) < 0){
            historyList.add(value);
        }
        return historyMap;
    }

    /**
     * リストに格納された値をListViewのダイアログとして表示する
     * @param historyMap　鉄道タイプ、区間（開始・終了）、理由ｂの4種のキーにリストを格納したHashMap
     * @param id　値を代入する予定のTextViewのid（int）
     * @param idName　ダイアログの名前用（一意であれば何でもいい）
     */
    public void makeDialog(HashMap<String, ArrayList<String>> historyMap, @IdRes int id, String idName){
        ListDialogFragment dialog = new ListDialogFragment();
        Bundle arguments = new Bundle();
        ArrayList<String> itemsArrayList = historyMap.get(idName);
        String[] items = new String[itemsArrayList.size()];
        for (int index=0; index<itemsArrayList.size();index++){
            items[index]=itemsArrayList.get(index);
        }
        arguments.putStringArray("items", items);
        arguments.putInt("id", id);
        dialog.setArguments(arguments);
        String tagName = idName + "Text";
        dialog.show(getSupportFragmentManager(), tagName);
    }

    /**
     * エラー処理（setErrorとダイアログの表示）を行う
     * @param editText  エラーになったEditText
     * @param message エラー時に表示するメッセージ内容
     * @param title エラーダイアログのタイトル
     */
    public void errorHanding(EditText editText, String message, String title){
        editText.setError(message);

        new AlertDialog.Builder(EditActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

}
