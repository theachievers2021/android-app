package com.example.localhub.entities;

import android.app.Activity;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import com.example.localhub.R;

public class LoadingDialog {

    private AlertDialog alertDialog;

    public LoadingDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog,null));
        builder.setCancelable(false);
        alertDialog = builder.create();
    }

    public void startLoadingDialog(){
        alertDialog.show();
    }

    public void dismissDialog(){
        alertDialog.dismiss();
    }

    public boolean isShowing() {
        return alertDialog.isShowing();
    }
}