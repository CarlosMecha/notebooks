package com.carlosmecha.notebooks.pages;

import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.tags.Tag;
import com.carlosmecha.notebooks.users.User;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Page model.
 *
 * Created by carlos on 4/01/17.
 */
@Entity
@Table(name = "pages")
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "notebook_code", nullable = false, updatable = false)
    private Notebook notebook;

    private Date date;

    private Date createdOn;
    private Date updatedOn;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "page_tags", joinColumns = {
            @JoinColumn(name = "page_id", nullable = false, updatable = false)
    }, inverseJoinColumns = {
            @JoinColumn(name = "tag_code", nullable = false, updatable = false)
    })
    private Set<Tag> tags;

    public Page() {
    }

    public Page(Notebook notebook, Date date, User createdBy, Set<Tag> tags) {
        this.notebook = notebook;
        this.date = date;
        this.createdBy = createdBy;
        this.tags = tags;
        Date now = new Date();
        this.createdOn = now;
        this.updatedOn = now;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Notebook getNotebook() {
        return notebook;
    }

    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
