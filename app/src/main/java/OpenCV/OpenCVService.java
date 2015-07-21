package OpenCV;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

/**
 * Created by Italo on 20/07/2015.
 */
public class OpenCVService extends Service
    {

        public OpenCVService(BaseLoaderCallback mOpenCVCallBack) {
            this.mOpenCVCallBack = mOpenCVCallBack;
        }

        private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                    {
                      //  Log.i(TAG, "OpenCV loaded successfully");
                        // Create and set View
                      //  mView = new puzzle15View(mAppContext);
                    //    setContentView(mView);
                    } break;
                    default:
                    {
                        super.onManagerConnected(status);
                    } break;
                }
            }
        };

        /** Call on every application resume **/
//        @Override
//        protected void onResume()
        {
            //Log.i(TAG, "Called onResume");
            //only for activity
            //super.onResume();

            //Log.i(TAG, "Trying to load OpenCV library");
//            if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mOpenCVCallBack))
//            {
             //   Log.e(TAG, "Cannot connect to OpenCV Manager");
            }
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
