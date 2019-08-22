package app.sliko.utills;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.github.ybq.android.spinkit.SpinKitView;

import java.util.Objects;

import app.sliko.R;


public class CustomProgress extends Dialog implements DialogInterface.OnDismissListener {
    private CustomProgress(Context context) {
        super(context);
    }

    public CustomProgress(Context context, int theme) {
        super(context, theme);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            SpinKitView progressBar = (SpinKitView) findViewById(R.id.spin_kit);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public static CustomProgress show(Context context, String message
            , boolean cancelable) {
        CustomProgress dialog = new CustomProgress(context);
        dialog.setTitle("");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_custom_progress);
        if (message.equals("1")) {
            dialog.setCancelable(false);
        } else {
            dialog.setCancelable(true);
        }
        Objects.requireNonNull(dialog.getWindow()).getAttributes().gravity = Gravity.CENTER;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface arg0) {
        System.out.println("dismiss is called");
    }
}
