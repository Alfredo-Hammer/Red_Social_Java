package com.hammer67.ajecimface.models;

public class Coments {

    private String id;
    private String coments;
    private String idUser;
    private String idPost;
    Long timestamp;

    public Coments() {

    }

    public Coments(String id, String coments, String idUser, String idPost, Long timestamp) {
        this.id = id;
        this.coments = coments;
        this.idUser = idUser;
        this.idPost = idPost;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComents() {
        return coments;
    }

    public void setComents(String coments) {
        this.coments = coments;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
