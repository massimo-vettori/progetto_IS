package com.lacliquep.barattopoli.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.lacliquep.barattopoli.R;
import com.lacliquep.barattopoli.classes.Exchange;
import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.classes.User;
import com.lacliquep.barattopoli.fragments.sign.InsertNewUserFragment;
import com.lacliquep.barattopoli.views.ItemView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ObjectFragment extends Fragment {

    protected LinearLayout container;
    protected ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_object, container, false);
        this.container = v.findViewById(R.id.main_scroller);
        this.progressBar = v.findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.INVISIBLE);


        //test on exchange
        //Exchange.insertExchangeInDatabase("ObjectFragment", "a6744f72-50ae-4aa0-b220-ff9acfb8b292", "0cc03c4a-2d8d-4fa9-8e44-5d60ccf631b9");
        Exchange.retrieveExchangeById("ObjectFragment", "fd79b61e-b045-4d18-8a77-b9d822fbfa36", new Consumer<Exchange>() {
            @Override
            public void accept(Exchange exchange) {
                Log.d("72", "id: " + exchange.getIdExchange());
                Log.d("72", "proposer: " + exchange.getProposer().getUserBasicInfo());
                Log.d("72", "applicant: " + exchange.getApplicant().getUserBasicInfo());
                Log.d("72", "firstProposedItem: " + exchange.getProposerItems().get(0).getItemBasicInfo());
                Log.d("72", "firstApplicantItem: " + exchange.getApplicantItems().get(0).getItemBasicInfo());
                Log.d("72", "exchangeStatus: " + exchange.getExchangeStatus().toString());
                Log.d("72", "date: " + exchange.getDate());
            }
        });

        /*
        For bottom bar
        ImageButton home = (ImageButton) v.findViewById(R.id.bottom_navigator_home);
        home.setBackgroundColor(Color.BLUE);*/

        //this.addItem(Item.getSampleItem());
        //this.addItem(Item.getSampleItem());
        //this.addItem(Item.getSampleItem());
        //this.addItem(Item.getSampleItem());
        //this.addItem(Item.getSampleItem());

        asyncShow();
        return v;
    }

    public void showItems() {
        //TODO:add filters
        String filter = "";
        Bundle b = getArguments();
        if (b != null) filter = b.getString("filter");
        if (filter.equals("Object")) retrieveItems(false,false, null);
        else if (filter.equals("Service")) retrieveItems(false, true, null);
        else if (filter.equals("Charity")) retrieveItems(true, false, null);
        else retrieveItems(false, false, null);
    }

    public void addItem(Item item) {
        View view = ItemView.createAndInflate(this.getContext(), item, container);
        container.addView(view);
    }

    protected void retrieveItems(boolean charity, boolean service, String category) {
        Item.retrieveMapWithAllItems(charity,service, category, false, FirebaseAuth.getInstance().getUid(), new ArrayList<String>(Arrays.asList("Italia", "Veneto", "Venezia", "Venezia")), new Consumer<Map<String, Item>>() {
            @Override
            public void accept(Map<String, Item> stringMapMap) {
                Log.d("61ObjectFragment", stringMapMap.toString());
                for(String idItem: stringMapMap.keySet()) {
                    Log.d("63", stringMapMap.get(idItem).getIdRange());
                    Log.d("63", stringMapMap.get(idItem).getIdItem());
                    Log.d("63", stringMapMap.get(idItem).getDescription());
                    Log.d("63", stringMapMap.get(idItem).getItemBasicInfo());
                    Log.d("63", stringMapMap.get(idItem).getTitle());
                    Log.d("63", "charity: "+String.valueOf(stringMapMap.get(idItem).isCharity()));
                    Log.d("63", "setvice: " + String.valueOf(stringMapMap.get(idItem).isCharity()));
                    Log.d("63", stringMapMap.get(idItem).getCategories().toString());
                    Log.d("63", stringMapMap.get(idItem).getImages().toString());
                    Log.d("63", stringMapMap.get(idItem).getLocation().toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addItem(stringMapMap.get(idItem));
                        }
                    });
                }
            }
        });
    }
    /**
     * check the SDK version in order to handle the itemView in background
     */
    void asyncShow() {
        // TODO find out which is the eldest SDK version accepting concurrent
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            // Do something for R and above versions
            //using concurrent executors
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                //Background work here
                showItems();
                handler.post(() -> {
                    //UI Thread work here
                    // TODO change the string
                    progressBar.setVisibility(View.VISIBLE);
                });
            });

        } else {
            new ObjectFragment.AsyncRegister().execute();
        }
    }
    /**
     * class to handle registration in asynchronous way before SDK R
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncRegister extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            showItems();
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
