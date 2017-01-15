package com.carlosmecha.notebooks.expenses;

import com.carlosmecha.notebooks.categories.Category;
import com.carlosmecha.notebooks.tags.Tag;

import java.util.*;

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

    public Report(String title, Iterable<Expense> expenses) {
        this.title = title;
        this.createdOn = new Date();
        build(expenses);
    }

    public Report(String title, Date startDate, Date endDate, Iterable<Expense> expenses) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdOn = new Date();
        build(expenses);
    }

    private void build(Iterable<Expense> expenses) {
        Set<Tag> tags = new HashSet<>();
        this.categories = new HashMap<>();
        this.total = 0;

        for(Expense expense : expenses) {
            if(startDate == null) {
                startDate = expense.getDate();
            } else if (startDate.after(expense.getDate())) {
                startDate = expense.getDate();
            }
            if(endDate == null) {
                endDate = expense.getDate();
            } else if (endDate.before(expense.getDate())) {
                endDate = expense.getDate();
            }

            float value = expense.getValue();
            Category category = expense.getCategory();

            categories.put(category, categories.containsKey(category) ? categories.get(category) + value : value);
            total += expense.getValue();

            for(Tag tag : expense.getTags()) {
                tags.add(tag);
            }
        }

        this.tags = new ArrayList<>(tags);
        this.tags.sort(Comparator.comparing(Tag::getCode));
    }

    public String getTitle() {
        return title;
    }

    public float getTotal() {
        return total;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Map<Category, Float> getCategories() {
        return categories;
    }

    public List<Tag> getTags() {
        return tags;
    }
}
