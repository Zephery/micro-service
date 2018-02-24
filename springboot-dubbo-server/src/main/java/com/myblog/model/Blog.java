package com.myblog.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Blog implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    @Column(name = "blogid")
    private Integer blogid;
    @Column(name = "title")
    private String title;
    @Column(name = "summary")
    private String summary;
    @Column(name = "content")
    private String content;
    @Column(name = "create_at")
    private String createAt;
    @Column(name = "hits")
    private Integer hits;
    @Column(name = "imageurl")
    private String imageurl;

    public Integer getBlogid() {
        return blogid;
    }

    public void setBlogid(Integer blogid) {
        this.blogid = blogid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

}