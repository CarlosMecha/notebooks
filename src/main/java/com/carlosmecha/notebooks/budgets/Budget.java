package com.carlosmecha.notebooks.budgets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Budget model.
 *
 * Created by carlos on 22/01/17.
 */
public class Budget {

    private int id;
    private String notebookCode;
    private String name;
    private float value;
    private Date startOn;
    private Date endOn;
    private Date createdOn;
    private String createdBy;
    private String description;

    protected Budget() {
    }

    public static Budget fromRow(ResultSet row) throws SQLException {
        //id, notebook_code, name, value, start_on, end_on, created_on, created_by, description
        Budget budget = new Budget();
        budget.id = row.getInt("id");
        budget.notebookCode = row.getString("notebook_code");
        budget.name = row.getString("name");
        budget.value = row.getFloat("value");
        budget.startOn = new Date(row.getTimestamp("start_on").getTime());
        budget.endOn = new Date(row.getTimestamp("end_on").getTime());
        budget.createdOn = new Date(row.getTimestamp("created_on").getTime());
        budget.createdBy = row.getString("created_by");
        budget.description = row.getString("description");
        return budget;
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

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    protected void setValue(float value) {
        this.value = value;
    }

    public Date getStartOn() {
        return startOn;
    }

    protected void setStartOn(Date startOn) {
        this.startOn = startOn;
    }

    public Date getEndOn() {
        return endOn;
    }

    protected void setEndOn(Date endOn) {
        this.endOn = endOn;
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

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass().equals(Budget.class)) && ((Budget) obj).id == id; 
    }

    @Override
    public int hashCode() {
        return id;
    }
}
