package com.example.gislib.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "rent") public class RentRecord {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  @Column(name = "book_id") private Long bookId;

  @Column(name = "library_id") private Long libraryId;

  @Column(name = "student_id") private Long studentId;

  @Column(name = "rent_time") private LocalDateTime rentTime;

  @Column(name = "return_time") private LocalDateTime returnTime;

 public
  RentRecord() {}

  // getters/setters
 public
  Long getId() { return id; }
 public
  void setId(Long id) { this.id = id; }
 public
  Long getBookId() { return bookId; }
 public
  void setBookId(Long bookId) { this.bookId = bookId; }
 public
  Long getLibraryId() { return libraryId; }
 public
  void setLibraryId(Long libraryId) { this.libraryId = libraryId; }
 public
  Long getStudentId() { return studentId; }
 public
  void setStudentId(Long studentId) { this.studentId = studentId; }
 public
  LocalDateTime getRentTime() { return rentTime; }
 public
  void setRentTime(LocalDateTime rentTime) { this.rentTime = rentTime; }
 public
  LocalDateTime getReturnTime() { return returnTime; }
 public
  void setReturnTime(LocalDateTime returnTime) { this.returnTime = returnTime; }
}
