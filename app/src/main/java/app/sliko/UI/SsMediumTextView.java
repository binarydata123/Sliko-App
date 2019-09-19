package app.sliko.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by rajat singh on 1/6/17.
 */

public class SsMediumTextView extends TextView {
    public SsMediumTextView(Context context) {
        super(context);
    }

    public SsMediumTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SsMediumTextView(Context context, AttributeSet attrs) {
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
