package DBhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import entity.Image;

/**
 * Created by Italo on 13/07/2015.
 */
public class SQLiteManager extends SQLiteOpenHelper {

    //Image
    public static final String _ID = "_id";
    public static final String _NOME = "nome";
    public static final String _LATITUDE = "latitude";
    public static final String _LONGITIDE = "longitude";
    public static final String _ACCELEROMETERX = "accelerometerx";
    public static final String _ACCELEROMETERY = "accelerometery";
    public static final String _ACCELEROMETERZ = "accelerometerz";

    //DB
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "photoGuideDB.db";
    private static final String TABLE_IMAGES = "images";


    public SQLiteManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IMAGE_TABLE =
                "CREATE_TABLE" +
                        TABLE_IMAGES + "(" +
                        _ID + "INTEGER PRIMARY KEY," +
                        _NOME + " TEXT," +
                        _LATITUDE + " FLOAT," +
                        _LONGITIDE + " FLOAT," +
                        _ACCELEROMETERX + " FLOAT," +
                        _ACCELEROMETERY + " FLOAT," +
                        _ACCELEROMETERZ + " FLOAT," + ")";
        db.execSQL(CREATE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  TABLE_IMAGES);
        onCreate(db);
    }

//ADD HANDLER METHODS
    public void addImage(Image image) {

        ContentValues values = new ContentValues();
        values.put(_NOME, image.getName());
        values.put(_LATITUDE, image.getLatitude());
        values.put(_LONGITIDE, image.getLongitude());
        values.put(_ACCELEROMETERX, image.getAccelerometerX());
        values.put(_ACCELEROMETERY, image.getAccelerometerY());
        values.put(_ACCELEROMETERZ, image.getAccelerometerZ());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_IMAGES, null, values);
        db.close();
    }

//QUERY HANDLER METHODS
    //Achar imagem por nome, duplicar e modificar paramentro se quiser outras condicoes
    public Image findImagebyName(String imagename) {

        String query = "Select * FROM " + TABLE_IMAGES + " WHERE " + _NOME+ " =  \"" + imagename + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Image image = new Image();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            image.setId(Long.parseLong(cursor.getString(0)));
            image.setName(cursor.getString(1));
            image.setLatitude(Float.parseFloat(cursor.getString(2)));
            image.setLongitude(Float.parseFloat(cursor.getString(3)));
            image.setAccelerometerX(Float.parseFloat(cursor.getString(4)));
            image.setAccelerometerY(Float.parseFloat(cursor.getString(5)));
            image.setAccelerometerZ(Float.parseFloat(cursor.getString(6)));
            cursor.close();
        } else {
            image = null;
        }
        db.close();
        return image;
    }

 //DELETE HANDLER METHODS

    public boolean deleteImagebyName(String imagename) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_IMAGES + " WHERE " + _NOME + " =  \"" + imagename+ "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Image image = new Image();

        if (cursor.moveToFirst()) {
            image.setId(Long.parseLong(cursor.getString(0)));
            db.delete(TABLE_IMAGES, _ID + " = ?",
                    new String[] { String.valueOf(image.getId()) });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

}