package com.example.myapplication;

import java.text.DecimalFormat;

/**
 * 件別明細のモデル
 */
public class DateListModel {
    private String date;
    private String railwayType;
    private String startStation;
    private String endStation;
    private int cost;
    private String costType;
    private String reason;

    /**
     * データをセットする
     * @param date 日付
     * @param railwayType　鉄道種類
     * @param startStation　区間（開始）駅
     * @param endStation　区間（終了）駅
     * @param cost　金額
     * @param costType　申請タイプ
     * @param reason　理由
     */
    public void setData(String date, String railwayType, String startStation, String endStation, int cost, String costType, String reason){
        this.date = date;
        this.railwayType = railwayType;
        this.startStation = startStation;
        this.endStation = endStation;
        this.cost = cost;
        this.costType = costType;
        this.reason = reason;
    }

    /**
     * 日付を返す
     * @return　日付
     */
    public String getDate(){
        return this.date;
    }

    /**
     * 鉄道の種類を返す
     * @return　鉄道種類
     */
    public String getStation(){
        return this.railwayType;
    }

    /**
     * 区間（開始）を返す
     * @return　区間（開始）駅
     */
    public String getStartStation(){
        return this.startStation;
    }

    /**
     * 区間（終了）を返す
     * @return
     */
    public String getEndStation(){
        return this.endStation;
    }

    /**
     * フォーマットを通した金額を返す
     * @return　金額
     */
    public String getCost(){
        DecimalFormat yenFormat = new DecimalFormat("\u00A5###,###");
        return yenFormat.format(this.cost);
    }

    /**
     * 申請タイプを返す
     * @return　申請タイプ
     */
    public String getType(){
        return this.costType;
    }

    /**
     * 理由を返す
     * @return　理由
     */
    public String getReason(){
        return this.reason;
    }

}
