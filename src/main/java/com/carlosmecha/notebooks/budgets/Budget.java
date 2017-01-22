package com.carlosmecha.notebooks.budgets;

import com.carlosmecha.notebooks.expenses.Expense;
import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.users.User;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Budget model.
 *
 * Created by carlos on 22/01/17.
 */
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "notebook_code", nullable = false, updatable = false)
    private Notebook notebook;

    private float value;

    @Column(name = "start_on")
    private Date startOn;

    @Column(name = "end_on")
    private Date endOn;

    @Column(name = "created_on")
    private Date createdOn;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "budget_expenses", joinColumns = {
            @JoinColumn(name = "budget_id", nullable = false, updatable = false)
    }, inverseJoinColumns = {
            @JoinColumn(name = "expense_id", nullable = false, updatable = false)
    })
    private Set<Expense> expenses;

    private String description;

    public Budget() {
    }

    public Budget(Notebook notebook, float value, Date startOn, Date endOn, String description, User createdBy) {
        this();
        this.notebook = notebook;
        this.value = value;
        this.startOn = startOn;
        this.endOn = endOn;
        this.createdOn = new Date();
        this.createdBy = createdBy;
        this.description = description;
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

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Date getStartOn() {
        return startOn;
    }

    public void setStartOn(Date startOn) {
        this.startOn = startOn;
    }

    public Date getEndOn() {
        return endOn;
    }

    public void setEndOn(Date endOn) {
        this.endOn = endOn;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Set<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(Set<Expense> expenses) {
        this.expenses = expenses;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
