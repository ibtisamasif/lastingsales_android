package com.example.muzafarimran.lastingsales.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;

/**
 * A simple text label view that can be applied as a "badge" to any given {@link View}.
 * This class is intended to be instantiated at runtime rather than included in XML layouts.
 *
 * @author Jeff Gilfelt
 */
public class ErrorScreenView extends LinearLayout {
    public ImageView imageView;
    public TextView textView;

    public ErrorScreenView(Context context) {
        super(context);
        // more stuff
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_error_screen, this, true);
        imageView = (ImageView) findViewById(R.id.delight_image);
        textView = (TextView) findViewById(R.id.delight_text);
    }

    public ErrorScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // more stuff
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_error_screen, this, true);
        imageView = (ImageView) findViewById(R.id.delight_image);
        textView = (TextView) findViewById(R.id.delight_text);
    }

    public void setErrorImage(int drawable) {
        imageView.setImageResource(drawable);
    }

    public void setErrorText(String text) {
        textView.setText(text);
    }

}
