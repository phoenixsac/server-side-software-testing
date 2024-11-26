package com.had.adminservice.repository;

import com.had.adminservice.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {

    List<Facility> findByType(String type);

    @Query(value = "SELECT * FROM facility WHERE ufid = :facilityId", nativeQuery = true)
    Optional<Facility> findFacilityById(String facilityId);

    @Query(value = "SELECT ufid FROM facility WHERE ufid = :affiliatedFacilityId", nativeQuery = true)
    String findUfidFromFacility(String affiliatedFacilityId);

    @Query(value = "SELECT * FROM facility", nativeQuery = true)
    List<Facility> findAllFacilities();
}
