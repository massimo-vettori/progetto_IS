package com.lacliquep.barattopoli.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lacliquep.barattopoli.ItemViewActivity;
import com.lacliquep.barattopoli.R;
import com.lacliquep.barattopoli.classes.Exchange;
import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.classes.Ownership;

/**
 * TODO: document your custom view class.
 */
public class ExchangeItemView extends View {

    public static View createAndInflate(Context ctx, Exchange exchange, Ownership o, @Nullable ViewGroup container) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.sample_excange_item_view, container, false);

        TextView desc_sx   = view.findViewById(R.id.desc_sx);
        TextView desc_dx   = view.findViewById(R.id.desc_dx);
        TextView title_sx  = view.findViewById(R.id.title_sx);
        TextView title_dx  = view.findViewById(R.id.title_dx);
        ImageView image_sx = view.findViewById(R.id.image_sx);
        ImageView image_dx = view.findViewById(R.id.image_dx);

        ConstraintLayout status_container = view.findViewById(R.id.status_container); //TODO: set the status container color
        TextView status                   = view.findViewById(R.id.status_text);

        switch (o) {
            case PERSONAL:
                desc_sx.setText(R.string.your_object_service);
                desc_dx.setText(R.string.others_proposal);
                status.setOnClickListener(v -> {
                    // Creates a modal dialog to accept or reject the exchange
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setMessage("Vuoi accettare o rifiutare lo scambio?")
                           .setPositiveButton("Accetta", (dialog, id)   -> {/* TODO: Implement */})
                           .setNegativeButton("Rifiuta", (dialog, id)   -> {/* TODO: Implement */})
                           .setNeutralButton("Non adesso", (dialog, id) -> dialog.cancel())
                           .show();
                });

                break;
            case OTHER:
                desc_sx.setText(R.string.object_service);
                desc_dx.setText(R.string.your_object_service);

                status.setOnClickListener(v -> {
                    // Creates a modal dialog to accept or reject the exchange
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setMessage("Vuoi annullare lo scambio?")
                            .setPositiveButton("Si, annulla", (dialog, id)   -> {/* TODO: Implement */})
                            .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                            .show();
                });

                break;
        }

        status.setClickable(true);

        image_dx.setClickable(true);
        image_sx.setClickable(true);

        // TODO: Add default charity item
        if (!exchange.getApplicantItems().isEmpty()) {
            image_dx.setOnClickListener(v -> {
                Intent intent = new Intent(ctx, ItemViewActivity.class);
                Item item = exchange.getApplicantItems().get(0);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", item);
                intent.putExtras(bundle);
                ctx.startActivity(intent);
            });

            image_dx.setImageBitmap(exchange.getApplicantItems().get(0).getFirstImage());
            title_dx.setText(exchange.getApplicantItems().get(0).getTitle());
        }

        // TODO: Add default charity item
        if (!exchange.getProposerItems().isEmpty()) {
            image_sx.setOnClickListener(v -> {
                Intent intent = new Intent(ctx, ItemViewActivity.class);
                Item item = exchange.getProposerItems().get(0);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", item);
                intent.putExtras(bundle);
                ctx.startActivity(intent);
            });

            image_sx.setImageBitmap(exchange.getProposerItems().get(0).getFirstImage());
            title_sx.setText(exchange.getProposerItems().get(0).getTitle());
        }

        return view;
    }

    public ExchangeItemView(Context context) {
        super(context);
        init(null, 0);
    }

    protected void init(AttributeSet attrs, int defStyle) {
        // Load attributes

    }

}