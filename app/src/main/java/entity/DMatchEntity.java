package entity;

import org.opencv.features2d.DMatch;

/**
 * Created by Italo on 11/11/2015.
 */
public class DMatchEntity {
    private Integer id;
    private Integer query_id;
    private Integer train_id;
    private DMatch dmatch;

    public DMatchEntity(Integer id, Integer query_id, Integer train_id, DMatch dmatch) {
        this.id = id;
        this.query_id = query_id;
        this.train_id = train_id;
        this.dmatch = dmatch;
    }

    public DMatchEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuery_id() {
        return query_id;
    }

    public void setQuery_id(Integer query_id) {
        this.query_id = query_id;
    }

    public Integer getTrain_id() {
        return train_id;
    }

    public void setTrain_id(Integer train_id) {
        this.train_id = train_id;
    }

    public DMatch getDmatch() {
        return dmatch;
    }

    public void setDmatch(DMatch dmatch) {
        this.dmatch = dmatch;
    }
}

