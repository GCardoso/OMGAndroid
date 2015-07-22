package com.example.guilhermecardoso.omgandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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
import java.util.List;

import DBhelpers.SQLiteManager;
import entity.Image;

public class MainActivity extends Activity implements View.OnClickListener, SensorEventListener {
    private TextView mainTextViewAccelerometer;
    private TextView mainTextViewGPS;
    private Button mainButton;
    private Context context;
    public static ImageView imageView;
    private SensorManager mSensorManager;
    private ServiceGPSTracker serviceGPS;
    private TableHelper tableHelper;
    private Sensor mSensor;
    private SQLiteManager dbManager;
    private float x, y, z;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;

    TableLayout mainTable;
    ArrayList<Image> imagens;


    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private Camera mCamera = null;
    private boolean inPreview = false;
    private boolean cameraConfigured = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainButton = (Button) findViewById(R.id.main_button);
        mainTextViewAccelerometer = (TextView) findViewById(R.id.main_textview_accelerometer);
        mainTextViewGPS = (TextView) findViewById(R.id.main_textview_gps);
        mainButton.setOnClickListener(this);
        this.imageView = (ImageView) this.findViewById(R.id.imageViewPhotoTaken);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        serviceGPS = new ServiceGPSTracker(this);
        tableHelper = new TableHelper(this);
        mainTable = (TableLayout) findViewById(R.id.main_table);

        imagens = new ArrayList<Image>();
        tableHelper.createTable(mainTable);


        preview=(SurfaceView)findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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

        imagens.add(0, new Image());
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        //newImage();

        return image;
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

        File imgFile = new File(mCurrentPhotoPath);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        int nh = (int) (myBitmap.getHeight() * (512.0 / myBitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);
        imageView.setImageBitmap(scaled);

        if (serviceGPS.canGetLocation()) {
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

        tableHelper.addRow(mainTable,imagem);

        context = getApplicationContext();
        //newImage(
        // );

    }


    private void startPreview() {
        if (cameraConfigured && mCamera !=null) {

            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            inPreview=true;
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        mCamera.setDisplayOrientation(90);
        return(result);
    }

    private void initPreview(int width, int height) {
        if (mCamera !=null && previewHolder.getSurface()!=null) {
            try {
                mCamera.setPreviewDisplay(previewHolder);
            }
            catch (Throwable t) {
                Log.e("Preview-surfaceCallback",
                        "Exception in setPreviewDisplay()", t);
                Toast
                        .makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters= mCamera.getParameters();
                //Camera.Size size=getBestPreviewSize(width, height,
                  //      parameters);
/////////////////////
                Camera.Parameters params = mCamera.getParameters();

                // Supported picture formats (all devices should support JPEG).
                List<Integer> formats = params.getSupportedPictureFormats();

                if (formats.contains(ImageFormat.JPEG))
                {
                    params.setPictureFormat(ImageFormat.JPEG);
                    params.setJpegQuality(100);
                }
                else
                    params.setPictureFormat(PixelFormat.RGB_565);

                // Now the supported picture sizes.
                List<Camera.Size> sizes = params.getSupportedPictureSizes();
                Camera.Size size = sizes.get(sizes.size()-1);
                params.setPictureSize(size.width, size.height);

                // Set the brightness to auto.
                params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

                // Set the flash mode to auto.
                params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);

                // Set the scene mode to auto.
                params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);

                // Lastly set the focus to auto.
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

                mCamera.setParameters(params);
//////////////////
                if (size!=null) {
                    parameters.setPreviewSize(size.width, size.height);
                    mCamera.setParameters(parameters);
                    cameraConfigured=true;
                }
            }
        }
    }

    SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
            try {
                //mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                mCamera = Camera.open();
            }catch (RuntimeException e){
                e.printStackTrace();
            }
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void surfaceChanged(SurfaceHolder holder,
                                   int format, int width,
                                   int height) {
            initPreview(width, height);
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // Surface will be destroyed when we return, so stop the preview.
            // Because the CameraDevice object is not a shared resource, it's very
            // important to release it when the activity is paused.

        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
    }

    public void lookupImage(View view) {
        SQLiteManager sqLiteManager = new SQLiteManager(this, null, null, 1);

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
        if (inPreview) {
            mCamera.stopPreview();
        }

        mCamera.release();
        mCamera =null;
        inPreview=false;
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        mCamera = Camera.open();
        startPreview();
    }

    @Override
    protected void onDestroy() {
        if (inPreview) {
            mCamera.stopPreview();
            inPreview=false;
        }
        if (mCamera != null){
            mCamera.release();
            mCamera =null;
        }
        super.onDestroy();


    }

    @Override
    public void onClick(View v) {

    }
}
