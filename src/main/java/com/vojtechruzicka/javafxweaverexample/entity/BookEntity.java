package com.vojtechruzicka.javafxweaverexample.entity;

import javax.persistence.*;

@Entity
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id", nullable = false)
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String genre;
    private String year;
    public BookEntity(){}


    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) {this.title = title;}
    public String getAuthor() { return author; }
    public void settAuthor (String author) { this.author = author; }
    public String getPublisher() { return publisher;}
    public void setPublisher (String publisher) { this.publisher = publisher; }
    public String getGenre() { return genre; }
    public void setGenre (String genre) { this.genre = genre; }
    public String getYear(){
        return year;
    }
    public void setYear(String year) {this.year = year;}

}
