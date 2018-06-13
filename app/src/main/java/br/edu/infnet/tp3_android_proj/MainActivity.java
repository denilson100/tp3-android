package br.edu.infnet.tp3_android_proj;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.edu.infnet.tp3_android_proj.interfaces.SmsListener;
import br.edu.infnet.tp3_android_proj.util.Permissions;
import br.edu.infnet.tp3_android_proj.util.SmsBroadcastReceiver;
import br.edu.infnet.tp3_android_proj.util.PermissoesRunTime;
import br.edu.infnet.tp3_android_proj.util.ServiceMapa;
import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener,
        TextToSpeech.OnUtteranceCompletedListener {

    MapView mMapView;
    private GoogleMap googleMap;
    private LocationTracker tracker;
    private CoordinatorLayout coordinatorLayout;

    private SmsBroadcastReceiver mIntentReceiver;
    public static final String OTP_REGEX = "[0-9]{1,6}";

    private TextToSpeech tts = null;
    private String msg = "";
    private String numero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null) {
            Intent startingIntent = this.getIntent();
            msg = startingIntent.getStringExtra("MESSAGE");
            tts = new TextToSpeech(this, this);
        }

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("TAG", "Sem Permissao");
                        if (verifyPermission() != true) {
                            alertSemPermissao();
                        }
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);
                    zoomInPosition();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void zoomInPosition() {
        tracker = new LocationTracker(
                MainActivity.this,
                new TrackerSettings()
                        .setUseGPS(true)
                        .setUseNetwork(false)
                        .setUsePassive(false)
                        .setTimeBetweenUpdates(1000)) {

            @Override
            public void onLocationFound(Location location) {
                // Do some stuff when a new GPS Location has been found
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                LatLng loc = new LatLng(latitude, longitude);
                ServiceMapa.posicaoCameraMapa(googleMap, loc);
            }

            @Override
            public void onTimeout() {

            }


        };
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            showToast("Sem permissao");
        } else {
            tracker.startListening();

        }
    }

    public boolean verifyPermission(){
        // Verifica permissao, se nao tiver vai para activity de permissao
        boolean permissao;
        int permissionCheckLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionSMS = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS);

        if(permissionCheckLocation != PackageManager.PERMISSION_GRANTED && permissionSMS != PackageManager.PERMISSION_GRANTED){
            permissao = false;
        } else {
            permissao = true;
        }
        return permissao;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (verifyPermission() != true) {
            alertSemPermissao();
        } else {
            zoomInPosition();
        }
    }

    public void alertSemPermissao() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Precisamos de algumas permissões.", Snackbar.LENGTH_LONG)
                .setAction("PERMISSÃO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Permissions.verifyPermissions(MainActivity.this);
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts!=null) {
            tts.shutdown();
        }
    }

    @Override
    public void onInit(int status) {

        Locale locale = new Locale("pt_BR");
        tts.setLanguage(locale);
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        tts.shutdown();
        tts = null;
        finish();
    }
}
