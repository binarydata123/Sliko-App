package app.sliko.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by rajat singh on 1/6/17.
 */

public class SsBoldEditText extends EditText {
    public SsBoldEditText(Context context) {
        super(context);
    }

    public SsBoldEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SsBoldEditText(Context context, AttributeSet attrs) {
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
