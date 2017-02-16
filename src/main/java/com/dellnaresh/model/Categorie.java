package com.dellnaresh.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nmiriyal on 16/02/2017.
 */
public class Categorie {

    private int id;
    private String name;
    private String seo_token;
    private int level;
    private String parentCategory;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeo_token() {
        return seo_token;
    }

    public void setSeo_token(String seo_token) {
        this.seo_token = seo_token;
    }

    @Override
    public String toString() {
        return "Categorie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", seo_token='" + seo_token + '\'' +
                ", level=" + level +
                ", parentCategory='" + parentCategory + '\'' +
                '}';
    }
}
