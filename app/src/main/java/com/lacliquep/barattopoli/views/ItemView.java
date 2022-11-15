package com.lacliquep.barattopoli.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.lacliquep.barattopoli.R;

public class ItemView extends ConstraintLayout {
    String title = "";

    ImageView image;
    TextView  bottom;
    TextView  top;

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setClipToOutline(true);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        inflate(ctx, R.layout.view_item, this);
        TypedArray arr = ctx.obtainStyledAttributes(attrs, new int[]{ R.attr.url, R.attr.top, R.attr.bottom });

        initComponents();
        this.setItem(arr.getString(0), arr.getString(1), arr.getString(2));

        arr.recycle();
    }


    private void initComponents() {
        image  = findViewById(R.id.image_container);
        top    = findViewById(R.id.top_text);
        bottom = findViewById(R.id.bottom_text);
    }

    private void setTopText(String text) {
        top.setText(text);
    }

    private void setBottomText(String text) {
        bottom.setText(text);
    }

    private void setImage(String url) {
        // TODO: set image by its url
    }

    public void setItem(String url, String top, String bottom) {
        setImage(url);
        setTopText(top);
        setBottomText(bottom);
    }

    public void setItem(String top, String bottom) {
        setTopText(top);
        setBottomText(bottom);
    }

    public void setItem(String url) {
        setImage(url);
    }
}
