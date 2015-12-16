package DBhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.opencv.features2d.DMatch;
import org.opencv.features2d.KeyPoint;

import java.io.File;
import java.security.Key;
import java.util.List;

import entity.DMatchEntity;
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
    public static final String RESPONSE = "response";
    public static final String KEYPOINT_CLASS_ID = "class_id";

    //DMatchEntity
    public static final String ID_DMATCH = "id";
    public static final String ID_QUERY = "id_query";
    public static final String ID_TRAIN = "id_train";
    public static final String IDX_QUERY = "idx_query";
    public static final String IDX_TRAIN = "idx_train";

    public static final String IDX_IMAGE = "idx_image";
    public static final String DISTANCE = "distance";




    //DB
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "photoGuideDB.db";
    private static final String TABLE_IMAGES = "images";
    private static final String TABLE_KEYPOINTS = "keypoints";
    private static final String TABLE_DMATCHES = "dmatches";


    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //db.execSQL("PRAGMA  main.synchronous=off;");

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
                        ID_IMAGE_KEYPOINT + " INTEGER," +
                        COORDX + " DOUBLE," +
                        COORDY + " DOUBLE," +
                        SIZE + " FLOAT," +
                        ANGLE + " FLOAT," +
                        RESPONSE + " FLOAT," +
                        OCTAVE + " INTEGER," +
                        KEYPOINT_CLASS_ID + " INTEGER," +
                        "FOREIGN KEY(" + ID_IMAGE_KEYPOINT + ") REFERENCES " + TABLE_IMAGES + "("+ID_IMAGE+")" + ")";
        String CREATE_DMATCHES_TABLE =
                "CREATE TABLE " +
                        TABLE_DMATCHES + "(" +
                        ID_DMATCH + " INTEGER PRIMARY KEY," +
                        ID_QUERY + " INTEGER," +
                        ID_TRAIN + " INTEGER," +
                        IDX_QUERY + " INTEGER," +
                        IDX_TRAIN + " INTEGER," +
                        IDX_IMAGE + " INTEGER," +
                        DISTANCE + " FLOAT,"  +
                        "FOREIGN KEY(" + ID_QUERY + ") REFERENCES " + TABLE_KEYPOINTS + "("+ID_KEYPOINT+")," +
                        "FOREIGN KEY(" + ID_TRAIN + ") REFERENCES " + TABLE_KEYPOINTS + "("+ID_KEYPOINT+")" + ")";
        db.execSQL(CREATE_IMAGE_TABLE);
        db.execSQL(CREATE_KEYPOINT_TABLE);
        db.execSQL(CREATE_DMATCHES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  TABLE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " +  TABLE_KEYPOINTS);
        db.execSQL("DROP TABLE IF EXISTS " +  TABLE_DMATCHES);
        onCreate(db);
    }
//ADD HANDLER METHODS
    public long addImage(Image image) {

        ContentValues values = new ContentValues();
        values.put(NOME, image.getName());
        values.put(LATITUDE, image.getLatitude());
        values.put(LONGITIDE, image.getLongitude());
        values.put(ACCELEROMETERX, image.getAccelerometerX());
        values.put(ACCELEROMETERY, image.getAccelerometerY());
        values.put(ACCELEROMETERZ, image.getAccelerometerZ());

        SQLiteDatabase db = this.getWritableDatabase();
        long retorno = -1;
        retorno = db.insert(TABLE_IMAGES, null, values);
        db.close();
        return retorno;
    }

    public long addKeypoint(Integer fid,KeyPoint kp) {

        ContentValues values = new ContentValues();
        values.put(ID_IMAGE_KEYPOINT, fid);
        values.put(COORDX, kp.pt.x);
        values.put(COORDY, kp.pt.y);
        values.put(SIZE, kp.size);
        values.put(ANGLE, kp.angle);
        values.put(RESPONSE, kp.response);
        values.put(OCTAVE, kp.octave);
        values.put(KEYPOINT_CLASS_ID, kp.class_id);

        SQLiteDatabase db = this.getWritableDatabase();
        long retorno = -1;
        retorno = db.insert(TABLE_KEYPOINTS, null, values);
        db.close();
        return retorno;
    }

    public Integer[] addKeypointMany(Integer fid,List<KeyPoint> kp) {
        Integer[] retorno = new Integer[kp.size()];
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        for (int i = 0; i < kp.size(); i++) {


            ContentValues values = new ContentValues();
            values.put(ID_IMAGE_KEYPOINT, fid);
            values.put(COORDX, kp.get(i).pt.x);
            values.put(COORDY, kp.get(i).pt.y);
            values.put(SIZE, kp.get(i).size);
            values.put(ANGLE, kp.get(i).angle);
            values.put(RESPONSE, kp.get(i).response);
            values.put(OCTAVE, kp.get(i).octave);
            values.put(KEYPOINT_CLASS_ID, kp.get(i).class_id);


            long temp;
            temp = db.insert(TABLE_KEYPOINTS, null, values);
            retorno[i] = (int)temp;
            if (temp == -1) break;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return retorno;
    }

    public Integer[] addDMatchMany( Integer[] queryid,Integer[] trainid,List<DMatch> dm) {

        Integer[] retorno = new Integer[dm.size()];
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        for (int i = 0; i < dm.size(); i++) {

            ContentValues values = new ContentValues();
            values.put(ID_TRAIN, trainid[i]);
            values.put(ID_QUERY, queryid[i]);
            values.put(IDX_TRAIN, dm.get(i).trainIdx);
            values.put(IDX_QUERY, dm.get(i).queryIdx);
            values.put(IDX_IMAGE, dm.get(i).imgIdx);
            values.put(DISTANCE, dm.get(i).distance);


            long temp = db.insert(TABLE_DMATCHES, null, values);
            retorno[i] = (int)temp;
            if (temp == -1) break;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return retorno;
    }
    public long addDMatch( Integer queryid,Integer trainid, DMatch dm) {

        ContentValues values = new ContentValues();
        values.put(ID_TRAIN, trainid);
        values.put(ID_QUERY, queryid);
        values.put(IDX_TRAIN, dm.trainIdx);
        values.put(IDX_QUERY, dm.queryIdx);
        values.put(IDX_IMAGE, dm.imgIdx);
        values.put(DISTANCE, dm.distance);

        SQLiteDatabase db = this.getWritableDatabase();
        long retorno = -1;
        retorno = db.insert(TABLE_DMATCHES, null, values);
        db.close();
        return retorno;
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

        String query = "Select * FROM " + TABLE_KEYPOINTS + " WHERE " + ID_KEYPOINT + " =  \"" + id + "\"";

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

        String query = "Select * FROM " + TABLE_KEYPOINTS + " WHERE " + ID_IMAGE_KEYPOINT + " =  \"" + id_image + "\"";

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

    public DMatchEntity findDMatchbyID(String id_image) {

        String query = "Select * FROM " + TABLE_DMATCHES + " WHERE " + ID_DMATCH + " =  \"" + id_image + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        DMatchEntity dme = new DMatchEntity();
        DMatch dm;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            dm = new DMatch(Integer.parseInt(cursor.getString(3)), // X
                    Integer.parseInt(cursor.getString(4)), // Y
                    Integer.parseInt(cursor.getString(5)), // ANGLE
                    Float.parseFloat(cursor.getString(6))); // RESPONSE

            dme.setId(Integer.parseInt(cursor.getString(0)));
            dme.setQuery_id(Integer.parseInt(cursor.getString(1)));
            dme.setTrain_id(Integer.parseInt(cursor.getString(2)));
                    cursor.close();
        } else {
            dme = null;
        }
        db.close();
        return dme;
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