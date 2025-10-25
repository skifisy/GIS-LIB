package com.example.gislib.controller;

import com.example.gislib.entity.RentRecord;
import com.example.gislib.repository.RentRecordRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@RestController @RequestMapping("/api/rents")
    @CrossOrigin(origins = "*") public class RentQueryController {
 private
  final RentRecordRepository rentRecordRepository;

 public
  RentQueryController(RentRecordRepository rentRecordRepository) {
    this.rentRecordRepository = rentRecordRepository;
  }

  @GetMapping public List<RentRecord> listByStudent(
      @RequestParam(required = false) Long studentId) {
    if (studentId == null) return rentRecordRepository.findAll();
    return rentRecordRepository.findAll()
        .stream()
        .filter(r->studentId.equals(r.getStudentId()))
        .collect(Collectors.toList());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    try {
      rentRecordRepository.deleteById(id);
      return ResponseEntity.noContent().build();
    } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
      return ResponseEntity.notFound().build();
    }
  }
}
