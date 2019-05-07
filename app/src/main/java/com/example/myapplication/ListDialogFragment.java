package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * 履歴用のリストダイアログを表示するためのクラス
 */
public class ListDialogFragment extends DialogFragment{
    /**
     * ダイアログの作成
     * @param saveInstanceState
     * @return　リスト項目表示のダイアログ
     */
    public Dialog onCreateDialog(Bundle saveInstanceState){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        final String[] items = getArguments().getStringArray("items");
        dialogBuilder.setTitle("履歴");
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            /**
             * クリックしたアイテムをTextViewに格納する
             * @param dialog
             * @param which
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedValue = items[which];

                EditActivity editActivity = (EditActivity) getActivity();
                int viewId = getArguments().getInt("id");
                editActivity.setTextView(viewId, selectedValue);
            }
        });
        return dialogBuilder.create();
    }
}

