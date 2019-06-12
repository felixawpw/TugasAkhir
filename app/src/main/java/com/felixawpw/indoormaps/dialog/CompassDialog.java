package com.felixawpw.indoormaps.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.navigation.Path;
import com.felixawpw.indoormaps.util.SensorHelper;
import com.google.common.graph.Graph;

import java.util.List;

public class CompassDialog {
    Dialog dialog;
    ImageView imageCompass;
    double currentDegree = 0;
    SensorHelper sensorHelper;
    public CompassDialog(Activity activity) {
        View v = activity.getLayoutInflater().inflate(R.layout.dialog_show_compass, null);
        imageCompass = v.findViewById(R.id.image_compass);
        sensorHelper = new SensorHelper(activity, this);

        dialog = new Dialog(activity, R.style.DialogNotDim);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        dialog.setContentView(v);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.RIGHT | Gravity.TOP);
    }

    public void rotateImage(double degree) {
        RotateAnimation ra = new RotateAnimation(
                (float)currentDegree,
                (float)-degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);

        imageCompass.startAnimation(ra);
        currentDegree = -degree;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        sensorHelper.destroy();
        dialog.dismiss();
    }
}
