package com.example.guilhermecardoso.omgandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

import org.opencv.android.Utils;
import org.opencv.core.CvType;

import org.opencv.core.Mat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
    public static ImageView imageView2;
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
    private static Bitmap img;

    //Tutorial3 atributes clean after

    private OpenCVcameraView mOpenCvCameraView;
    private List<Size> mResolutionList;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;

    public Mat                    mGray;

    public Mat                    mRgba;

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

        //tableHelper.createTable(mainTable);
        /*preview = (SurfaceView) findViewById(R.id.preview);
        previewHolder = preview.getHolder();
*/

        setContentView(R.layout.cameraview);
        this.imageView = (ImageView) this.findViewById(R.id.imageViewPhotoTaken);
        this.imageView2 = (ImageView) this.findViewById(R.id.imageViewPhotoTaken2);
        mOpenCvCameraView = (OpenCVcameraView) findViewById(R.id.openCVCameraView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        //mOpenCvCameraView.setResolution();


        defaultPicturesSaveFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/PhotoGuide");
        if(!defaultPicturesSaveFolder.exists()) {
            defaultPicturesSaveFolder.mkdir();
        }

    }


    private synchronized void processORB(){
        Log.i(TAG,"Start of processOrb : " +  Long.toString(System.currentTimeMillis()));
        long time = System.nanoTime();
        //if (mGray.equals(mGray2)) return null;else {Log.i(TAG,"blablabla");return

        //img = FeatureDetectorAlgorithms.ORB(path1   , path2);
             Bitmap img = FeatureDetectorAlgorithms.ORB(mGray, mGray2,this);


        if (pathFlag && mGray.cols() > 0) {
            Bitmap image = Bitmap.createBitmap(mGray.cols(), mGray.rows(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(mGray, image);
            imageView2.setImageBitmap(image);
        }
        if (img==null) { Log.i(TAG,"Sem Matches para mostrar");} else imageView.setImageBitmap(img);
    contFrames =0;
        Log.i(TAG,"End of process Orb : " +  Long.toString(System.currentTimeMillis()));
        Log.i(TAG,"Time taken: " + Long.toString(System.nanoTime() - time));


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
        SQLiteManager sqLiteManager = new SQLiteManager(this);

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

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        super.onPause();

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

        File f = getDatabasePath("photoGuideDB.db");
        FileInputStream fis=null;
        FileOutputStream  fos=null;

        try
        {
            fis=new FileInputStream(f);
            fos=new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/db_dump.db"));
            while(true)
            {
                int i=fis.read();
                if(i!=-1)
                {fos.write(i);}
                else
                {break;}
            }
            fos.flush();
            Toast.makeText(this, "DB dump OK", Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, "DB dump ERROR", Toast.LENGTH_LONG).show();
        }
        finally
        {
            try
            {
                fos.close();
                fis.close();
            }
            catch(IOException ioe)
            {}
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int cont = 1;

        while (true){
            Log.e(TAG,"Loop do while " + cont);
            path1 = defaultPicturesSaveFolder + "/img" + Integer.toString(cont) + ".jpg";
            path2 = defaultPicturesSaveFolder + "/img" + Integer.toString(cont+1) + ".jpg";
            File file1 = new File(path1);
            File file2 = new File(path2);
            if (!file1.exists() || !file2.exists())break;
            processORB();
            FileOutputStream fos = null;
            if (img!=null) {
                try {
                    fos = new FileOutputStream(defaultPicturesSaveFolder + "/resultImg" + Integer.toString(cont) + "_" + Integer.toString(cont + 1) + ".png");

                    img.compress(Bitmap.CompressFormat.PNG, 100, fos);

                } catch (java.io.IOException e) {
                    Log.e("PictureDemo", "Exception in photoCallback", e);
                } finally {
                    try {
                        if (fos != null) {
                            fos.flush();
                            fos.close();
                        }
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            cont++;
        }

        return false;
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mGray2 = new Mat();

    }

    public void onCameraViewStopped() {
    }

    @Override
    public synchronized Mat onCameraFrame(CvCameraViewFrame inputFrame) {

    //    Log.i(TAG,"Start of onCameraFrame : " +  Long.toString(System.currentTimeMillis()));
    //    long time2 = System.nanoTime();
        mRgba = inputFrame.rgba();


        Log.i(TAG, "framCont : " + contFrames);


           if (contFrames == FrameSkip){
            //Log.i(TAG,"Start of Frame Capture : " +  Long.toString(System.currentTimeMillis()));
            //long time = System.nanoTime();

               Mat gray = inputFrame.gray();
               if (mGray.cols() == 0 && mGray.rows() == 0){
                   mGray = inputFrame.gray();
                 //  Log.i(TAG, "Frame = mgray1");
                   contFrames = 0;
               }
               else {

                   if (mGray2.cols() == 0 && mGray2.rows() == 0){
                       mGray2 = gray;
                       contFrames = 0;
                   //    Log.i(TAG, "frame = mgray2");
                   }else{
                   if (pathFlag)  {
                       mGray = gray;
                   //    Log.i(TAG, "Frame = mgray1");
                   }else {
                    //   Log.i(TAG, "Frame = mgray2");
                       mGray2 = gray;
                   }
                   }

                  // Log.i(TAG,"Mgray and MGray2 " + (mGray!=null) + (mGray2!=null));
                 //  Log.i(TAG,"Mgray and MGray2 " + mGray.rows() + " " + mGray.cols());
                 //  Log.i(TAG, "Mgray and MGray2 " + mGray2.rows() + " " + mGray2.cols());
                   pathFlag = !pathFlag;
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {

                           processORB();


                       }

                   });
               }
              // Log.i(TAG,"End of Frame Capture : " +  Long.toString(System.currentTimeMillis()));
              // Log.i(TAG,"Time taken: " + Long.toString(System.nanoTime() - time));
        }

        contFrames++;
      //  Log.i(TAG,"End of OnCameraFrame : " +  Long.toString(System.currentTimeMillis()));
      //  Log.i(TAG,"Time taken: " + Long.toString(System.nanoTime() - time2));
        return mRgba;
    }
}
