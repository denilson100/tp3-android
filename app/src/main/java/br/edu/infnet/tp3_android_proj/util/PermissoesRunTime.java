package br.edu.infnet.tp3_android_proj.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import br.edu.infnet.tp3_android_proj.R;


/**
 * Created by denmont on 06/06/2018.
 */

public class PermissoesRunTime extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add slides, edit configuration...

        addSlide(new SimpleSlide.Builder()
                .title("Permissão de acesso a localização")
                .description("Precisamos desta informação para para determinadas funcões no app.")
                .image(R.drawable.pin_512)
                .background(R.color.branco)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                .build());

    }

    @Override
    protected void onPause() {
        super.onPause();
        int permissionCheckCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheckCamera == PackageManager.PERMISSION_GRANTED){
        }
    }

}
