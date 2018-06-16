package br.edu.infnet.tp3_android_proj.util;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class Alerts {


    public static void toast(Activity activity, String message){
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    public static void  snackbarFixe(String mensagem, CoordinatorLayout coordinatorLayout) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, mensagem, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

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

    public static void showSnackbar(Activity activity, String message){

        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();

    }

}
