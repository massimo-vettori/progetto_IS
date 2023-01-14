package com.lacliquep.barattopoli;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.lacliquep.barattopoli.classes.Exchange;
import com.lacliquep.barattopoli.classes.Ownership;
import com.lacliquep.barattopoli.fragments.ObjectFragment;
import com.lacliquep.barattopoli.views.ExchangeItemView;

import java.security.acl.Owner;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeViewActivity extends AppCompatActivity {

    private Button       back;
    private LinearLayout scroller;
    private TextView     title;
    protected ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_view);

        Intent intent       = getIntent();
        Ownership ownership = Ownership.from(intent.getStringExtra("proposer"));

        back     = findViewById(R.id.back_button);
        scroller = findViewById(R.id.board_item_list);
        title    = findViewById(R.id.title);
        this.progressBar = findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.INVISIBLE);


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

        asyncShow(o);
    }

    protected void addItems(Ownership o) {
        progressBar.setVisibility(View.INVISIBLE);
        String user = FirebaseAuth.getInstance().getUid();
        Exchange.getUserExchanges(user, o.equals(Ownership.OTHER), false, exchange -> {
            Log.d("ExchangeViewActivity", "Got an exchange: " + exchange.getIdExchange());
            scroller.addView(ExchangeItemView.createAndInflate(this, exchange, o, scroller));
        });
    }
    /**
     * check the SDK version in order to handle the itemView in background
     */
    private void asyncShow(Ownership o) {
        // TODO find out which is the eldest SDK version accepting concurrent
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            // Do something for R and above versions
            //using concurrent executors
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                //Background work here
                addItems(o);
                handler.post(() -> {
                    //UI Thread work here
                    // TODO change the string
                    progressBar.setVisibility(View.VISIBLE);
                });
            });

        } else {
            new AsyncS().execute(o.toString());
        }
    }
    /**
     * class to handle the items show in asynchronous way before SDK R
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncS extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            addItems(Ownership.from(strings[0]));
            //TODO delete or improve publishProgress
            for (int i = 0; i < 100; ++i) publishProgress(i);
            return null;
        }
        // TODO: add a progression bar or sth? delete or improve onProgressUpdate
        protected void onProgressUpdate(Integer... integers) {
            //Toast.makeText(getActivity(), getString(R.string.in_progress), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(integers[0]);
        }

    }
}