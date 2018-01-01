package com.carlosmecha.notebooks.pages;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

import com.carlosmecha.notebooks.tags.Tag;

/**
 * Page model.
 *
 * Created by carlos on 4/01/17.
 */
public class Page {

    private int id;
    private String notebookCode;
    private Date date;
    private Date createdOn;
    private Date updatedOn;
    private String createdBy;
    private Set<Comment> comments;
    private Set<Tag> tags;

    protected Page() {
    }

    public static Page fromRow(ResultSet row) throws SQLException {
        // id, notebook_code, date, created_on, updated_on, created_by
        Page page = new Page();
        page.id = row.getInt("id");
        page.notebookCode = row.getString("notebook_code");
        page.date = new Date(row.getTimestamp("date").getTime());
        page.createdOn = new Date(row.getTimestamp("created_on").getTime());
        page.updatedOn = new Date(row.getTimestamp("updated_on").getTime());
        page.createdBy = row.getString("created_by");
        return page;
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public String getNotebookCode() {
        return notebookCode;
    }

    protected void setNotebookCode(String notebookCode) {
        this.notebookCode = notebookCode;
    }

    public Date getDate() {
        return date;
    }

    protected void setDate(Date date) {
        this.date = date;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    protected void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    protected void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    protected void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    protected void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    protected void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
