package cl.academia.academiaapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

public class MyServicePrueba extends Service {

    private MyTask myTask;

    public MyServicePrueba() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        this.myTask = new MyTask();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myTask.execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        this.myTask.cancel(true);
        Toast.makeText(this, "servicio terminado", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private class MyTask extends AsyncTask<String, String, String>{

        int cont;
        boolean isContinuar;

        @Override
        protected void onPreExecute() {
            this.cont = 0;
            this.isContinuar = true;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            System.out.println("********************* doInBackground ...");

            while(isContinuar){
                try {
                    publishProgress((cont++)+"");
                    Thread.sleep(5000);

                    if(cont == 4)
                        this.isContinuar = false;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            stopService(new Intent(getApplicationContext(), MyServicePrueba.class));

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(getApplicationContext(), "contador = " + cont, Toast.LENGTH_SHORT).show();
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            Toast.makeText(getApplicationContext(), "contador se detuvo en = " + cont, Toast.LENGTH_SHORT).show();
            super.onCancelled(s);
        }
    }
}
