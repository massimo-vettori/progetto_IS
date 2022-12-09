package com.lacliquep.barattopoli.classes;

import java.util.*;

/**
 * class to identify the user's location or an item location, in order to filter the displayed items
 * @author pares
 * @since 1.0
 */
public class Location {
    private final static ArrayList<String> countries = new ArrayList<>(1);
    private final static ArrayList<ArrayList<String>> regions = new ArrayList<>(1);
    private final static ArrayList<ArrayList<ArrayList<String>>> provinces = new ArrayList<>(1);
    private final static ArrayList<ArrayList<ArrayList<ArrayList<String>>>> cities = new ArrayList<>(1);

    /**
     * the number of the basic elements about a location when stored in a User or Item
     */
    public static final int INFO_LENGTH = 4;
    /**
     * the basic info elements about a location when stored in a User or Item
     */
    public static final String INFO_PARAM = "country,region,province,city";

    static {
        //only Italy for now
        countries.set(0,"Italia");
        regions.set(0, new ArrayList<>());
        regions.get(0).addAll(Arrays.asList("Abruzzo", "Basilicata", "Calabria", "Campania", "Emilia-Romagna"));
        regions.get(0).addAll(Arrays.asList("Friuli Venezia Giulia", "Lazio", "Liguria", "Lombardia", "Marche"));
        regions.get(0).addAll(Arrays.asList("Molise", "Piemonte", "Puglia", "Sardegna", "Sicilia"));
        regions.get(0).addAll(Arrays.asList("Toscana", "Trentino-Alto Adige", "Umbria", "Valle d'Aosta", "Veneto"));

        provinces.set(0, new ArrayList<>());
        provinces.get(0).set(0,new ArrayList<>(20));
        //only Veneto for now
        provinces.get(0).set(19,new ArrayList<>());
        provinces.get(0).get(19).addAll(Arrays.asList("Belluno", "Padova", "Rovigo", "Treviso", "Venezia", "Verona", "Vicenza"));

        //only Venezia for now
        cities.set(0, new ArrayList<>());
        cities.get(0).set(0,new ArrayList<>(20));
        cities.get(0).set(19, new ArrayList<>(7));
        cities.get(0).get(19).set(4, new ArrayList<>());
        //only a couple of cities for now
        cities.get(0).get(19).get(4).addAll(Arrays.asList("Venezia", "Mestre", "Marghera"));
    }

    //dedicated Exception
    public static class noSuchLocationException extends Exception {
        public noSuchLocationException(String s) {
            super(s);
        }
    }

    //dedicated Exception
    public static class noSuchCountryException extends noSuchLocationException {
        public noSuchCountryException(String s) {
            super("Country: " + s + " does not exist or is not available");
        }
    }

    //dedicated Exception
    public static class noSuchRegionException extends noSuchCountryException {
        public noSuchRegionException(String s) {
            super("Region: " + s + " does not exist or is not available");
        }
    }

    //dedicated Exception
    public static class noSuchProvinceException extends noSuchRegionException {
        public noSuchProvinceException(String s) {
            super("Province: " + s + " does not exist or is not available");
        }
    }

    public static ArrayList<String> getAvailableCountries() {
        return countries;
    }

    public static ArrayList<String> getAvailableRegionsForCountry(String country) throws noSuchCountryException {
        if (country == null) throw new noSuchCountryException("NULL");
        ArrayList<String> avCountries = getAvailableCountries();
        if (!(avCountries.contains(country))) throw new noSuchCountryException(country);
        else {
            int i = 0;
            boolean found = false;
            ArrayList<String> res = new ArrayList<>();
            while (i++ < avCountries.size() && !found) {
                if (avCountries.get(i).equals(country)) {
                    found = true;
                    res = regions.get(i);
                }
            }
            //return res;
            //return only Veneto for now
            return new ArrayList<>(Collections.singletonList("Veneto"));
        }
    }

    public static ArrayList<String> getAvailableProvincesForRegion(String region, String country) throws noSuchRegionException {
        if (region == null) throw new noSuchRegionException("NULL");
        ArrayList<String> avRegions = new ArrayList<>();
        try {
            avRegions = getAvailableRegionsForCountry(country);
        } catch (noSuchCountryException e) {
            throw new noSuchRegionException(region + " (country: " + country + ")");
        }
        if (!(avRegions.contains(region))) throw new noSuchRegionException(region + " (country: " + country + ")");
        else {
            int i = 0;
            boolean found = false;
            ArrayList<String> res = new ArrayList<>();
            while (i++ < avRegions.size() && !found) {
                if (avRegions.get(i).equals(country)) {
                    found = true;
                    int countryIndex = country.indexOf(country);
                    int regionIndex = regions.get(countryIndex).indexOf(region);
                    res = provinces.get(countryIndex).get(regionIndex);
                }
            }
            //return res;
            //return only Venezia for now
            return new ArrayList<>(Collections.singletonList("Venezia"));
        }
    }

    public static ArrayList<String> getAvailableCitiesForProvince(String province, String region, String country) throws noSuchProvinceException {
        if (province == null) throw new noSuchProvinceException("NULL");
        ArrayList<String> avProvinces = new ArrayList<>();
        try {
            avProvinces = getAvailableProvincesForRegion(region, country);
        } catch (noSuchCountryException e) {
            throw new noSuchProvinceException(province + " (region: " + region + ", country: " + country + ")");
        }
        if (!(avProvinces.contains(region))) throw new noSuchProvinceException(province + " (region: " + region + ", country: " + country + ")");
        else {
            int i = 0;
            boolean found = false;
            ArrayList<String> res = new ArrayList<>();
            while (i++ < avProvinces.size() && !found) {
                if (avProvinces.get(i).equals(country)) {
                    found = true;
                    int countryIndex = country.indexOf(country);
                    int regionIndex = regions.get(countryIndex).indexOf(region);
                    int provinceIndex = provinces.get(countryIndex).get(regionIndex).indexOf(province);
                    res = cities.get(countryIndex).get(regionIndex).get(provinceIndex);
                }
            }
            //return res;
            //return only a couple of cities for now
            return new ArrayList<>(Arrays.asList("Venezia", "Mestre", "Marghera"));
        }
    }


}
