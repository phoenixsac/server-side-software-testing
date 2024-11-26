package com.had.adminservice.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "professional")
public class Professional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @Column(name = "experience")
    private Integer experience;

    @Column(name = "affiliated_facility_id")
    private String affiliatedFacilityId;

    @Column(name = "specialization", length = 255)
    private String specialization;

    @Column(name = "system_of_medicine", length = 255)
    private String systemOfMedicine;


    @Column(name = "qualification", length = 255) // Added qualification field
    private String qualification;

    @Column(name = "status", length = 255) // Added status field
    private String status;

    @Column(name = "place_of_work", length = 255) // Added place_of_work field
    private String placeOfWork;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
