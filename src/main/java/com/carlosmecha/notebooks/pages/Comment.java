package com.carlosmecha.notebooks.pages;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Comment model.
 *
 * Created by carlos on 4/01/17.
 */
public class Comment {

    private int id;
    private int pageId;
    private String content;
    private Date wroteOn;
    private String wroteBy;
    private int previousCommentId;
    private int nextCommentId;

    protected Comment() {
    }

    public static Comment fromRow(ResultSet row) throws SQLException {
        // id, page_id, content, wrote_on, wrote_by, previous_comment_id, next_comment_id
        Comment comment = new Comment();
        comment.id = row.getInt("id");
        comment.pageId = row.getInt("page_id");
        comment.content = row.getString("content");
        comment.wroteOn = new Date(row.getTimestamp("wrote_on").getTime());
        comment.wroteBy = row.getString("wrote_by");
        comment.previousCommentId = row.getInt("previous_comment_id");
        if (row.wasNull()) {
            comment.previousCommentId = -1;
        }
        comment.nextCommentId = row.getInt("next_comment_id");
        if (row.wasNull()) {
            comment.nextCommentId = -1;
        }
        return comment;
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public int getPageId() {
        return pageId;
    }

    protected void setPage(int pageId) {
        this.pageId = pageId;
    }

    public String getContent() {
        return content;
    }

    protected void setContent(String content) {
        this.content = content;
    }

    public Date getWroteOn() {
        return wroteOn;
    }

    protected void setWroteOn(Date wroteOn) {
        this.wroteOn = wroteOn;
    }

    public String getWroteBy() {
        return wroteBy;
    }

    protected void setWroteBy(String wroteBy) {
        this.wroteBy = wroteBy;
    }

    public int getPreviousCommentId() {
        return previousCommentId;
    }

    protected void setPreviousCommentId(int previousCommentId) {
        this.previousCommentId = previousCommentId;
    }

    public int getNextCommentId() {
        return nextCommentId;
    }

    public void setNextCommentId(int nextCommentId) {
        this.nextCommentId = nextCommentId;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass().equals(Comment.class)) && ((Comment) obj).id == id; 
    }

    @Override
    public int hashCode() {
        return id;
    }
}
