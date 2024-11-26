package com.had.adminservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private String gender;

    @Column(name = "address")
    private String address;


    @Column(name = "blood_grp")
    private String bloodGroup;

    @Column(name = "guardian_first_name")
    private String guardianFirstName;

    @Column(name = "guardian_last_name")
    private String guardianLastName;

    @Column(name = "guardian_contact")
    private String guardianContact;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
