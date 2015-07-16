package com.example.guilhermecardoso.omgandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import DBhelpers.SQLiteManager;
import entity.Image;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, SensorEventListener {
    private TextView mainTextViewAccelerometer;
    private TextView mainTextViewGPS;
    private Button mainButton;
    private Context context;
    public static ImageView imageView;
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private SensorManager mSensorManager;
    private ServiceGPSTracker serviceGPS;
    private Sensor mSensor;
    private SQLiteManager dbManager;
    private float x,y,z;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;

    TableLayout mainTable;
    ArrayList<Image> imagens;
    static int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainButton                  = (Button) findViewById(R.id.main_button);
        mainTextViewAccelerometer   = (TextView) findViewById(R.id.main_textview_accelerometer);
        mainTextViewGPS             = (TextView) findViewById(R.id.main_textview_gps);
        mainButton.setOnClickListener(this);
        this.imageView              = (ImageView)this.findViewById(R.id.imageViewPhotoTaken);

        mSensorManager              =  (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor                     = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        serviceGPS                  = new ServiceGPSTracker(this);
        context = getApplicationContext();
        mainTable = (TableLayout) findViewById(R.id.main_table);

        imagens = new ArrayList<Image>();
        createTable();
    }

    private void createTable(){
        TableRow tableRowHeader = new TableRow(this);
        tableRowHeader.setId(10);
        tableRowHeader.setBackgroundColor(Color.GRAY);
        tableRowHeader.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView labelInfo = new TextView(this);
        labelInfo.setId(20);
        labelInfo.setText("Info Sobre a Imagem");
        labelInfo.setTextColor(Color.WHITE);
        labelInfo.setPadding(5, 5, 5, 5);
        tableRowHeader.addView(labelInfo);// add the column to the table row here

        mainTable.addView(tableRowHeader, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

    }

    private void addRow(Image img){
        TableRow newRow = new TableRow(this);
        if(count % 2 !=0){
            newRow.setBackgroundColor(Color.GRAY);
        }else{
            newRow.setBackgroundColor(Color.DKGRAY);
        }

        newRow.setId(100 + count);
        newRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        newRow.setGravity(Gravity.TOP);

        TextView labelNome = new TextView(this);
        labelNome.setId(200 + count);
        labelNome.setText(img.getName() + " ");
        labelNome.setPadding(2, 0, 5, 0);
        labelNome.setTextColor(Color.WHITE);
        newRow.addView(labelNome);

        View v = new View(this);
        v.setLayoutParams(new TableRow.LayoutParams(5, TableRow.LayoutParams.MATCH_PARENT));
        v.setBackgroundColor(Color.rgb(150, 50, 150));
        newRow.addView(v);


        TextView labelXYZ = new TextView(this);
        labelXYZ.setId(200 + count);
        labelXYZ.setText(img.getAccelerometerX() + " " + img.getAccelerometerY() + " " + img.getAccelerometerZ() + " ");
        labelXYZ.setTextColor(Color.WHITE);
        newRow.addView(labelXYZ);


        View v2 = new View(this);
        v2.setLayoutParams(new TableRow.LayoutParams(5, TableRow.LayoutParams.MATCH_PARENT));
        v2.setBackgroundColor(Color.rgb(150, 50, 150));
        newRow.addView(v2);

        TextView labelGPS = new TextView(this);
        labelGPS.setId(300 + count);
        labelGPS.setText(img.getLatitude() + " " + img.getLongitude());
        labelGPS.setTextColor(Color.WHITE);


        newRow.setGravity(Gravity.LEFT);
        newRow.addView(labelGPS);

// finally add this to the table row
        mainTable.addView(newRow, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        count++;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* directory */
        );

        imagens.add(0,new Image(imageFileName));
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();
        //newImage();

        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                galleryAddPic();
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        CharSequence text = "File was saved in " + mCurrentPhotoPath;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        File imgFile = new  File(mCurrentPhotoPath);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);
        imageView.setImageBitmap(scaled);

        if (serviceGPS.canGetLocation()){
            mainTextViewGPS.setText("Latitude: " + serviceGPS.getLatitude() + " Longitude: " + serviceGPS.getLongitude());
        }
        mainTextViewAccelerometer.setText("x = " + x + " y = " + y + " z = " + z);

        Image imagem = imagens.get(0);
        imagem.setAccelerometerX(x);
        imagem.setAccelerometerY(y);
        imagem.setAccelerometerZ(z);
        imagem.setLatitude(serviceGPS.getLatitude());
        imagem.setLongitude(serviceGPS.getLongitude());
        imagens.set(0, imagem);

        addRow(imagem);

        context = getApplicationContext();
        //newImage();
        Log.i("TEST Storage", context.getFilesDir().toString());
    }

    @Override
    public void onClick(View v) {
        dispatchTakePictureIntent();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
    }

    public void newImage() {
        SQLiteManager sqLiteManager = new SQLiteManager(this, null, null, 1);

        String nome = mCurrentPhotoPath;

        double lat = serviceGPS.getLatitude();
        double longt = serviceGPS.getLongitude();
        float accX = x;
        float accY = y;
        float accZ = z;

        Image image =
                new Image(nome,lat,longt,accX,accY,accY);

        sqLiteManager.addImage(image);

    }

    public void lookupImage (View view) {
        SQLiteManager sqLiteManager= new SQLiteManager(this, null, null, 1);

        //modificar essa parte com as views que v�o fornecer os parametros para busca
        //Image image =
        //        sqLiteManager.findImagebyName(productBox.getText().toString());
        //
        //if (product != null) {
        //    idView.setText(String.valueOf(product.getID()));

        //    quantityBox.setText(String.valueOf(product.getQuantity()));
        //} else {
        //    idView.setText("No Match Found");
        //}
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }
}
