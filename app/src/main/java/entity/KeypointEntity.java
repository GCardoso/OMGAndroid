package entity;

import org.opencv.features2d.KeyPoint;

/**
 * Created by Italo on 20/10/2015.
 */
public class KeypointEntity {

    private Integer id;
    private Integer id_image;
    private KeyPoint keypoint;

    public KeypointEntity(Integer id, Integer id_image, KeyPoint keypoint) {
        this.id = id;
        this.id_image = id_image;
        this.keypoint = keypoint;
    }

    public KeypointEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId_image() {
        return id_image;
    }

    public void setId_image(Integer id_image) {
        this.id_image = id_image;
    }

    public KeyPoint getKeypoint() {
        return keypoint;
    }

    public void setKeypoint(KeyPoint keypoint) {
        this.keypoint = keypoint;
    }
}

