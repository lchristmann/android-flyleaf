package com.leanderchristmann.flyleaf.models;

public class Category {

    private final String listName;
    private boolean expanded;

    public Category(String listName){
        this.listName = listName;
        this.expanded = false;
    }

    public String getListName(){
        return this.listName;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
//not setters for listName and dbTableName & made final, bcs those can only be set when constructed