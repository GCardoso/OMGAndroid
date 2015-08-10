package com.example.guilhermecardoso.omgandroid;

import android.app.Activity;
import android.graphics.Bitmap;
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

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;

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
    private static String TAG = "Main Activity";
    TableLayout mainTable;
    ArrayList<Image> imagens;


    private static int contadorLinhas = 0;
>>>>>>> 357ed35127575d6a7cd3cf5b98ead13233c13b13

    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private Camera mCamera = null;
    private boolean inPreview = false;
    private boolean cameraConfigured = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            Log.i(TAG,"Didn't work");
        }

        this.imageView = (ImageView) this.findViewById(R.id.imageViewPhotoTaken);

        serviceGPS = new ServiceGPSTracker(this);
		serviceXYZ = new ServiceGyroscope(this.getApplicationContext());
        tableHelper = new TableHelper(this);
		mainTable = (TableLayout) findViewById(R.id.main_table);
        dbManager = new SQLiteManager(this,null,null,1);
		//tableHelper.createTable(mainTable);
        preview=(SurfaceView)findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    




        processORB();
    }



    private void processORB(){
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);;
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);


        ///storage/emutaled/0/DCIM/Camera/IMG_20150804_165353.jpg
        ///sdcard/nonfree/IMG_20150804_165353.jpg
        //first image
        Mat img1 = Highgui.imread("/sdcard/nonfree/img11.jpg");
        Mat descriptors1 = new Mat();
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();


        detector.detect(img1, keypoints1);
        descriptor.compute(img1, keypoints1, descriptors1);

        //second image
        Mat img2 = Highgui.imread("/sdcard/nonfree/img12.jpg");
        Mat descriptors2 = new Mat();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();

        detector.detect(img2, keypoints2);
        descriptor.compute(img2, keypoints2, descriptors2);

        //matcher should include 2 different image's descriptors
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(descriptors1,descriptors2,matches);
        //feature and connection colors
        Scalar RED = new Scalar(255,0,0);
        Scalar GREEN = new Scalar(0,255,0);
        //output image
        Mat outputImg = new Mat();
        MatOfByte drawnMatches = new MatOfByte();
        //this will draw all matches, works fine
        Features2d.drawMatches(img1, keypoints1, img2, keypoints2, matches,
                outputImg, GREEN, RED, drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);

        Bitmap imageMatched = Bitmap.createBitmap(outputImg.cols(), outputImg.rows(), Bitmap.Config.RGB_565);//need to save bitmap
        Utils.matToBitmap(outputImg, imageMatched);
        imageView.setImageBitmap(imageMatched);
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
