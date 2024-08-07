package com.example.soundwaves;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Artist {
    private int id;
    private String name;
    private String img;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Artist(int id, String name, String img) {
        this.id = id;
        this.name = name;
        this.img = img;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", img=" + img +
                '}';
    }
}
