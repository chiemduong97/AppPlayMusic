package com.example.lab1;

public class Music {
    private int id;
    private int image;
    private String name;
    private int media;
    public Music(){}
    public Music(int id,int image,String name,int media){
        this.id=id;
        this.image=image;
        this.name=name;
        this.media=media;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMedia() {
        return media;
    }

    public void setMedia(int media) {
        this.media = media;
    }
}
