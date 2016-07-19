package cl.academia.academiaapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;

public class ContactoDetalleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto_detalle);
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

        TextView textView = (TextView) findViewById(R.id.nombre_contacto);
        ImageView imageView = (ImageView) findViewById(R.id.imagen_contacto);
        ListView listaNumeros = (ListView) findViewById(R.id.lista_numeros);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        Intent intent = getIntent();


        String nombre     = intent.getStringExtra("CONTACTO_ID").split("-")[0];
        String contactoId = intent.getStringExtra("CONTACTO_ID").split("-")[1];

        System.out.println(contactoId);

        textView.setText(nombre);

        Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactoId));
        InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), my_contact_Uri);
        Bitmap my_btmp = null;
        if (photo_stream != null) {
            BufferedInputStream buf = new BufferedInputStream(photo_stream);
            my_btmp = BitmapFactory.decodeStream(buf);
            System.out.println("TIENE IMAGEN!!!!!!! ");
            imageView.setImageBitmap(my_btmp);
        }


        ContentResolver cr = getContentResolver();
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactoId, null, null);
        while (phones.moveToNext()) {
            String numero    = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int tipoTelefono = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            String tipo = "";

            switch (tipoTelefono) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    tipo = "Sin tipo";
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    tipo = "Movil";
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    tipo = "Trabajo";
                    break;
            }


            adapter.add(numero + " - " + tipo);
        }


        listaNumeros.setAdapter(adapter);


    }

}
