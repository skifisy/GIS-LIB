package com.example.gislib.repository;

import com.example.gislib.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository public interface StudentRepository extends
    JpaRepository<Student, Long> {
}
