package com.had.adminservice.responseBody;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityResponseBody {

    @JsonIgnore
    private Long facilityId;

    private String facilityUFID;
    private String facilityState;
    private String facilityDistrict;
    private String facilitySubDistrict;
    private String facilityCountry;
    private String facilityType;

    @JsonIgnore
    private Long userId;

    private String facilityEmail;
    private String facilityName;

    @JsonIgnore
    private String facilityLastName;

    private String facilityContact;
    private String facilityLoginId;
    private boolean isFacilityActive;

    // Constructor with message parameter
//    public FacilityResponseBody(String message) {
//        this.facilityUFID = message;
//    }
}
