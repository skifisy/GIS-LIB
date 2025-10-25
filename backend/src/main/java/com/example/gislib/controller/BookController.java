package com.example.gislib.controller;

import com.example.gislib.entity.Book;
import com.example.gislib.repository.BookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController @RequestMapping("/api/books")
    @CrossOrigin(origins = "*") public class BookController {
 private
  final BookRepository bookRepository;

 public
  BookController(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @GetMapping public List<Book> listByLibrary(@RequestParam(required = false)
                                                  Long libraryId) {
    if (libraryId == null) return bookRepository.findAll();
    return bookRepository.findAll()
        .stream()
        .filter(b->libraryId.equals(b.getLibraryId()))
        .collect(Collectors.toList());
  }

  @PostMapping public ResponseEntity < ? > create(@RequestBody Book b) {
    Book saved = bookRepository.save(b);
    return ResponseEntity.ok(saved);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    try {
      bookRepository.deleteById(id);
      return ResponseEntity.noContent().build();
    } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
      return ResponseEntity.notFound().build();
    }
  }
}
