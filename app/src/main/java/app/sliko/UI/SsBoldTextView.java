package app.sliko.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by rajat singh on 1/6/17.
 */

public class SsBoldTextView extends TextView {
    public SsBoldTextView(Context context) {
        super(context);
    }

    public SsBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SsBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/ss_bold.ttf");
            setTypeface(tf);
        }
    }
}
