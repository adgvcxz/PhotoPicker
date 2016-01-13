package com.adgvcxz.photopicker.util;

import java.util.ArrayList;

/**
 * zhaowei
 * Created by zhaowei on 16/1/13.
 */
public class PhotoDir {
    public String id;
    public String cover;
    public String name;
    public ArrayList<String> photos;

    @Override
    public boolean equals(Object o) {
        return (o instanceof PhotoDir) && ((PhotoDir) o).id.equals(id);
    }
}
