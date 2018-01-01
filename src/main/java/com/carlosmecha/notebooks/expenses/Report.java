package com.carlosmecha.notebooks.expenses;

import com.carlosmecha.notebooks.categories.Category;
import com.carlosmecha.notebooks.tags.Tag;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Basic report.
 *
 * Created by carlos on 8/01/17.
 */
public class Report {

    private String title;
    private float total;
    private Date startDate;
    private Date endDate;
    private Date createdOn;
    private Map<Category, Float> categories;
    private List<Tag> tags;

    protected Report() {
        this.createdOn = new Date();
    }

    public String getTitle() {
        return title;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    public float getTotal() {
        return total;
    }

    protected void setTotal(float total) {
        this.total = total;
    }

    public Date getStartDate() {
        return startDate;
    }

    protected void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    protected void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Map<Category, Float> getCategories() {
        return categories;
    }

    protected void setCategories(Map<Category, Float> categories) {
        this.categories = categories;
    }

    public List<Tag> getTags() {
        return tags;
    }

    protected void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
