package com.lacliquep.barattopoli.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lacliquep.barattopoli.ItemViewActivity;
import com.lacliquep.barattopoli.R;
import com.lacliquep.barattopoli.classes.BarattopoliUtil;
import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.classes.Ownership;

public class ItemView extends ConstraintLayout {
    String title = "";

    ImageView image;
    TextView  bottom;
    TextView  top;

    public static View createAndInflate(Context ctx, Item item, @Nullable ViewGroup container) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.view_item, container, false);

        view.setClickable(true);
        view.setFocusable(true);

        view.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, ItemViewActivity.class);
            intent.putExtra("item", Item.serialize(item));
            ctx.startActivity(intent);
        });

        TextView userName = view.findViewById(R.id.user_name);
        ImageView avatar  = view.findViewById(R.id.user_avatar);
        ImageView image   = view.findViewById(R.id.image_container);
        TextView title    = view.findViewById(R.id.item_title);
        TextView price    = view.findViewById(R.id.item_price_range);

        userName.setText(item.getOwner().toString());
        title.setText(item.getTitle());
        price.setText(item.getIdRange());

        return view;
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setClickable(true);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        init(context, attrs);
        Log.d("[ItemView]", "Created new ItemView");
    }

    private void init(Context ctx, AttributeSet attrs) {
        inflate(ctx, R.layout.view_item, this);
        if (attrs == null) return;
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
