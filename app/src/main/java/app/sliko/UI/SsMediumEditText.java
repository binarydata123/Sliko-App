package app.sliko.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by rajat singh on 1/6/17.
 */

public class SsMediumEditText extends EditText {
    public SsMediumEditText(Context context) {
        super(context);
    }

    public SsMediumEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SsMediumEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/ss_medium.ttf");
            setTypeface(tf);
        }
    }
}
