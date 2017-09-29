package com.example.wirecamp.activity.handlers;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * Created by Pramod on 27-09-2017.
 */
public class ProgressBarHandler {
    private ProgressBar mProgressBar;
    private ProgressDialog progressDialog;
    private boolean isDialog;

    public ProgressBarHandler(Context context, boolean isDialog) {
        this.isDialog = isDialog;
        if (isDialog) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please Wait... (This will take some time)");
        } else {
            if (((AppCompatActivity) context).findViewById(android.R.id.content)!=null) {
                ViewGroup layout = (ViewGroup) ((AppCompatActivity) context).findViewById(android.R.id.content).getRootView();
                mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyle);
                mProgressBar.setIndeterminate(true);
                RelativeLayout.LayoutParams params = new
                        RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                RelativeLayout rl = new RelativeLayout(context);
                rl.setGravity(Gravity.CENTER);
                rl.addView(mProgressBar);
                layout.addView(rl, params);
                hide();
            }
        }
    }

    public void show() {
        if (isDialog) {
            progressDialog.show();
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hide() {
        if (isDialog) {
            progressDialog.dismiss();
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
