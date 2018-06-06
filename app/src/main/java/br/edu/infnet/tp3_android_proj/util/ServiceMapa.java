package br.edu.infnet.tp3_android_proj.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class ServiceMapa {

    /**
     * Recebe o mapa e o location para setar o zoom do mapa
     * @param mMap
     * @param location
     */
    public static void posicaoCameraMapa(GoogleMap mMap, Location location){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
//              .bearing(90)                // Sets the orientation of the camera to east
//              .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public static void posicaoCameraMapa(GoogleMap mMap, LatLng location){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(10).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
