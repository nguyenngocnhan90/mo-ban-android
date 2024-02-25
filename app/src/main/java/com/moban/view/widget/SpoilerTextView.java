package com.moban.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

import com.moban.R;
import com.moban.util.Font;
import com.moban.util.StringUtil;

public class SpoilerTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final int DEFAULT_TRIM_LENGTH = 200;
    private static final String ELLIPSIS = "\u2026";

    private CharSequence originalText;
    private CharSequence trimmedText;
    private BufferType bufferType;
    boolean trim = true;
    private int trimLength;
    private ViewMoreSpan viewMoreSpan;
    private String strEllipsisReadMore;

    public SpoilerTextViewHandler spoilerTextViewHandler;

    public SpoilerTextView(Context context) {
        this(context, null);
    }

    public SpoilerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpoilerTextView);
        this.trimLength = typedArray.getInt(R.styleable.SpoilerTextView_trimLength, DEFAULT_TRIM_LENGTH);
        typedArray.recycle();

        initView(context);
    }

    private void initView(Context context) {
        viewMoreSpan = new ViewMoreSpan();
        setMovementMethod(LinkMovementMethod.getInstance());
        strEllipsisReadMore = ELLIPSIS + context.getString(R.string.read_more);
    }

    private void setText() {
        super.setText(getDisplayableText(), bufferType);
    }

    private CharSequence getDisplayableText() {
        return trim ? trimmedText : originalText;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        trimmedText = getTrimmedText(text);
        bufferType = type;
        setText();
    }

    private CharSequence getTrimmedText(CharSequence text) {
        if (originalText != null && trimLength > 0 && originalText.length() > trimLength && trim) {
            SpannableStringBuilder spannable = new SpannableStringBuilder(originalText, 0, trimLength + 1).append(strEllipsisReadMore);
            spannable.setSpan(viewMoreSpan, spannable.length() - strEllipsisReadMore.length(), spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable;
        } else {
            return originalText;
        }
    }

    public CharSequence getOriginalText() {
        return originalText;
    }

    public void setTrimLength(int trimLength) {
        this.trimLength = trimLength;
        trimmedText = getTrimmedText(originalText);
        setText();
    }

    public int getTrimLength() {
        return trimLength;
    }

    private class ViewMoreSpan extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            if (trim) {
                trim = !trim;
                trimLength = originalText.length();
                setText();

                if (spoilerTextViewHandler != null) {
                    spoilerTextViewHandler.onExpandText();
                }
            }
//            requestFocusFromTouch();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(StringUtil.Companion.getColor(getContext(), R.color.color_black));
            Typeface bold = Font.Companion.boldTypeface(getContext());
            ds.setTypeface(bold);
            //ds.setUnderlineText(true);
        }
    }

    public void setSpoilerTextViewHandler(SpoilerTextViewHandler spoilerTextViewHandler) {
        this.spoilerTextViewHandler = spoilerTextViewHandler;
    }

    public boolean isTrim() {
        return trim;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public interface SpoilerTextViewHandler {
        void onExpandText();
    }
}
