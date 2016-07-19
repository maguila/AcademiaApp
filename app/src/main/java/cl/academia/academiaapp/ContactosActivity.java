package cl.academia.academiaapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);
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


        ListView listaContactos = (ListView) findViewById(R.id.lista_contactos);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);




        //CHEQUEAR EL PERMISO A LOS CONTACTOS
        int permissionCheckReadSMS       = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int permissionCheckWriteContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS);
        int permissionCheckReadContacts  = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);


        if(permissionCheckReadContacts != PackageManager.PERMISSION_GRANTED){
            //LA PRIMERA VEZ LA APP PREGUNTA SI QUEREMOS DAR PERMISO A LA APLICACION
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }else{

            //REALIZAMOS LAS ACCIONES CON LOS PERMISOS APROBADOS POR EL USUARIO
            List<String> contacts = getContactNames();
            for(String contacto : contacts){
                adapter.add(contacto);
            }
        }

        //ASOCIAMOS EL ADAPTADOR AL LIST VIEW
        listaContactos.setAdapter(adapter);

        listaContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = parent.getItemAtPosition(position)+"";

                Intent intent = new Intent(view.getContext(), ContactoDetalleActivity.class);
                intent.putExtra("CONTACTO_ID", itemValue);
                startActivity(intent);
            }
        });



    }

    private List<String> getContactNames() {
        List<String> contacts = new ArrayList<>();
        // Obtenemos el ContentResolver
        ContentResolver cr = getContentResolver();

        // Obtenemos el cursos con todos los contactos
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // Movemos el cursor a la primera posicion, tambien nos aseguramos que el cursos no este vacio
        if (cursor.moveToFirst()) {
            //Iteramos en el cursor
            do {

                //Identificador interno del contacto
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                //Obtenemos el nombre del contacto
                String name  = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                //Validamos que el nombre no sea nulo (el adapter se cae al asociar un string nulo)
                if(!name.contains("@"))
                    contacts.add(name!=null ? name + "-"+contactId : "Sin Nombre!!!!!!");

            } while (cursor.moveToNext());
        }
        // Close the curosor
        cursor.close();
        return contacts;
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

}
