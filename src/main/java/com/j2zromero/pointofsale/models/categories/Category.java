package com.j2zromero.pointofsale.models.categories;

public class Category {
    private String name;
    private int id;

    public Category(){
    }
    public Category(String name, int id){
        this.name =name;
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
