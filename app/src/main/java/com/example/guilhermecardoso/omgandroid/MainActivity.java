package com.example.guilhermecardoso.omgandroid;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View.OnTouchListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;

import java.io.IOException;

import DBhelpers.SQLiteManager;
import OpenCV.OpenCVcameraView;
import Services.FeatureDetectorAlgorithms;
import Services.ServiceGPSTracker;
import Services.ServiceGyroscope;
import entity.Image;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;



public class MainActivity extends Activity implements CvCameraViewListener2, OnTouchListener {
    public static ImageView imageView;
    private ServiceGPSTracker serviceGPS;
    private ServiceGyroscope serviceXYZ;
    private SQLiteManager dbManager;
	private TableHelper tableHelper;
    private static String TAG = "Main Activity";
	TableLayout mainTable;
    private static boolean pathFlag = true;
    private static String path1,path2;


    private static int contadorLinhas = 0;


    /*private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private Camera mCamera = null;
    private boolean inPreview = false;
    private boolean cameraConfigured = false;*/

    //Tutorial3 atributes clean after

    private OpenCVcameraView mOpenCvCameraView;
    private List<Size> mResolutionList;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;

    //Tutorial3 OpenCV caller, Switch to Service Caller

     /*if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            Log.i(TAG, "Didn't work");
        }*/

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"Called onCreate");

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            Log.i(TAG, "Didn't work");
        }


        serviceGPS = new ServiceGPSTracker(this);
        serviceXYZ = new ServiceGyroscope(this.getApplicationContext());
        tableHelper = new TableHelper(this);
        //mainTable = (TableLayout) findViewById(R.id.main_table);
        dbManager = new SQLiteManager(this, null, null, 1);
        //tableHelper.createTable(mainTable);
        /*preview = (SurfaceView) findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
*/

        setContentView(R.layout.cameraview);
        this.imageView = (ImageView) this.findViewById(R.id.imageViewPhotoTaken);
        mOpenCvCameraView = (OpenCVcameraView) findViewById(R.id.openCVCameraView);


        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);

        //mOpenCvCameraView.setResolution();



        //processORB();

    }


    private void processORB(){
        imageView.setImageBitmap(FeatureDetectorAlgorithms.ORB(path1,path2));

    }

/*

    private void startPreview() {
        if (cameraConfigured && mCamera != null) {

            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            inPreview = true;
        }
    }
*/

/*
    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
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
        return (result);
    }
*/

  /*  private void initPreview(int width, int height) {
        if (mCamera != null && previewHolder.getSurface() != null) {
            try {
                mCamera.setPreviewDisplay(previewHolder);
            } catch (Throwable t) {
                Log.e("Preview-surfaceCallback",
                        "Exception in setPreviewDisplay()", t);
                Toast
                        .makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size size = getBestPreviewSize(width, height,
                        parameters);

                if (size != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    mCamera.setParameters(parameters);
                    cameraConfigured = true;
                }
            }
        }
    }*/
/*
    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
            try {
                //mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                mCamera = Camera.open();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*public void surfaceChanged(SurfaceHolder holder,
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
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        List<String> effects = mOpenCvCameraView.getEffectList();

        if (effects == null) {
            Log.e(TAG, "Color effects are not supported by device!");
            return true;
        }

        mColorEffectsMenu = menu.addSubMenu("Color Effect");
        mEffectMenuItems = new MenuItem[effects.size()];

        int idx = 0;
        ListIterator<String> effectItr = effects.listIterator();
        while(effectItr.hasNext()) {
            String element = effectItr.next();
            mEffectMenuItems[idx] = mColorEffectsMenu.add(1, idx, Menu.NONE, element);
            idx++;
        }

        mResolutionMenu = menu.addSubMenu("Resolution");
        mResolutionList = mOpenCvCameraView.getResolutionList();
        mResolutionMenuItems = new MenuItem[mResolutionList.size()];

        ListIterator<Size> resolutionItr = mResolutionList.listIterator();
        idx = 0;
        while(resolutionItr.hasNext()) {
            Size element = resolutionItr.next();
            mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE,
                    Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
            idx++;
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item.getGroupId() == 1) {
            mOpenCvCameraView.setEffect((String) item.getTitle());
            Toast.makeText(this, mOpenCvCameraView.getEffect(), Toast.LENGTH_SHORT).show();
        } else if (item.getGroupId() == 2) {
            int id = item.getItemId();
            Size resolution = mResolutionList.get(id);
            mOpenCvCameraView.setResolution(resolution);
            resolution = mOpenCvCameraView.getResolution();
            String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
            Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
        }

        return true;
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

    protected void onPause() {
 /*       if (inPreview) {
            mCamera.stopPreview();
        }

        mCamera.release();
        mCamera = null;
        inPreview = false;
        super.onPause();*/

        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();


    }

    protected void onResume() {
        /*super.onResume();
        mCamera = Camera.open();
        startPreview();*/


        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
    }

    @Override
    protected void onDestroy() {
/*        if (inPreview) {
            mCamera.stopPreview();
            inPreview = false;
        }
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        super.onDestroy();*/

        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG,"onTouch event");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());

        String fileName =
                //"/storage/emulated/0/Download/sample_" + currentDateandTime + ".jpg";
                "/storage/emulated/0/Download/sample_" + contadorLinhas++ + ".jpg";

                mOpenCvCameraView.takePicture(fileName);

        if (pathFlag) path1 = fileName; else path2 = fileName;
        pathFlag = !pathFlag;
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        //if(path1 != null && path2 !=null)processORB();
        return false;
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }




}
