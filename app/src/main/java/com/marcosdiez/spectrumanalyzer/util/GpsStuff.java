package com.marcosdiez.spectrumanalyzer.util;


import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Globals;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Marcos on 12/20/13.
 */
public class GpsStuff implements LocationListener {
    private static final String TAG = "XB-GpsStuff";
    ////////
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static GpsStuff myGpsStuff = null;
    public double lat = 0;
    public double lng = 0;
    LocationManager locationManager;
    Criteria theCriteria = new Criteria();
    String provider;
    boolean useGps = true;

    private GpsStuff() {
        locationManager = (LocationManager) Globals.getContext().getSystemService(Globals.getContext().LOCATION_SERVICE);

        theCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        theCriteria.setAltitudeRequired(false);

        provider = locationManager.getBestProvider(theCriteria, true);
    }

    public static String distanceAsPrettyString(double distance) {
        if (distance < 1000) {
            return Math.round(distance) + "m";
        }
        if (distance < 10000) {
            return ((float) Math.round(distance / 100)) / 10.0 + "km";
        }
        return Math.round(distance / (1000)) + "km";
    }

    ///////

    public static double distanceBetween(double lat1, double lng1, double lat2, double lng2) {
        float result[] = {0};
        Location.distanceBetween(lat1, lng1, lat2, lng2, result);
        return result[0];
    }

    public static synchronized GpsStuff getMyGpsStuff() {
        if (myGpsStuff == null) {
            myGpsStuff = new GpsStuff();
        }
        return myGpsStuff;
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public String distanceFromHereStr(double toLat, double toLng) {
        double distance = distanceFromHere(toLat, toLng);
        return distanceAsPrettyString(distance);
    }

    public void setLocationToGps() {
        useGps = true;
    }

    public void setLocationManual(double lat, double lng) {
        useGps = false;
        this.lat = lat;
        this.lng = lng;
    }

    public void refreshLocation() {
        if (useGps) {
            Location newLocation = locationManager.getLastKnownLocation(provider);

            if (newLocation != null) {
                Location location = newLocation;
                if (location != null) {
                    lng = location.getLongitude();
                    lat = location.getLatitude();
                }
            }
        }
    }

    public String locationToAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(Globals.getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strReturnedAddress.append(returnedAddress.getCountryName()).append("\n");
                return strReturnedAddress.toString();
            } else {
                return "";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public double distanceFromHere(double toLat, double toLng) {
        refreshLocation();
        return distanceFromHereHelper(toLat, toLng);
    }

    double distanceFromHereHelper(double toLat, double toLng) {
        float result[] = {0};
        Location.distanceBetween(lat, lng, toLat, toLng, result);
        Log.d(TAG, "lat:" + lat + " " + " lng " + lng + " toLat " + toLat + " toLng " + toLng + " result " + result[0]);
        return result[0];
    }

    public void onLocationChanged(Location loc) {
        if (useGps) {
            lat = loc.getLatitude();
            lng = loc.getLongitude();
            Log.d(TAG, "New Location: Lat: " + loc.getLatitude() + " Lng:" + loc.getLongitude());
        }
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
