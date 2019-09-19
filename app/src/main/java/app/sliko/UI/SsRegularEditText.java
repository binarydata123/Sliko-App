package app.sliko.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by rajat singh on 1/6/17.
 */

public class SsRegularEditText extends EditText {
    public SsRegularEditText(Context context) {
        super(context);
    }

    public SsRegularEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SsRegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/ss_regular.ttf");
            setTypeface(tf);
        }
    }
}
