package com.carlosmecha.notebooks.categories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Category model.
 *
 * Created by Carlos on 12/25/16.
 */
public class Category {

    private int id;
    private String code;
    private String notebookCode;
    private String name;
    private Date createdOn;

    protected Category() {}

    public static Category fromRow(ResultSet row) throws SQLException {
        // id, code, notebook_code, name, created_on
        Category category = new Category();
        category.id = row.getInt("id");
        category.code = row.getString("code");
        category.notebookCode = row.getString("notebook_code");
        category.name = row.getString("name");
        category.createdOn = new Date(row.getTimestamp("created_on").getTime());
        return category;
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

    @Override
    public String toString() {
        return String.format("Category %s: %s", code, name);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass().equals(Category.class)) && ((Category) obj).id == id; 
    }

    @Override
    public int hashCode() {
        return id;
    }

}
