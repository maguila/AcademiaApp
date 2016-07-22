package cl.academia.academiaapp;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cl.academia.academiaapp.sqlLite.DataBaseHelper;
import cl.academia.academiaapp.sqlLite.UsuarioPojo;

public class HttpActivity extends AppCompatActivity {

    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //CREO EL LIST VIEW Y AGREGO VALORES AL ADAPTADOR DESDE UN JSON REMOTO
        if(isConexionActiva())
            initListViewDesdeJson();
        else{
            Toast.makeText(this, "No hay conexion, se debe traer datos de la base de datos ...", Toast.LENGTH_SHORT).show();

            ListView listaUsuarios = (ListView) findViewById(R.id.lista_usuarios);
            ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

            DataBaseHelper dbHelper = new DataBaseHelper(this, false);
            for(UsuarioPojo o : dbHelper.getAllUsuarios()){
                Log.d("LECTURA DE SQLite ... ", "NOMBRE  : " + o.getNombre() + ",  APELLIDO : " + o.getApellido());
                adapter.add(o.getNombre() + " " + o.getApellido() );
            }

            TextView text = (TextView) findViewById(R.id.cantidad_usuarios_text);
            text.setText(dbHelper.countUsuarios()+"");
            listaUsuarios.setAdapter(adapter);

        }


    }

    public void initListViewDesdeJson(){
        //INSTANCIAMOS NUESTRO LIST VIEW
        ListView listaUsuarios = (ListView) findViewById(R.id.lista_usuarios);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);


        //Google creo esto para informar de violaciones de politicas relacionadas con los hijos en ejecucion (Thread) y la maquina virtual (Dalvik)
        //Con esto se crea un alerta al violar dicha politica, se crea una traza de la pila de ejecucion (Stack Trace)
        //Siempre cuando accedemos a la red, debemos agregar estas dos lineas ...
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //LLAMAMOS UN JSON DESDE UN URL
        String url = "http://server2.solcloud.cl/academia/retorno.json";
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

            String jsonString = sb.toString().trim(); //Quitamos los espacion en blanco

            System.out.println(jsonString);

            JSONObject jsonObj = new JSONObject(sb.toString().trim()); //AHORA INSTANCIAMOS LA CLASE JSON OBJECT
            this.jsonArray = jsonObj.getJSONArray("usuarios"); // OBTENER EL ARREGLO JSON

            //RECORRERMOS EL JSON ARRAY
            for(int i = 0; i<jsonArray.length(); i++){
                JSONObject o    = jsonArray.getJSONObject(i);
                String nombre   = o.getString("nombre");
                String apellido = o.getString("apellido");
                adapter.add(nombre + " " + apellido );
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        //ASOCIAMOS EL LIST VIEW AL ADAPTADOR
        listaUsuarios.setAdapter(adapter);
    }



    public void leerBD(View view){
        DataBaseHelper dbHelper = new DataBaseHelper(this, false);
        for(UsuarioPojo o : dbHelper.getAllUsuarios()){
            Log.d("LECTURA DE SQLite ... ", "NOMBRE : " + o.getNombre() + ",  APELLIDO : " + o.getApellido());
        }

        TextView text = (TextView) findViewById(R.id.cantidad_usuarios_text);
        text.setText(dbHelper.countUsuarios()+"");
    }


    public void sincronizar(View view){

        if(isConexionActiva()){

            DataBaseHelper dbHelper = new DataBaseHelper(this, false);
            dbHelper.deleteAllUsuarios();

            initListViewDesdeJson();

            try{
                for(int i = 0; i<jsonArray.length(); i++){
                    JSONObject o    = jsonArray.getJSONObject(i);
                    String nombre   = o.getString("nombre");
                    String apellido = o.getString("apellido");
                    insertDB(nombre, apellido);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }


            TextView text = (TextView) findViewById(R.id.cantidad_usuarios_text);
            text.setText(dbHelper.countUsuarios()+"");
        }else{
            Toast.makeText(this, "No hay conexion, no es posible sincronizar datos ...", Toast.LENGTH_SHORT).show();
        }


    }

    public void insertDB(String nombre, String apellido){
        DataBaseHelper dbHelper = new DataBaseHelper(this, false);
        Log.d("INSERT ... ", "insertando en base de datos el usuario " + nombre);
        dbHelper.addUsuario(new UsuarioPojo(null, nombre, apellido));
    }

    public void crearUsuario(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Datos nuevo usuario");

        //ASOCIAR UN LAYOUT REMOTO
        LayoutInflater inflater = this.getLayoutInflater();
        final View viewRemoto = inflater.inflate(R.layout.crear_usuario,null);
        builder.setView(viewRemoto);



        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText editNombre = (EditText) viewRemoto.findViewById(R.id.editText);
                EditText editApellido = (EditText) viewRemoto.findViewById(R.id.editText2);

                postRequestURL("http://server2.solcloud.cl/academia/json.php", editNombre.getText().toString(), editApellido.getText().toString());

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void postRequestURL(String url, String nombre, String apellido){
        Map<String, String> params = new HashMap<String, String>();
        params.put("accion", "crear");
        params.put("nombre", nombre);
        params.put("apellido", apellido);


        String parametrosURL = "?accion=crear&nombre="+nombre+"&apellido="+apellido;


        HttpURLConnection conn = null;
        try{
            conn = (HttpURLConnection) (new URL(url+parametrosURL)).openConnection();

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

            System.out.println("SALIDA .... " + sb.toString());

        }catch(IOException e){
            e.printStackTrace();
        }

    }


    /**
     * METODO PARA SABER SI LA CONEXION ESTA ACTIVA
     * @return
     */
    private boolean isConexionActiva() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
