package Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by guilhermecardoso on 7/22/15.
 */
public class ServiceGyroscope extends Service implements SensorEventListener{
    private final String TAG = "Service Gyroscope";

    private double accX;
    private double accY;
    private double accZ;
    private Sensor mSensor;
    private SensorManager mSensorManager;

    private final Context context;

    public ServiceGyroscope(Context cont){
        this.context = cont;
        mSensorManager = (SensorManager) cont.getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        accX = event.values[0];
        accY = event.values[1];
        accZ = event.values[2];

        Log.i(TAG,accX + " " + accY + " " + accZ);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public double getAccX() {
        return accX;
    }

    public double getAccY() {
        return accY;
    }

    public double getAccZ() {
        return accZ;
    }
}
