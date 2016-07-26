package cl.academia.academiaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cl.academia.academiaapp.service.MyServicePrueba;

public class ServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
    }

    public void enviarNotificacionWear(View view){
        //AndroidUtils.enviarNotificacion(this, "dsadas dsa d", R.drawable.common_google_signin_btn_icon_dark, MainActivity.class);
    }

    public void iniciarServicio(View view){
        startService(new Intent(this, MyServicePrueba.class));
    }

    public void detenerServicio(View view){
        stopService(new Intent(this, MyServicePrueba.class));
    }
}
