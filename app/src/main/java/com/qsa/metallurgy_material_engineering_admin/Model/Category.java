package com.qsa.metallurgy_material_engineering_admin.Model;

public class Category {
    String id, categoryName, categoryImage;

    public Category() {

    }

    public Category(String id, String categoryName, String categoryImage) {
        this.id = id;
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }
}
