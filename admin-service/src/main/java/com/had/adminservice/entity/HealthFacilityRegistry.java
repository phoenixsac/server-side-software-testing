package com.had.adminservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
@Entity
@Table(name = "health_facility_registry")
public class HealthFacilityRegistry {

    @Id
    @Column(name = "facility_id", length = 255)
    private String facilityId;

    @Column(name = "facility_name", length = 255)
    private String facilityName;

    @Column(name = "system_of_medicine", length = 255)
    private String systemOfMedicine;

    @Column(name = "email_id", length = 255)
    private String emailId;

    @Column(name = "state_or_ut", length = 255)
    private String stateOrUt;

    @Column(name = "sub_district", length = 255)
    private String subDistrict;

    @Column(name = "facility_ownership", length = 20)
    private String facilityOwnership;

    @Column(name = "facility_type", length = 50)
    private String facilityType;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Column(name = "country", length = 255)
    private String country;

    @Column(name = "district", length = 255)
    private String district;

    @Column(name = "facility_region", length = 10)
    private String facilityRegion;

}
