package io.github.yedaxia.musicnote.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/25.
 */

@Entity
public class Track {

    @Id(autoincrement=true)
    private Long id;

    private Long projectId;

    private String name;

    private String fileName;

    private Date createTime;

    @Generated(hash = 1024188734)
    public Track(Long id, Long projectId, String name, String fileName,
            Date createTime) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.fileName = fileName;
        this.createTime = createTime;
    }

    @Generated(hash = 1672506944)
    public Track() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
