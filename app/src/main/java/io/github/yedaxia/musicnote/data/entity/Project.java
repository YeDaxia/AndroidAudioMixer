package io.github.yedaxia.musicnote.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/25.
 */

@Entity
public class Project implements Serializable{

    private static final long serialVersionUID = 1060213783256782822L;

    @Id(autoincrement=true)
    private Long id;

    private String name;

    private String tune;

    private String beat;

    private Short speed;

    private Date createTime;

    @Transient
    private List<Track> trackList;

    @Generated(hash = 1427280558)
    public Project(Long id, String name, String tune, String beat, Short speed,
            Date createTime) {
        this.id = id;
        this.name = name;
        this.tune = tune;
        this.beat = beat;
        this.speed = speed;
        this.createTime = createTime;
    }

    @Generated(hash = 1767516619)
    public Project() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getBeat() {
        return beat;
    }

    public void setBeat(String beat) {
        this.beat = beat;
    }

    public Short getSpeed() {
        return speed;
    }

    public void setSpeed(Short speed) {
        this.speed = speed;
    }

    public String getTune() {
        return tune;
    }

    public void setTune(String tune) {
        this.tune = tune;
    }

    public List<Track> getTrackList() {
        return trackList;
    }

    public void setTrackList(List<Track> trackList) {
        this.trackList = trackList;
    }
}
