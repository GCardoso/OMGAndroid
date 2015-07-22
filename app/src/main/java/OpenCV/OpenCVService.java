package OpenCV;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.opencv.android.*;

/**
 * Created by Italo on 21/07/2015.
 */
public class OpenCVService extends Service {

    final Context mContext;

    //TAG for debug purposes
    private static final String TAG = "OpenCVServiceLOG";

    private BaseLoaderCallback mlLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:{
                    Log.i(TAG,"OpenCV Callback Successful");
                }
                break;
                default:{super.onManagerConnected(status);}
                break;
            }

        }

    };


    public OpenCVService(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
