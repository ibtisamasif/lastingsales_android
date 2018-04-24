package com.example.muzafarimran.lastingsales.carditems;

public class CommentItem {
    private String commentText;
    private String createdAt;
    private String updatedAt;
    private String created_by;
    private boolean left;

    public CommentItem(String commentText, String createdAt, String updatedAt ,String created_by, boolean left) {
        this.commentText = commentText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.created_by = created_by;
        this.left = left;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }
}
