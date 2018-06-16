package br.edu.infnet.tp3_android_proj.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import br.edu.infnet.tp3_android_proj.R;
import br.edu.infnet.tp3_android_proj.util.AddressUtil;
import br.edu.infnet.tp3_android_proj.util.Alerts;
import br.edu.infnet.tp3_android_proj.util.Permissions;
import br.edu.infnet.tp3_android_proj.util.ServiceMapa;
import br.edu.infnet.tp3_android_proj.util.SmsUltil;
import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener,
        TextToSpeech.OnUtteranceCompletedListener {

    MapView mMapView;
    private GoogleMap googleMap;
    private LocationTracker tracker;
    private CoordinatorLayout coordinatorLayout;
    private Switch mSwirch;

    private TextToSpeech tts = null;
    private String msgCompleta = "";
    private String number;

    public static int INTENT_TEXT_SPEECH_CODE = 11;
    public static boolean STATUS_NAO_PERTUBE;
    private BroadcastReceiver receiver;
    private LatLng latLng;

    public static String MSG_CUSTOMIZADA = "Estou ocupado no momento!"; // menssagem sera custumozada pleo usuario.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mSwirch = (Switch) findViewById(R.id.myswitch);

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

        // Instacia um receptor de SMS
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ( MainActivity.STATUS_NAO_PERTUBE == true) {
                    if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                        Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
                        SmsMessage[] msgs = null;
                        String msg_from = null;
                        if (bundle != null) {
                            //---retrieve the SMS message received---
                            try {
                                Object[] pdus = (Object[]) bundle.get("pdus");
                                msgs = new SmsMessage[pdus.length];
                                String msgBody = null;
                                for (int i = 0; i < msgs.length; i++) {
                                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                                    msg_from = msgs[i].getOriginatingAddress();
                                    msgBody = msgs[i].getMessageBody();
                                    Log.d("TAG", "MSG: " + msgBody);
                                    Toast toast = Toast.makeText(context, "De: " + msg_from + " MSG: " + msgBody, Toast.LENGTH_LONG);
                                    toast.show();
                                }

                                // Monta a mensagem com o numero
                                msgCompleta = "Você recebeu uma mensagem de: " + msg_from + ", dizendo: " + msgBody;
                                number = msg_from;

                                // Inicia um intent para falar a string
                                Intent checkIntent = new Intent();
                                checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                                startActivityForResult(checkIntent, INTENT_TEXT_SPEECH_CODE);

                            } catch (Exception e) {
                                Log.d("Exception caught", e.getMessage());
                            }
                        }
                    }
                }
            }
        };

    }

    public void falar() {
        tts = new TextToSpeech(this, this);
        Locale locale = new Locale("pt_BR");
        tts.setLanguage(locale);
        tts.speak(msgCompleta, TextToSpeech.QUEUE_FLUSH, null);
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
                latLng = loc;
                ServiceMapa.posicaoCameraMapa(googleMap, loc);
                Log.d("TAG", "Location updade");
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

        // Inicia o filtro
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(this.receiver, filter);
    }

    public void onPause() {
        super.onPause();

        this.unregisterReceiver(this.receiver);
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

    /**
     * Iniciado quando uma intent TTS é chamada
     * @param status
     */
    @Override
    public void onInit(int status) {

        Locale locale = new Locale("pt_BR");
        tts.setLanguage(locale);
        tts.speak(msgCompleta, TextToSpeech.QUEUE_FLUSH, null);
        mensagemDeResposta();
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        tts.shutdown();
        tts = null;
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_TEXT_SPEECH_CODE) {
            tts = new TextToSpeech(this, this);
        }
    }

    public void mensagemDeResposta() {
        String address = AddressUtil.getAddress(this, latLng);
        String msg = "Msg de resposta ex: \n" + MSG_CUSTOMIZADA + "\n" + "Estou no endereço: " + address;
        Alerts.toast(this, msg);
    }

    public void clickSwitch(View view) {
        if (mSwirch.isChecked()) {
            Alerts.showSnackbar(this, "Modo não pertube ativo");
            STATUS_NAO_PERTUBE = true;
        } else {
            Alerts.showSnackbar(this, "Modo não pertube desativado");
            STATUS_NAO_PERTUBE = false;
        }
    }
}
