package com.example.myapplication;

import java.text.NumberFormat;

/**
 * 月別一覧のモデル
 */
public class MonthListModel {
    private String year;
    private String month;
    private int moneyNum;

    /**
     * モデルにデータを格納する
     * @param yearNum
     * @param monthNum
     * @param moneyNum
     */
    public void setData(int yearNum, int monthNum, int moneyNum){
        this.year = String.valueOf(yearNum);
        this.month = String.valueOf(monthNum);
        this.moneyNum = moneyNum;
    }

    /**
     * 年の値を返す
     * @return
     */
    public String getYear(){
        return this.year;
    }

    /**
     * 月の値を返す
     * @return
     */
    public String getMonth(){
        return this.month;
    }

    /**
     * フォーマットして「円」を付けた月度分の金額を返す
     * @return
     */
    public String getMoneyTitle(){
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        return numberFormat.format(this.moneyNum) + "円";
    }
}
