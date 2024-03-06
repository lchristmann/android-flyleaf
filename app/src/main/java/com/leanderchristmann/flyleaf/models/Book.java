package com.leanderchristmann.flyleaf.models;

public class Book {

    private final String title;     //blank final variable, can only be initialized one time
    private final String author;

    public Book(String title, String author){
        this.title = title;
        this.author = author;
    }

    //only getters needed, setting always by constructor, no changing possible after
    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }
}
