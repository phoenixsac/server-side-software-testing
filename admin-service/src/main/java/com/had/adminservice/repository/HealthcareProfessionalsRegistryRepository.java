package com.had.adminservice.repository;

import com.had.adminservice.entity.HealthcareProfessionalsRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HealthcareProfessionalsRegistryRepository extends JpaRepository<HealthcareProfessionalsRegistry, Long> {

    @Query(value = "SELECT * FROM healthcare_professionals_registry WHERE healthcare_professional_id = :hpId", nativeQuery = true)
    HealthcareProfessionalsRegistry validateProessionalWithHPR(Long hpId);

    @Query(value = "SELECT affiliated_facility_id FROM healthcare_professionals_registry WHERE healthcare_professional_id = :hpId", nativeQuery = true)
    String getAffiliatedFacilityId(Long hpId);

}
