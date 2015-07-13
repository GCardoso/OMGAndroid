package DBhelpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

import javax.crypto.spec.DHGenParameterSpec;

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
    private static final String DATABASE_NAME = "photGuideoDB.db";
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

    
}