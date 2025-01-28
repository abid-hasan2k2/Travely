package com.example.travely;

public class CategoryItem {
    private String name;
    private int imageResource;

    public CategoryItem(String name, int imageResource) {
        this.name = name;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResource;
    }
}