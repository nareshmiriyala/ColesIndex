package com.dellnaresh.model;

/**
 * Created by nmiriyal on 16/02/2017.
 */
public final class CategorieBuilder {
    private int id;
    private String name;
    private String seo_token;
    private int level;
    private String parentCategory;
    private String url;
    private CategorieBuilder() {
    }

    public static CategorieBuilder aCategorie() {
        return new CategorieBuilder();
    }

    public CategorieBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public CategorieBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CategorieBuilder withSeo_token(String seo_token) {
        this.seo_token = seo_token;
        return this;
    }

    public CategorieBuilder withLevel(int level) {
        this.level = level;
        return this;
    }

    public CategorieBuilder withParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
        return this;
    }
    public CategorieBuilder withUrl(String url) {
        this.url = url;
        return this;
    }

    public Categorie build() {
        Categorie categorie = new Categorie();
        categorie.setId(id);
        categorie.setName(name);
        categorie.setSeo_token(seo_token);
        categorie.setLevel(level);
        categorie.setParentCategory(parentCategory);
        categorie.setUrl(url);
        return categorie;
    }
}
