package com.carlosmecha.notebooks.categories;

import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.utils.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;

/**
 * Category model.
 *
 * Created by Carlos on 12/25/16.
 */
@Entity
@Table(name = "categories",
    uniqueConstraints = @UniqueConstraint(columnNames = {"code", "notebook_code"}))
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    private String code;

    @ManyToOne
    @JoinColumn(name = "notebook_code", nullable = false, updatable = false)
    private Notebook notebook;

    @NotEmpty
    private String name;
    private Date createdOn;

    public Category() {
    }

    public Category(Notebook notebook, String name) {
        this(notebook, StringUtils.nameToCode(name), name);
    }

    public Category(Notebook notebook, String code, String name) {
        this(notebook, code, name, new Date());
    }

    public Category(Notebook notebook, String code, String name, Date createdOn) {
        this();
        this.notebook = notebook;
        this.code = code;
        this.name = name;
        this.createdOn = createdOn;
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


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public String toString() {
        return String.format("Category %s: %s", code, name);
    }

}
