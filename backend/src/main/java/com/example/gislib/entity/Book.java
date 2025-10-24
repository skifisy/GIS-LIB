package com.example.gislib.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "book") public class Book {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  @Column(name = "library_id") private Long libraryId;

 private
  String name;

  @Column(name = "publication_time") private LocalDateTime publicationTime;

 private
  String author;

  @Column(name = "press_id") private Long pressId;

 public
  Book() {}

  // getters/setters
 public
  Long getId() { return id; }
 public
  void setId(Long id) { this.id = id; }
 public
  Long getLibraryId() { return libraryId; }
 public
  void setLibraryId(Long libraryId) { this.libraryId = libraryId; }
 public
  String getName() { return name; }
 public
  void setName(String name) { this.name = name; }
 public
  LocalDateTime getPublicationTime() { return publicationTime; }
 public
  void setPublicationTime(LocalDateTime publicationTime) {
    this.publicationTime = publicationTime;
  }
 public
  String getAuthor() { return author; }
 public
  void setAuthor(String author) { this.author = author; }
 public
  Long getPressId() { return pressId; }
 public
  void setPressId(Long pressId) { this.pressId = pressId; }
}
