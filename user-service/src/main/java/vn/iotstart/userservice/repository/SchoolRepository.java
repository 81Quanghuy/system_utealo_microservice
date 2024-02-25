package com.trvankiet.app.repository;

import com.trvankiet.app.entity.School;
import io.micrometer.core.instrument.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {
    List<School> findAllByDistrictId(Integer districtId);
    Optional<School> findByCode(String code);
}
