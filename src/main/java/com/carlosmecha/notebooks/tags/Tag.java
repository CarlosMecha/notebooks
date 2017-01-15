package com.carlosmecha.notebooks.tags;

import com.carlosmecha.notebooks.notebooks.Notebook;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;

/**
 * Tag model.
 *
 * Created by Carlos on 12/25/16.
 */
@Entity
@Table(name = "tags",
    uniqueConstraints = @UniqueConstraint(columnNames = {"code", "notebook_code"}))
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    private String code;

    @ManyToOne
    @JoinColumn(name = "notebook_code", nullable = false, updatable = false)
    private Notebook notebook;

    private Date createdOn;

    public Tag() {
    }

    public Tag(String code) {
        this(code, new Date());
    }

    public Tag(String code, Date createdOn) {
        this();
        this.code = code;
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

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public String toString() {
        return code;
    }

}
