package cl.academia.academiaapp.sqlLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 21-07-16.
 */
public class DataBaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "pruebaDB";
    private static final int DATABASE_VERSION = 3;


    private static final String KEY_NOMBRE_TABLA = "TABLA_USUARIOS";
    private static final String KEY_ID           = "campo_id";
    private static final String KEY_NOMBRE       = "campo_nombre";
    private static final String KEY_APELLIDO     = "campo_apellido";
    private static final String KEY_USUARIO      = "campo_usuario";




    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sqlCreateTable = new StringBuilder();
        sqlCreateTable.append("CREATE TABLE ");
        sqlCreateTable.append(KEY_NOMBRE_TABLA);
        sqlCreateTable.append("(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , ");
        sqlCreateTable.append( KEY_NOMBRE + " TEXT , ");
        sqlCreateTable.append( KEY_APELLIDO + " TEXT , ");
        sqlCreateTable.append( KEY_USUARIO + " TEXT  )");

        db.execSQL(sqlCreateTable.toString());

        //ACA EN ADELANTE DEBERIA IR LA SINCRONIZACION DE DATOS CON UNA BASE DE DATOS REMOTA...
        //Y EN CASO DE HABER CONEXION DE INTERNET
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + KEY_NOMBRE_TABLA);
        onCreate(db);
    }



    public void addUsuario(UsuarioPojo usuarioPojo){
        SQLiteDatabase db = this.getWritableDatabase();

        if(db==null)
            onCreate(db);

        ContentValues values = new ContentValues();
        values.put(KEY_NOMBRE, usuarioPojo.getNombre());
        values.put(KEY_USUARIO, usuarioPojo.getUsuario());
        values.put(KEY_APELLIDO, usuarioPojo.getApellido());

        db.insert(KEY_NOMBRE_TABLA, null, values);
        db.close();
    }

    public UsuarioPojo getUsuario(String usuario){
        SQLiteDatabase db = this.getReadableDatabase();

        if(db==null)
            onCreate(db);

        Cursor cursor = db.query(KEY_NOMBRE_TABLA, new String[]{KEY_ID, KEY_USUARIO, KEY_NOMBRE, KEY_APELLIDO},
                                 KEY_NOMBRE + "=?",
                                 new String[]{String.valueOf(usuario)},
                                 null ,null ,null, null);

        if(cursor != null){
            cursor.moveToFirst();
            UsuarioPojo pojo = new UsuarioPojo();
            pojo.setNombre(cursor.getString(1));
            pojo.setApellido(cursor.getString(2));
            return pojo;
        }

        db.close();
        return null;
    }

    public List<UsuarioPojo> getAllUsuarios(){
        List<UsuarioPojo> usuarioList = new ArrayList<>();
        String sql = "SELECT "+KEY_USUARIO+"  , "+KEY_NOMBRE+" , "+KEY_APELLIDO+" FROM " + KEY_NOMBRE_TABLA;

        SQLiteDatabase db = this.getWritableDatabase();

        if(db==null){
            onCreate(db);
            db = this.getWritableDatabase();
        }


        Cursor cursor = db.rawQuery(sql, null);

        if(cursor!=null && cursor.moveToFirst()){
            do{
               usuarioList.add(new UsuarioPojo(cursor.getString(0), cursor.getString(1), cursor.getString(2) ));
            }while(cursor.moveToNext());

        }
        db.close();
        return usuarioList;
    }

    public int countUsuarios(){
        int retorno = 0;
        String sql = "SELECT * FROM " + KEY_NOMBRE_TABLA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        retorno = cursor.getCount();
        cursor.close();
        db.close();
        return retorno;
    }

    public void deleteAllUsuarios(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + KEY_NOMBRE_TABLA);
        db.close();
    }

    public Cursor getAllUsuariosCursor(){
        SQLiteDatabase db = this.getWritableDatabase();
        String [] columnas = {KEY_NOMBRE, KEY_APELLIDO};

        Cursor c = db.query(KEY_NOMBRE_TABLA, columnas, null, null, null, null, null, null);
        return c;
    }

}
