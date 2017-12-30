package com.carlosmecha.notebooks.expenses;

import com.carlosmecha.notebooks.budgets.Budget;
import com.carlosmecha.notebooks.categories.Category;
import com.carlosmecha.notebooks.tags.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

/**
 * Expense model.
 *
 * Created by Carlos on 12/25/16.
 */
public class Expense {

    private long id;
    private String notebookCode;
    private int categoryId;
    private float value;
    private Date date;
    private Date createdOn;
    private Date updatedOn;
    private String createdBy;
    private String notes;

    private Category category;
    private Set<Tag> tags;
    private Set<Budget> budgets;
    
    protected Expense() {
    }

    public static Expense fromRow(ResultSet row) throws SQLException {
        // id, notebook_code, category_id, value, date, created_on, updated_on, created_by, notes
        Expense expense = new Expense();
        expense.id = row.getInt("id");
        expense.notebookCode = row.getString("notebook_code");
        expense.categoryId = row.getInt("category_id");
        expense.value = row.getFloat("value");
        expense.date = new Date(row.getTimestamp("date").getTime());
        expense.createdOn = new Date(row.getTimestamp("created_on").getTime());
        expense.updatedOn = new Date(row.getTimestamp("updated_on").getTime());
        expense.createdBy = row.getString("created_by");
        expense.notes = row.getString("notes");
        return expense;
    }

    public long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    public String getNotebookCode() {
        return notebookCode;
    }

    protected void setNotebookCode(String notebookCode) {
        this.notebookCode = notebookCode;
    }

    public int getCategoryId() {
        return categoryId;
    }

    protected void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public float getValue() {
        return value;
    }

    protected void setValue(float value) {
        this.value = value;
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

    public String getNotes() {
        return notes;
    }

    protected void setNotes(String notes) {
        this.notes = notes;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<Budget> getBudgets() {
        return budgets;
    }

    public void setBudgets(Set<Budget> budgets) {
        this.budgets = budgets;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Expense ");
        builder.append(id);
        builder.append("\n");
        builder.append(" - Category: ");
        builder.append(categoryId);
        builder.append("\n");
        builder.append(" - $");
        builder.append(value);
        builder.append("\n");
        builder.append(" - ");
        builder.append(date);
        builder.append("\n");
        for(Tag tag : tags) {
            builder.append(" - ");
            builder.append(tag);
            builder.append("\n");
        }
        return builder.toString();
    }
}
