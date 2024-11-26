package com.had.adminservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "healthcare_professionals_registry")
public class HealthcareProfessionalsRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "healthcare_professional_id")
    private Long healthcareProfessionalId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "system_of_medicine")
    private String systemOfMedicine;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "status")
    private String status;

    @Column(name = "affiliated_facility_id")
    private String affiliatedFacilityId;

    @Column(name = "place_of_work")
    private String placeOfWork;
}
