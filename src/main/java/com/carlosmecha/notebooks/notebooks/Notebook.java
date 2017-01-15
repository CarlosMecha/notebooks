package com.carlosmecha.notebooks.notebooks;

import com.carlosmecha.notebooks.users.User;
import com.carlosmecha.notebooks.utils.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;

/**
 * Notebook model.
 *
 * Created by Carlos on 12/25/16.
 */
@Entity
@Table(name = "notebooks")
public class Notebook {

    @Id
    @NotEmpty
    private String code;
    @NotEmpty
    private String name;
    private Date createdOn;

    @ManyToOne
    @JoinColumn(nullable = false, updatable = false)
    private User createdBy;

    public Notebook() {
    }

    public Notebook(String name, User createdBy) {
        this(StringUtils.nameToCode(name), name, createdBy);
    }

    public Notebook(String code, String name, User createdBy) {
        this(code, name, new Date(), createdBy);
    }

    public Notebook(String code, String name, Date createdOn, User createdBy) {
        this();
        this.code = code;
        this.name = name;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return String.format("Notebook %s: %s", code, name);
    }

}
