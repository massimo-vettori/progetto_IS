package com.lacliquep.barattopoli;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.lacliquep.barattopoli.classes.Exchange;
import com.lacliquep.barattopoli.classes.Ownership;
import com.lacliquep.barattopoli.views.ExchangeItemView;

import java.util.Objects;

public class ExchangeViewActivity extends AppCompatActivity {

    private Button       back;
    private LinearLayout scroller;
    private TextView     title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_view);

        Intent intent       = getIntent();
        Ownership ownership = Ownership.from(intent.getStringExtra("proposer"));

        back     = findViewById(R.id.back_button);
        scroller = findViewById(R.id.board_item_list);
        title    = findViewById(R.id.title);

        // Removes the title bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        init(ownership);
    }

    protected void init(Ownership o) {
        back.setOnClickListener(v -> finish());

        switch (o) {
            case OTHER:
                title.setText(R.string.others_requests);
                break;
            case PERSONAL:
                title.setText(R.string.your_requests);
                break;
        }

        addItems(o);
    }

    protected void addItems(Ownership o) {
        String user = FirebaseAuth.getInstance().getUid();
        Exchange.getUserExchanges(user, o.equals(Ownership.OTHER), false, exchange -> {
            Log.d("ExchangeViewActivity", "Got an exchange: " + exchange.getIdExchange());
            scroller.addView(ExchangeItemView.createAndInflate(this, exchange, o, scroller));
        });
    }
}