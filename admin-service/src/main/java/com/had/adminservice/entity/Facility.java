package com.had.adminservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "facility")
public class Facility {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "ufid", nullable = false)
    private String ufid;

    @Column(name = "state")
    private String state;

    @Column(name = "district")
    private String district;

    @Column(name = "sub_district")
    private String subDistrict;

    @Column(name = "country")
    private String country;

    @Column(name = "type", length = 50)
    private String type;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
