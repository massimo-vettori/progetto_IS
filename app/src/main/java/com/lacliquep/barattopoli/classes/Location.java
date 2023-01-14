package com.lacliquep.barattopoli.classes;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.lacliquep.barattopoli.R;

import java.util.*;

/**
 * class to identify the user's location or an item location, in order to filter the displayed items
 * @author pares
 * @since 1.0
 */
public class Location {
    private final static Map<String, Map<String, Map<String, ArrayList<String>>>> countries = new HashMap<>();
    /**
     * the number of the basic elements about a location when stored in a User or Item
     */
    static final int INFO_LENGTH = 4;
    /**
     * the basic info elements about a location when stored in a User or Item
     */
    static final String INFO_PARAM = "country,region,province,city";


    private Location(){
        //only Italy for now
        countries.put("Italia", new HashMap<>());
        countries.get("Italia").put("Veneto", new HashMap<>());
        countries.get("Italia").get("Veneto").put("Venezia", new ArrayList<>(Arrays.asList("Venezia", "Mestre", "Marghera")));
    }

    /**
     * a list of the available countries at this application state of art
     * @return
     */
    private static ArrayList<String> getAvailableCountries() {
        return new ArrayList<>(countries.keySet());
    }
    /**
     * a list of the available regions for a provided country at this application state of art
     * @param country the specified country
     * @return a void arrayList ifthe country is not available
     */
    private static ArrayList<String> getAvailableRegionsForCountry(String country) {
        if (countries.containsKey(country)) return new ArrayList<>(countries.get(country).keySet());
        else return new ArrayList<>();
    }

    /**
     * a list of the available provinces for the specified region and country, at this application state of art
     * @param region the specified region
     * @param country the specified country
     * @return a void arrayList if the region or the country are not available
     */
    private static ArrayList<String> getAvailableProvincesForRegion(String region, String country) {
        if (countries.containsKey(country) && countries.get(country).containsKey(region)) return new ArrayList<>(countries.get(country).get(region).keySet());
        else return new ArrayList<>();
    }

    /**
     * a list of the available cities for the specified province, region and country, at this application state of art
     * @param region the specified region
     * @param country the specified country
     * @return a void arrayList if the province or the region or the country are not available
     */
    private static ArrayList<String> getAvailableCitiesForProvince(String province, String region, String country) {
        if (countries.containsKey(country) && countries.get(country).containsKey(region) && countries.get(country).get(region).containsKey(province))
            return new ArrayList<>(countries.get(country).get(region).get(province));
        else return new ArrayList<>();
    }

    private static void displayAlertLocation(Context context, String typeOfLocation, ArrayList<String> avail) {
        String alertMessage = "";
        ArrayList<String> availableToDisplay = new ArrayList<>();
        alertMessage += typeOfLocation + context.getString(R.string.wrong_choice);
        for (String a: avail) {
            alertMessage += ("\n" + a);
        }
        final String finalMessage = alertMessage;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //display the item preview and insert in database if yes is clicked
        builder.setMessage(finalMessage)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.understood), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }}).show();
    }

    /**
     * check if the provide country is available, at the state of art of this application
     * @param context the Activity/fragment where this method is being called from
     * @param country the specified country
     * @return true if the country is available
     */
    private static boolean checkCountry(Context context, String country) {
        boolean ok = true;
        ArrayList<String> avail = getAvailableCountries();
        for (String s: avail) Log.d("TEST", s);
        if (!(avail.contains(country))) {
            displayAlertLocation(context, context.getString(R.string.insert_country), avail);
            ok = false;
        }
        return ok;
    }
    /**
     * check if the provided region is available, at the state of art of this application
     * @param context the Activity/fragment where this method is being called from
     * @param country the specified country
     * @param region the specified region
     * @return true if the region in the specified country is available
     */
    private static boolean checkRegion(Context context, String country, String region) {
        boolean ok = true;
        ArrayList<String> avail = getAvailableRegionsForCountry(country);
            if (!(avail.contains(region))) {
                displayAlertLocation(context, context.getString(R.string.insert_region), avail);
                ok = false;
            }
        return ok;
    }
    /**
     * check if the provided province is available, at the state of art of this application
     * @param context the Activity/fragment where this method is being called from
     * @param country the specified country
     * @param region the specified region
     * @param province the specified province
     * @return true if the province in the specified region and country is available
     */
    private static boolean checkProvince(Context context, String country, String region, String province) {
        boolean ok = true;
        ArrayList<String> avail = getAvailableProvincesForRegion(region, country);
            if (!(avail.contains(province))) {
                displayAlertLocation(context, context.getString(R.string.insert_province), avail);
                ok = false;
            }
        return ok;
    }
    /**
     * check if the provided city is available, at the state of art of this application
     * @param context the Activity/fragment where this method is being called from
     * @param country the specified country
     * @param region the specified region
     * @param province the specified province
     * @param city the specified city
     * @return true if the city  in the specified province, region and country is available
     */
    private static boolean checkCity(Context context, String country, String region, String province, String city) {
        boolean ok = true;
        ArrayList<String> avail = getAvailableCitiesForProvince(province, region, country);
            if (!(avail.contains(city))) {
                displayAlertLocation(context, context.getString(R.string.insert_city), avail);
                ok = false;
            }
        return ok;
    }

    /**
     * check if the provided location (i.e. city, province, region and location) is available, at the state of art of this application
     * @param context the Activity/fragment where this method is being called from
     * @param country the specified country
     * @param region the specified region
     * @param province the specified province
     * @param city the specified city
     * @return true if the city, province, region and country are available
     */
    public static boolean checkLocation(Context context, String country, String region, String province, String city) {
        boolean ok = true;
        Location l = new Location();
        if (!checkCountry(context, country)) ok = false;
        else {
            if (!checkRegion(context, country, region)) ok = false;
            else {
                if (!checkProvince(context, country, region, province)) ok = false;
                else {
                    if (!checkCity(context, country, region, province, city)) ok = false;
                }
            }
        }
        return ok;
    }



}
