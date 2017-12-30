package com.carlosmecha.notebooks.notebooks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Notebook model.
 *
 * Created by Carlos on 12/25/16.
 */
public class Notebook {

    private String code;
    private String name;
    private Date createdOn;
    private String createdBy;

    protected Notebook() {
    }

    public static Notebook fromRow(ResultSet row) throws SQLException {
        // code, name, created_on, created_by
        Notebook notebook = new Notebook();
        notebook.code = row.getString("code");
        notebook.name = row.getString("name");
        notebook.createdOn = new Date(row.getTimestamp("created_on").getTime());
        notebook.createdBy = row.getString("created_by");
        return notebook;
    }

    public String getCode() {
        return code;
    }

    protected void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    protected void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    protected void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return String.format("Notebook %s: %s", code, name);
    }

}
