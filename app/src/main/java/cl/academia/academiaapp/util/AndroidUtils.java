package cl.academia.academiaapp.util;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by miguel on 26-07-16.
 */
public class AndroidUtils {


    public static void enviarNotificacion(Context context, String mensaje , int drawableIcon ,Class<?> activity){
        int notificationId = 001;
        // construir el intento para la notificacion
        Intent viewIntent = new Intent(context, activity);
        viewIntent.putExtra("sss", "PARAMETRO");
        PendingIntent viewPendingIntent = PendingIntent.getActivity(context, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(drawableIcon)
                        .setContentTitle("Notificación")
                        .setContentText(mensaje)
                        .setContentIntent(viewPendingIntent);

        //Vibracion
        notificationBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        //LED
        //notificationBuilder.setLights(Color.RED, 3000, 3000);

        // obtener una instancia del servicio NotificationManager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // construir la notificacion con el notificationManager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public static String getDataStringByURL(String url){

        //PARA LAS SIGUIENTES DOS LINEAS: Google creo esto para informar de violaciones de politicas relacionadas con los hijos en ejecucion (Thread) y la maquina virtual (Dalvik)
        //Con esto se crea un alerta al violar dicha politica, se crea una traza de la pila de ejecucion (Stack Trace)
        //Siempre cuando accedemos a la red, debemos agregar estas dos lineas ...
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        HttpURLConnection conn = null;
        try{
            conn = (HttpURLConnection) (new URL(url)).openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            InputStream is = conn.getInputStream();

            BufferedReader buff = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = "-";

            while( (line = buff.readLine()) !=null ){
                sb.append(line).append(" \n");
            }

            return sb.toString().trim();

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static void mostrarProgressDialog(Context context){
        final ProgressDialog loadingdialog = ProgressDialog.show(context, "" , "Sincronizando, favor espere", true);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        loadingdialog.dismiss();
    }


    /**
     * METODO PARA SABER SI LA CONEXION ESTA ACTIVA
     * @return
     */
    public static boolean isConexionActiva(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
