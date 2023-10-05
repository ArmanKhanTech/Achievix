package com.android.achievix.View;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

public class ExpandableTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final int MAX_LINES_COLLAPSED = 5;
    private boolean isExpanded = false;

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        setEllipsize(TextUtils.TruncateAt.END);
        setMaxLines(MAX_LINES_COLLAPSED);
        setOnClickListener(v -> toggle());
    }

    public void toggle() {
        if (isExpanded) {
            setMaxLines(MAX_LINES_COLLAPSED);
        } else {
            setMaxLines(Integer.MAX_VALUE);
        }
        isExpanded = !isExpanded;
    }
}

