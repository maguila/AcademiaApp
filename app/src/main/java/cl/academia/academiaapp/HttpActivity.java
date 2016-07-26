package cl.academia.academiaapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import cl.academia.academiaapp.sqlLite.DataBaseHelper;
import cl.academia.academiaapp.sqlLite.UsuarioPojo;
import cl.academia.academiaapp.util.AndroidUtils;

public class HttpActivity extends AppCompatActivity {

    private JSONArray jsonArray;
    private Context contextActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);

        this.contextActivity = this;

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
        if(AndroidUtils.isConexionActiva(this)){
            initListViewDesdeJson();
        }else{
            Toast.makeText(this, "No hay conexion, se debe traer datos de la base de datos ...", Toast.LENGTH_SHORT).show();

            ListView listaUsuarios = (ListView) findViewById(R.id.lista_usuarios);
            ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

            DataBaseHelper dbHelper = new DataBaseHelper(this, false);
            for(UsuarioPojo o : dbHelper.getAllUsuarios()){
                Log.d("LECTURA DE SQLite .... ", "NOMBRE  : " + o.getNombre() + ",  APELLIDO : " + o.getApellido());
                adapter.add(o.getNombre() + " " + o.getApellido() );
            }

            TextView text = (TextView) findViewById(R.id.cantidad_usuarios_text);
            text.setText(dbHelper.countUsuarios()+"");
            listaUsuarios.setAdapter(adapter);

        }


    }

    /***************************************
     * Carga datos al listView leyendo un json
     ***************************************/
    public void initListViewDesdeJson(){

        //INSTANCIAMOS NUESTRO LIST VIEW
        ListView listaUsuarios = (ListView) findViewById(R.id.lista_usuarios);
        ArrayAdapter<String> adapter = new ArrayAdapter(contextActivity, android.R.layout.simple_list_item_1);


        try{
            String jsonString = AndroidUtils.getDataStringByURL("http://server2.solcloud.cl/academia/retorno.json");
            JSONObject jsonObj = new JSONObject(jsonString); //AHORA INSTANCIAMOS LA CLASE JSON OBJECT
            jsonArray = jsonObj.getJSONArray("usuarios"); // OBTENER EL ARREGLO JSON

            //RECORRERMOS EL JSON ARRAY
            for(int i = 0; i<jsonArray.length(); i++){
                JSONObject o    = jsonArray.getJSONObject(i);
                String nombre   = o.getString("nombre");
                String apellido = o.getString("apellido");
                adapter.add(nombre + " " + apellido );
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        //ASOCIAMOS EL LIST VIEW AL ADAPTADOR
        listaUsuarios.setAdapter(adapter);


    }


    /**
     * Accion para leer datos desde SQLite
     * @param view
     */
    public void leerBD(View view){
        DataBaseHelper dbHelper = new DataBaseHelper(this, false);
        for(UsuarioPojo o : dbHelper.getAllUsuarios()){
            Log.d("LECTURA DE SQLite ... ", "NOMBRE : " + o.getNombre() + ",  APELLIDO : " + o.getApellido());
        }

        TextView text = (TextView) findViewById(R.id.cantidad_usuarios_text);
        text.setText(dbHelper.countUsuarios()+"");
    }


    /**
     * Accion para sincronizar datos
     * @param view
     */
    public void sincronizar(View view){

        if(AndroidUtils.isConexionActiva(this)){

            DataBaseHelper dbHelper = new DataBaseHelper(contextActivity, false);
            dbHelper.deleteAllUsuarios();

            initListViewDesdeJson();


            try{
                for(int i = 0; i<jsonArray.length(); i++){
                    JSONObject o    = jsonArray.getJSONObject(i);
                    String nombre   = o.getString("nombre");
                    String apellido = o.getString("apellido");
                    dbHelper.addUsuario(new UsuarioPojo(null, nombre, apellido));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }


            TextView text = (TextView) findViewById(R.id.cantidad_usuarios_text);
            text.setText(dbHelper.countUsuarios()+"");
        }else{
            Toast.makeText(contextActivity, "No hay conexion, no es posible sincronizar datos ...", Toast.LENGTH_SHORT).show();
        }




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


                String url = "http://server2.solcloud.cl/academia/json.php";
                String nombre   = editNombre.getText().toString();
                String apellido = editApellido.getText().toString();

                String parametrosURL = "?accion=crear&nombre="+nombre+"&apellido="+apellido;
                String retorno = AndroidUtils.getDataStringByURL(url+parametrosURL);

                System.out.println("RETORNO = " + retorno);

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



    /* ESTE ES EL PHP QUE ACTUALIZA Y RETORNA UN JSON


        <?php

          $accion   = $_GET['accion'];
          $nombre   = $_GET['nombre'];
          $apellido = $_GET['apellido'];

          ini_set('display_errors', 1);
          ini_set('display_startup_errors', 1);
          error_reporting(E_ALL);



          if($accion == "crear"){
            header('Content-Type: application/json');
            $data = file_get_contents("retorno.json");
            $tempArray = json_decode($data);
            $tempArray->usuarios[] = array("nombre"=> $nombre, "apellido"=> $apellido);
            $jsonData = json_encode($tempArray);
            file_put_contents("retorno.json", $jsonData);
            //print_r($tempArray);
            echo "guardado ok ...";
          }


        ?>




     */


}
