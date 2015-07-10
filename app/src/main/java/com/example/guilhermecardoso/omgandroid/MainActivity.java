package com.example.guilhermecardoso.omgandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity implements View.OnClickListener, SensorEventListener {
    TextView mainTextViewAccelerometer;
    TextView mainTextViewGPS;
    Button mainButton;
    public static ImageView imageView;
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    SensorManager mSensorManager;
    Sensor mSensor;
    private float x,y,z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainButton = (Button) findViewById(R.id.main_button);
        mainTextViewAccelerometer = (TextView) findViewById(R.id.main_textview_accelerometer);
        mainTextViewGPS = (TextView) findViewById(R.id.main_textview_gps);
        mainButton.setOnClickListener(this);
        this.imageView = (ImageView)this.findViewById(R.id.imageViewPhotoTaken);

        mSensorManager =  (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    // Save a file: path for use with ACTION_VIEW intents

        String mCurrentPhotoPath;

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

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();
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


        Context context = getApplicationContext();
        CharSequence text = "File was saved in " + mCurrentPhotoPath;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        File imgFile = new  File(mCurrentPhotoPath);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);
        imageView.setImageBitmap(scaled);



        mainTextViewAccelerometer.setText("x = " + x + " y = " + y + " z = " + z);
    }

    @Override
    public void onClick(View v) {
        // Take what was typed into the EditText
        // and use in TextView
        dispatchTakePictureIntent();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        System.out.println("x = " + x + " y = " + y + " z = " + z);
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
