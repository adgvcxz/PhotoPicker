package com.adgvcxz.photopicker.util;

import java.util.ArrayList;

/**
 * zhaowei
 * Created by zhaowei on 16/1/13.
 */
public class PhotoDir {
    private String id;
    private String cover;
    private String name;
    private ArrayList<String> photos;

    public PhotoDir(String id, String name) {
        this.id = id;
        this.name = name;
        photos = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof PhotoDir) && ((PhotoDir) o).id.equals(id);
    }

    public void setCover(String c) {
        cover = c;
    }

    public void addPhoto(String path) {
        photos.add(path);
    }

    public void addAll(ArrayList<String> paths) {
        photos.addAll(paths);
    }


    public ArrayList<String> getPhotos() {
        return photos;
    }

    public String getName() {
        return name;
    }

    public String getCover() {
        return cover;
    }

}
