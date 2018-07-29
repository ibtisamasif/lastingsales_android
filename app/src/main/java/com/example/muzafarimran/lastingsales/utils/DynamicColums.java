package com.example.muzafarimran.lastingsales.utils;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;

public class DynamicColums {


    Context context;

    public DynamicColums(Context context) {
        this.context = context;
    }


    public TextView textView(String value, String tag) {

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView textView = new TextView(this.context);
        textView.setLayoutParams(layoutParams);
        textView.setText(" " + value + " ");
        textView.setTextSize(13);
        textView.setMinWidth(200);
        textView.setTag(tag);
        textView.setPadding(20, 20, 20, 20);

        return textView;


    }

    public EditText editText(String val, String tag, int type) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        EditText editText = new EditText(this.context);
        editText.setMinimumWidth(300);
        editText.setTag(tag);
        editText.setText(val);
        editText.setTextSize(15);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(true);
        editText.setInputType(type);

        editText.setPadding(20, 20, 20, 20);
        return editText;
    }

    public Spinner spinner(ArrayAdapter<String> values, String tag, int position) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        Spinner spinner = new Spinner(this.context);
        spinner.setAdapter(values);


        spinner.setSelection(position);


        spinner.setBackgroundResource(R.drawable.spinner_back);
        spinner.setMinimumWidth(300);
        spinner.setTag(tag);
        return spinner;
    }


}
