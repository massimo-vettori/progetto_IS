package com.lacliquep.barattopoli.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.lacliquep.barattopoli.R;
import com.lacliquep.barattopoli.classes.Exchange;
import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.classes.User;
import com.lacliquep.barattopoli.views.ItemView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ObjectFragment extends Fragment {

    protected LinearLayout container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_object, container, false);
        this.container = v.findViewById(R.id.main_scroller);

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

        String filter = "";
        Bundle b = getArguments();
        if (b != null) filter = b.getString("filter");
        if (filter.equals("Object")) retrieveItems(false,false, null);
        else if (filter.equals("Service")) retrieveItems(false, true, null);
        else if (filter.equals("Charity")) retrieveItems(true, false, null);
        else retrieveItems(false, false, null);
        //TODO:add filters
        return v;
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
}
