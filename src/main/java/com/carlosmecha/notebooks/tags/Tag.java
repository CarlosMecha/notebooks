package com.carlosmecha.notebooks.tags;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Tag model.
 *
 * Created by Carlos on 12/25/16.
 */
public class Tag {

    private int id;
    private String code;
    private String notebookCode;
    private Date createdOn;

    protected Tag() {}

    public static Tag fromRow(ResultSet row) throws SQLException {
        // id, code, notebook_code, created_on
        Tag tag = new Tag();
        tag.id = row.getInt("id");
        tag.code = row.getString("code");
        tag.notebookCode = row.getString("notebook_code");
        tag.createdOn = new Date(row.getTimestamp("created_on").getTime());
        return tag;
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

    public String getCode() {
        return code;
    }

    protected void setCode(String code) {
        this.code = code;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    protected void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass().equals(Tag.class)) && ((Tag) obj).id == id; 
    }

    @Override
    public int hashCode() {
        return id;
    }

}
