package com.example.guilhermecardoso.omgandroid;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import DBhelpers.SQLiteManager;
import Services.ServiceGPSTracker;
import Services.ServiceGyroscope;
import entity.Image;

public class MainActivity extends Activity {
    public static ImageView imageView;

    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private ServiceGPSTracker serviceGPS;
    private ServiceGyroscope  serviceXYZ;
	private TableHelper tableHelper;
	private SQLiteManager dbManager;
	static final int REQUEST_TAKE_PHOTO = 1;

    TableLayout mainTable;


    private static int contadorLinhas = 0;

    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private Camera mCamera = null;
    private boolean inPreview = false;
    private boolean cameraConfigured = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.imageView = (ImageView) this.findViewById(R.id.imageViewPhotoTaken);

        serviceGPS = new ServiceGPSTracker(this);
		serviceXYZ = new ServiceGyroscope(this.getApplicationContext());
        mainTable = (TableLayout) findViewById(R.id.main_table);

        dbManager = new SQLiteManager(this,null,null,1);
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
                Camera.Size size=getBestPreviewSize(width, height,
                        parameters);

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
