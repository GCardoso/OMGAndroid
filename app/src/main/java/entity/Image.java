package entity;

/**
 * Created by Italo on 13/07/2015.
 */
public class Image {

    private Long id;
    private String name;
    private double latitude;
    private double longitude;
    private float accelerometerX;
    private float accelerometerY;
    private float accelerometerZ;

    public Image(String name){
        this.name = name;
    }

    public Image(long id, String name, double latitude, double longitude, float accelerometerX, float accelerometerY, float accelerometerZ) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accelerometerX = accelerometerX;
        this.accelerometerY = accelerometerY;
        this.accelerometerZ = accelerometerZ;
    }
// construtor sem ID para uso no banco
    public Image(String name, double latitude, double longitude, float accelerometerX, float accelerometerY, float accelerometerZ) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accelerometerX = accelerometerX;
        this.accelerometerY = accelerometerY;
        this.accelerometerZ = accelerometerZ;
    }

    public Image() {
    }

    public Long getId() {
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

    public float getAccelerometerZ() {
        return accelerometerZ;
    }

    public void setAccelerometerZ(float accelerometerZ) {
        this.accelerometerZ = accelerometerZ;
    }

    public float getAccelerometerY() {
        return accelerometerY;
    }

    public void setAccelerometerY(float accelerometerY) {
        this.accelerometerY = accelerometerY;
    }

    public float getAccelerometerX() {
        return accelerometerX;
    }

    public void setAccelerometerX(float accelerometerX) {
        this.accelerometerX = accelerometerX;
    }
}


