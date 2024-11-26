package com.had.adminservice.repository;

import com.had.adminservice.entity.HealthFacilityRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthFacilityRegistryRepository extends JpaRepository<HealthFacilityRegistry, String> {

    @Query(value = "SELECT * FROM health_facility_registry WHERE facility_id = :facilityId", nativeQuery = true)
    HealthFacilityRegistry getByFacilityId(String facilityId);

    @Query(value = "SELECT facility_id FROM health_facility_registry WHERE facility_id = :facilityId", nativeQuery = true)
    String checkIFFacilityExistsInHFR(String facilityId);
}
