package com.example.gislib.repository;

import com.example.gislib.entity.RentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository public interface RentRecordRepository extends
    JpaRepository<RentRecord, Long> {
}
