package com.carlosmecha.notebooks.expenses;

import com.carlosmecha.notebooks.categories.Category;
import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.tags.Tag;
import com.carlosmecha.notebooks.users.User;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Expense model.
 *
 * Created by Carlos on 12/25/16.
 */
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notebook_code", nullable = false, updatable = false)
    private Notebook notebook;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private float value;
    private Date date;

    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "updated_on")
    private Date updatedOn;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "expense_tags", joinColumns = {
            @JoinColumn(name = "expense_id", nullable = false, updatable = false)
    }, inverseJoinColumns = {
            @JoinColumn(name = "tag_id", nullable = false, updatable = false)
    })
    private Set<Tag> tags;

    private String notes;

    public Expense() {
    }

    public Expense(Notebook notebook, Category category, float value, Date date, Set<Tag> tags, String notes, User createdBy) {
        this();
        this.notebook = notebook;
        this.category = category;
        this.value = value;
        this.date = date;
        this.tags = tags;
        this.notes = notes;
        this.createdOn = new Date();
        this.updatedOn = createdOn;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Notebook getNotebook() {
        return notebook;
    }

    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Expense ");
        builder.append(id);
        builder.append("\n");
        builder.append(" - ");
        builder.append(category);
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
