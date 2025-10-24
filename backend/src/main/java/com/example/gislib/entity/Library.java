package com.example.gislib.entity;

import org.locationtech.jts.geom.Point;

import javax.persistence.*;

@Entity @Table(name = "library") public class Library {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

 private
  String name;
 private
  String college;

  @Column(name = "number_of_books") private Long numberOfBooks;

  @Column(columnDefinition = "geometry(Point,4523)") private Point location;

 public
  Library() {}

  // getters / setters
 public
  Long getId() { return id; }
 public
  void setId(Long id) { this.id = id; }
 public
  String getName() { return name; }
 public
  void setName(String name) { this.name = name; }
 public
  String getCollege() { return college; }
 public
  void setCollege(String college) { this.college = college; }
 public
  Long getNumberOfBooks() { return numberOfBooks; }
 public
  void setNumberOfBooks(Long numberOfBooks) {
    this.numberOfBooks = numberOfBooks;
  }
 public
  Point getLocation() { return location; }
 public
  void setLocation(Point location) { this.location = location; }
}
