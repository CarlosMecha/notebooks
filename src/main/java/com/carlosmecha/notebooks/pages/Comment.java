package com.carlosmecha.notebooks.pages;

import com.carlosmecha.notebooks.users.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Comment model.
 *
 * Created by carlos on 4/01/17.
 */
@Entity
@Table(name = "page_comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "page_id", nullable = false, updatable = false)
    private Page page;

    private String content;
    private Date wroteOn;

    @ManyToOne
    @JoinColumn(name = "wrote_by", nullable = false, updatable = false)
    private User wroteBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_comment_id")
    private Comment previousComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_comment_id")
    private Comment nextComment;

    public Comment() {
    }

    public Comment(Page page, String content, Date wroteOn, User wroteBy) {
        this.page = page;
        this.content = content;
        this.wroteOn = wroteOn;
        this.wroteBy = wroteBy;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getWroteOn() {
        return wroteOn;
    }

    public void setWroteOn(Date wroteOn) {
        this.wroteOn = wroteOn;
    }

    public User getWroteBy() {
        return wroteBy;
    }

    public void setWroteBy(User wroteBy) {
        this.wroteBy = wroteBy;
    }

    public Comment getPreviousComment() {
        return previousComment;
    }

    public void setPreviousComment(Comment previousComment) {
        this.previousComment = previousComment;
    }

    public Comment getNextComment() {
        return nextComment;
    }

    public void setNextComment(Comment nextComment) {
        this.nextComment = nextComment;
    }
}
