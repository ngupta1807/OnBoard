package com.bookmyride.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by vinod on 8/22/2017.
 */
public class Bookings implements ClusterItem {

    LatLng mLatLng;
    String title;
    String snippet;

    public Bookings(LatLng mLatLng, String title, String snippet) {
        this.mLatLng = mLatLng;
        this.title = title;
        this.snippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mLatLng;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

}
