package com.felixawpw.indoormaps.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.font.RobotoTextView;

public class LoadingDialog {
    Dialog loadingDialog;
    RobotoTextView textTitle, textMessage;
    public LoadingDialog(Activity activity, String title, String message) {
        View v = activity.getLayoutInflater().inflate(R.layout.dialog_navigation_loading, null);
        textTitle = v.findViewById(R.id.text_title);
        textMessage = v.findViewById(R.id.text_message);

        textTitle.setText(title);

        loadingDialog = new Dialog(activity, R.style.MaterialDialogSheet);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        loadingDialog.setContentView(v);
        loadingDialog.setCancelable(true);

        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
    }

    public void show() {
        loadingDialog.show();
    }

    public void hide() {
        loadingDialog.hide();
    }

    public void dismiss() {
        loadingDialog.dismiss();
    }
}
