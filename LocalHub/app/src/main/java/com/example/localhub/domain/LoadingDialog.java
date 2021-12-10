package com.example.localhub.domain;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.localhub.R;
import com.google.android.gms.dynamite.DynamiteModule;

public class LoadingDialog {

    private Activity activity;

    private AlertDialog alertDialog;

    public LoadingDialog(Activity activity){
        this.activity=activity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog,null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }
    public void dismissDialog(){
        alertDialog.dismiss();
    }
}
