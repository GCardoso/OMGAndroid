package DBhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.opencv.features2d.KeyPoint;

import entity.Image;
import entity.KeypointEntity;

/**
 * Created by Italo on 13/07/2015.
 */
public class SQLiteManager extends SQLiteOpenHelper {

    //Image
    public static final String ID_IMAGE = "id";
    public static final String NOME = "nome";
    public static final String LATITUDE = "latitude";
    public static final String LONGITIDE = "longitude";
    public static final String ACCELEROMETERX = "accelerometerx";
    public static final String ACCELEROMETERY = "accelerometery";
    public static final String ACCELEROMETERZ = "accelerometerz";

    //KeypointEntity
    public static final String ID_KEYPOINT = "id";
    public static final String ID_IMAGE_KEYPOINT = "id_imagem";
    public static final String COORDX = "X";
    public static final String COORDY = "Y";
    public static final String ANGLE = "angle";
    public static final String OCTAVE = "octave";
    public static final String SIZE = "size";
    public static final String RESPONSE = "size";
    public static final String KEYPOINT_CLASS_ID = "class_id";



    //DB
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "photoGuideDB.db";
    private static final String TABLE_IMAGES = "images";
    private static final String TABLE_KEYPOINTS = "keypoints";


    public SQLiteManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_IMAGE_TABLE =
                "CREATE TABLE " +
                        TABLE_IMAGES + "(" +
                        ID_IMAGE + " INTEGER PRIMARY KEY," +
                        NOME + " VARCHAR," +
                        LATITUDE + " DOUBLE," +
                        LONGITIDE + " DOUBLE," +
                        ACCELEROMETERX + " FLOAT," +
                        ACCELEROMETERY + " FLOAT," +
                        ACCELEROMETERZ + " FLOAT" + ")";

        String CREATE_KEYPOINT_TABLE =
                "CREATE TABLE " +
                        TABLE_KEYPOINTS + "(" +
                        ID_KEYPOINT + " INTEGER PRIMARY KEY," +
                        ID_IMAGE_KEYPOINT + " INTEGER FOREIGN KEY," +
                        COORDX + " DOUBLE," +
                        COORDY + " DOUBLE," +
                        SIZE + " FLOAT," +
                        ANGLE + " FLOAT," +
                        RESPONSE + " FLOAT," +
                        OCTAVE + " INTEGER," +
                        KEYPOINT_CLASS_ID + " INTEGER" + ")";
        db.execSQL(CREATE_IMAGE_TABLE);
        db.execSQL(CREATE_KEYPOINT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  TABLE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " +  TABLE_KEYPOINTS);
        onCreate(db);
    }

//ADD HANDLER METHODS
    public void addImage(Image image) {

        ContentValues values = new ContentValues();
        values.put(NOME, image.getName());
        values.put(LATITUDE, image.getLatitude());
        values.put(LONGITIDE, image.getLongitude());
        values.put(ACCELEROMETERX, image.getAccelerometerX());
        values.put(ACCELEROMETERY, image.getAccelerometerY());
        values.put(ACCELEROMETERZ, image.getAccelerometerZ());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_IMAGES, null, values);
        db.close();
    }

//QUERY HANDLER METHODS
    //Achar imagem por nome, duplicar e modificar paramentro se quiser outras condicoes
    public Image findImagebyName(String imagename) {

        String query = "Select * FROM " + TABLE_IMAGES + " WHERE " + NOME+ " =  \"" + imagename + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Image image = new Image();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            image.setId(Integer.parseInt(cursor.getString(0)));
            image.setName(cursor.getString(1));
            image.setLatitude(Double.parseDouble(cursor.getString(2)));
            image.setLongitude(Double.parseDouble(cursor.getString(3)));
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

    public Image findImagebyID(String id) {

        String query = "Select * FROM " + TABLE_IMAGES + " WHERE " + ID_IMAGE + " =  \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Image image = new Image();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            image.setId(Integer.parseInt(cursor.getString(0)));
            image.setName(cursor.getString(1));
            image.setLatitude(Double.parseDouble(cursor.getString(2)));
            image.setLongitude(Double.parseDouble(cursor.getString(3)));
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
    public KeypointEntity findKeypointbyID(String id) {

        String query = "Select * FROM " + ID_KEYPOINT + " WHERE " + ID_KEYPOINT + " =  \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        KeypointEntity kpe = new KeypointEntity();
        KeyPoint kp;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            kp = new KeyPoint(     Float.parseFloat(cursor.getString(2)), // X
                                        Float.parseFloat(cursor.getString(3)), // Y
                                                Float.parseFloat(cursor.getString(4)), //SIZE
                                                        Float.parseFloat(cursor.getString(5)), // ANGLE
                                                                Float.parseFloat(cursor.getString(6)), // RESPONSE
                                                                        Integer.parseInt(cursor.getString(7)), // OCTAVE
                                                                                Integer.parseInt(cursor.getString(8))); // CLASS_ID

            kpe.setId(Integer.parseInt(cursor.getString(0)));
            kpe.setId_image(Integer.parseInt(cursor.getString(1)));
            kpe.setKeypoint(kp);
            cursor.close();
        } else {
            kpe = null;
        }
        db.close();
        return kpe;
    }

    public KeypointEntity findKeypointbyImage(String id_image) {

        String query = "Select * FROM " + ID_IMAGE_KEYPOINT + " WHERE " + ID_IMAGE_KEYPOINT + " =  \"" + id_image + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        KeypointEntity kpe = new KeypointEntity();
        KeyPoint kp;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            kp = new KeyPoint(     Float.parseFloat(cursor.getString(2)), // X
                                        Float.parseFloat(cursor.getString(3)), // Y
                                                Float.parseFloat(cursor.getString(4)), //SIZE
                                                        Float.parseFloat(cursor.getString(5)), // ANGLE
                                                                Float.parseFloat(cursor.getString(6)), // RESPONSE
                                                                        Integer.parseInt(cursor.getString(7)), // OCTAVE
                                                                                Integer.parseInt(cursor.getString(8))); // CLASS_ID

            kpe.setId(Integer.parseInt(cursor.getString(0)));
            kpe.setId_image(Integer.parseInt(cursor.getString(1)));
            kpe.setKeypoint(kp);
            cursor.close();
        } else {
            kpe = null;
        }
        db.close();
        return kpe;
    }

 //DELETE HANDLER METHODS

    public boolean deleteImagebyName(String imagename) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_IMAGES + " WHERE " + NOME + " =  \"" + imagename+ "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Image image = new Image();

        if (cursor.moveToFirst()) {
            image.setId(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_IMAGES, ID_IMAGE + " = ?",
                    new String[] { String.valueOf(image.getId()) });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

}