package entity;

/**
 * Created by Italo on 13/07/2015.
 */
public class Image {

    private long id;
    private String name;
    private double latitude;
    private double longitude;
    private float[] accelerometerVal;

    public Image(long id, String name, double latitude, double longitude, float[] accelerometerVal) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accelerometerVal = accelerometerVal;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float[] getAccelerometerVal() {
        return accelerometerVal;
    }

    public void setAccelerometerVal(float[] accelerometerVal) {
        this.accelerometerVal = accelerometerVal;
    }
}

