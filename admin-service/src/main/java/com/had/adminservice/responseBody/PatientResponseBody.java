package com.had.adminservice.responseBody;


import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseBody {

    private Long id;
    private String name;
    private String gender;
    private Integer age;
    private String bloodGroup;
    private String contact;

}
