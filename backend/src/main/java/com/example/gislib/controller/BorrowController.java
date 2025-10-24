package com.example.gislib.controller;

import com.example.gislib.entity.Book;
import com.example.gislib.entity.RentRecord;
import com.example.gislib.repository.BookRepository;
import com.example.gislib.repository.RentRecordRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController @RequestMapping("/api")
    @CrossOrigin(origins = "*") public class BorrowController {
 private
  final BookRepository bookRepository;
 private
  final RentRecordRepository rentRecordRepository;

 public
  BorrowController(BookRepository bookRepository,
                   RentRecordRepository rentRecordRepository) {
    this.bookRepository = bookRepository;
    this.rentRecordRepository = rentRecordRepository;
  }

  @PostMapping("/borrow") public ResponseEntity <
      ? > borrow(@RequestBody BorrowReq req) {
    Optional<Book> opt = bookRepository.findById(req.getBookId());
    if (!opt.isPresent())
      return ResponseEntity.badRequest().body("book not found");
    Book b = opt.get();
    RentRecord r = new RentRecord();
    r.setBookId(b.getId());
    r.setLibraryId(b.getLibraryId());
    r.setStudentId(req.getStudentId());
    r.setRentTime(LocalDateTime.now());
    rentRecordRepository.save(r);
    return ResponseEntity.ok(r);
  }

  @PostMapping("/return") public ResponseEntity <
      ? > returnBook(@RequestBody ReturnReq req) {
    Optional<RentRecord> opt = rentRecordRepository.findById(req.getRentId());
    if (!opt.isPresent())
      return ResponseEntity.badRequest().body("rent record not found");
    RentRecord r = opt.get();
    r.setReturnTime(LocalDateTime.now());
    rentRecordRepository.save(r);
    return ResponseEntity.ok(r);
  }

 public
  static class BorrowReq {
   private
    Long bookId;
   private
    Long studentId;
   public
    Long getBookId() { return bookId; }
   public
    void setBookId(Long v) { this.bookId = v; }
   public
    Long getStudentId() { return studentId; }
   public
    void setStudentId(Long v) { this.studentId = v; }
  } public static class ReturnReq {
   private
    Long rentId;
   private
    Long studentId;
   public
    Long getRentId() { return rentId; }
   public
    void setRentId(Long v) { this.rentId = v; }
   public
    Long getStudentId() { return studentId; }
   public
    void setStudentId(Long v) { this.studentId = v; }
  }
}
