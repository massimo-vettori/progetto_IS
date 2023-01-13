package com.lacliquep.barattopoli.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lacliquep.barattopoli.ItemViewActivity;
import com.lacliquep.barattopoli.MainActivity;
import com.lacliquep.barattopoli.R;
import com.lacliquep.barattopoli.classes.BarattopoliUtil;
import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.classes.Ownership;
import com.lacliquep.barattopoli.classes.Range;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class ListItemView extends LinearLayout {
    private static final String TAG = "[ListItemView]";

    ImageView image;
    TextView  title;
    TextView  range;

    Item content;

    public static View createAndInflate(Context ctx, Item item, ViewGroup container) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.sample_list_item_view, container, false);

        TextView title        = itemView.findViewById(R.id.list_item_title);
        TextView range        = itemView.findViewById(R.id.list_item_range);
        ImageView image       = itemView.findViewById(R.id.list_item_image);
        TextView listItemTag1 = itemView.findViewById(R.id.list_item_tag1);
        TextView listItemTag2 = itemView.findViewById(R.id.list_item_tag2);
        Button chooseItem     = itemView.findViewById(R.id.choose_item);

        String thstring=null;
        Bitmap thumbnail  = item.getFirstImage();
        ArrayList<String> categories = new ArrayList<>(item.getCategories());

        title.setText(item.getTitle());
        //TODO: fix the view with the getString. At the moment is not possible!!!!
        range.setText(ItemViewActivity.rangeString(item, ctx));
        if (!categories.isEmpty()) {
            listItemTag1.setText(categories.get(0));
            if (categories.size() > 1) listItemTag2.setText(categories.get(1));
            else listItemTag2.setVisibility(View.INVISIBLE);
        } else {
            listItemTag1.setVisibility(View.INVISIBLE);
            listItemTag2.setVisibility(View.INVISIBLE);
        }
        if (thumbnail != null) {
            image.setImageBitmap(thumbnail);
        }

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ItemViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("ownership", Ownership.PERSONAL.toString());
            bundle.putString("caller", TAG);
//          (Failed attempt to serialize/deserialize the item)
//          bundle.putCharSequenceArray("item", Item.serialize(Item.getSampleItem()));
            bundle.putSerializable("item", item);
            intent.putExtras(bundle);
            view.getContext().startActivity(intent);
        });

        chooseItem.setOnClickListener(view -> {
            //TODO
        });

        return itemView;
    }


    public ListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initComponents();
        this.setClickable(true);
        init(context, attrs);
        Log.d("[ItemView]", "Created new ItemView");

        this.setOnClickListener();
    }

    public ListItemView(Context context) {
        super(context);
        this.initComponents();
        this.setClickable(true);
        init(context, null);
        Log.d("[ItemView]", "Created new ItemView");

        this.setOnClickListener();
    }

    private void init(Context ctx, AttributeSet attrs) {
        inflate(ctx, R.layout.view_item, this);
        if (attrs != null) {
            TypedArray arr = ctx.obtainStyledAttributes(attrs, R.styleable.ListItemView);

            String title = arr.getString(R.styleable.ListItemView_title);
            String range = arr.getString(R.styleable.ListItemView_range);
            String image = arr.getString(R.styleable.ListItemView_image);

            this.updateTitle(title);
            this.updateRange(range);
            this.updateImage(image);

            arr.recycle();
        }
    }

    public void setOnClickListener() {
        Log.d(TAG, "Added new onClickListener()");

        this.setOnClickListener(view -> {
            Log.d(TAG, "onClick: " + this.title.getText());
            Intent intent = new Intent(view.getContext(), ItemViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("ownership", Ownership.PERSONAL.toString());

        });
    }


    private void initComponents() {
        image = findViewById(R.id.list_item_image);
        range = findViewById(R.id.list_item_range);
        title = findViewById(R.id.list_item_title);
    }

    public void updateTitle(String text) {
        title.setText(text);
    }

    public void updateRange(String text) {
        range.setText(text);
    }

    public void updateImage(Bitmap image) {
        this.image.setImageBitmap(image);
    }

    public void updateImage(String encodedImage) {
        Bitmap image = BarattopoliUtil.decodeFileFromBase64(encodedImage);
        this.updateImage(image);
    }
}