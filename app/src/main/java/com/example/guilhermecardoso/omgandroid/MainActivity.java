package com.example.guilhermecardoso.omgandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import DBhelpers.SQLiteManager;
import OpenCV.OpenCVcameraView;
import Services.FeatureDetectorAlgorithms;
import Services.ServiceGPSTracker;
import Services.ServiceGyroscope;



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
    private static File defaultPicturesSaveFolder;
    private static int contFrames = 0;
    private static final int FrameSkip = 10;
    private static int contadorLinhas = 0;

    //Tutorial3 atributes clean after

    private OpenCVcameraView mOpenCvCameraView;
    private List<Size> mResolutionList;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;

    public Mat                    mGray;
    public Mat                    mGray2;

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
        Log.i(TAG, "Called onCreate");

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
*/

        setContentView(R.layout.cameraview);
        this.imageView = (ImageView) this.findViewById(R.id.imageViewPhotoTaken);
		mOpenCvCameraView = (OpenCVcameraView) findViewById(R.id.openCVCameraView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        //mOpenCvCameraView.setResolution();


        defaultPicturesSaveFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/PhotoGuide");
        if(!defaultPicturesSaveFolder.exists()) {
            defaultPicturesSaveFolder.mkdir();
        }

    }


    private void processORB(){

             Bitmap img = FeatureDetectorAlgorithms.ORB(mGray, mGray2);

        if (img==null) { Log.i(TAG,"Sem Matches para mostrar");} else imageView.setImageBitmap(img);


    }


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
            if (element.height == 640 && element.width == 480){int id = idx;
                Size resolution = mResolutionList.get(id);
                mOpenCvCameraView.setResolution(resolution);
                resolution = mOpenCvCameraView.getResolution();
                String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
                Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();}
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
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();


    }

    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
    }

    @Override
    protected void onDestroy() {
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

                defaultPicturesSaveFolder + "/sample_" + currentDateandTime + ".jpg";


        mOpenCvCameraView.takePicture(fileName);
        if(path1 != null && path2 !=null)processORB();
        if (pathFlag) path1 = fileName; else path2 = fileName;
        pathFlag = !pathFlag;
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();


		return false;
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mGray2 = new Mat();

    }

    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {


        if (mGray2 == null){
            mGray = inputFrame.gray();
            mGray2 = mGray;
        }
        else {
            mGray2 = mGray;
            mGray = inputFrame.gray();

            Mat m1 = mGray;
            Mat m2 = mGray2;

            mGray = m1;
            mGray2 = m2;
            if (contFrames++ == FrameSkip){
                contFrames = 0;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                processORB();
//stuff that updates ui

                }
            });
        }
        }
        return inputFrame.rgba();
    }




}
