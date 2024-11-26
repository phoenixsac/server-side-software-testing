package com.had.adminservice.responseBody;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientCardDetailResponseBody {
    private Long id;
    private String name;
    private String gender;
    private Integer age;
    private String bloodGroup;
    private String contact;
}
