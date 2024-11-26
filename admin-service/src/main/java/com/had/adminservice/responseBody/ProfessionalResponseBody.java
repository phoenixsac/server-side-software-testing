package com.had.adminservice.responseBody;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProfessionalResponseBody {
    private Long professionalId;
    private String firstName;
    private String lastName;
    private String specialization;
    private String systemOfMedicine;
    private String contactNumber;
    private String emailId;
    private String qualification;
    private Integer yearsOfExperience;
    private String status;
    private String affiliatedFacilityId;
    private String placeOfWork;
}
