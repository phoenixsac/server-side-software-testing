package com.had.adminservice.service;

import com.had.adminservice.entity.*;


import com.had.adminservice.exception.ResourceNotFoundException;
import com.had.adminservice.repository.*;
import com.had.adminservice.responseBody.FacilityResponseBody;
import com.had.adminservice.responseBody.PatientCardDetailResponseBody;
import com.had.adminservice.responseBody.PatientResponseBody;
import com.had.adminservice.responseBody.ProfessionalResponseBody;
//import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private BCryptPasswordEncoder bcryptPwdEncoder;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HealthFacilityRegistryRepository hfrRepository;

    @Autowired
    private HealthcareProfessionalsRegistryRepository hprRepository;

    @Autowired
    PatientRepository patientRepository;


    public String addFacility(String facilityId) {
        Logger logger = LoggerFactory.getLogger(getClass());

        if (facilityId == null || facilityId.isEmpty() || !facilityId.contains("@")) {
            throw new IllegalArgumentException("Facility ID incorrect format.");
        }

        logger.info("Received request to add facility with id: {}", facilityId);

        Optional<Facility> existingFacility = facilityRepository.findFacilityById(facilityId);
        if (existingFacility.isPresent()) {
            logger.warn("Facility with id {} already exists", facilityId);
            return "Facility with the provided id already exists!";
        }

        HealthFacilityRegistry healthFacilityRegistry = hfrRepository.getByFacilityId(facilityId);

        if (healthFacilityRegistry == null) {
            logger.warn("Facility with id {} does not exist in Health Facility Registry", facilityId);
            return "Given facility does not exist in Health Facility Registry!";
        }

        String loginId = generateRandomLoginId(6);
        String password = generateRandomPassword(8);
        String hashedPassword = bcryptPwdEncoder.encode(password);

        // Creating and saving the User
        User user = User.builder()
                .contact(healthFacilityRegistry.getContactNumber())
                .email(healthFacilityRegistry.getEmailId())
                .firstName(healthFacilityRegistry.getFacilityName())
                .type(healthFacilityRegistry.getFacilityType())
                .password(hashedPassword)
                .isActive(true)
                .loginId(loginId)
                .build();

        logger.info("Creating user for facility: {}", healthFacilityRegistry.getFacilityName());

        // Creating the Facility and associating it with the User
        Facility facility = Facility.builder()
                .ufid(healthFacilityRegistry.getFacilityId())
                .country(healthFacilityRegistry.getCountry())
                .district(healthFacilityRegistry.getDistrict())
                .subDistrict(healthFacilityRegistry.getSubDistrict())
                .state(healthFacilityRegistry.getStateOrUt())
                .type(healthFacilityRegistry.getFacilityType())
                .user(user) // Associate the Facility with the User
                .build();

        Facility savedFacility = facilityRepository.save(facility);

        if (savedFacility != null) {
            logger.info("Facility added successfully with id: {}", facilityId);
            return "Success";
        } else {
            logger.error("Failed to add facility {}", facilityId);
            return "Failed to add facility";
        }
    }


    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generateRandomLoginId(int length) {
        StringBuilder loginId = new StringBuilder();
        Random random = new Random();

        // Generate random login ID by appending random characters from the CHARACTERS string
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            loginId.append(CHARACTERS.charAt(index));
        }

        return loginId.toString();
    }

    public String generateRandomPassword(int length) {
        // Define the character set for generating the password
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$";

        // Initialize StringBuilder to store the password
        StringBuilder password = new StringBuilder();

        // Generate random password by appending random characters from charset
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * charset.length());
            password.append(charset.charAt(index));
        }

        return password.toString();
    }

    public List<FacilityResponseBody> getAllFacilities() {
        List<Facility> facilities = facilityRepository.findAll();
        return mapFacilitiesToResponse(facilities);
    }


    public List<FacilityResponseBody> getAllFacilitiesByType(String type) {
        List<Facility> facilities = facilityRepository.findByType(type);
        return mapFacilitiesToResponse(facilities);
    }

//    public List<FacilityResponseBody> getAllFacilitiesByType(String type) {
//        try {
//            List<Facility> facilities = facilityRepository.findByType(type);
//            return mapFacilitiesToResponse(facilities);
//        } catch (Exception e) {
//            throw new RuntimeException("Database error occurred");
//        }
//    }



    public FacilityResponseBody getFacilityById(Long id) {
        Optional<Facility> facilityOptional = facilityRepository.findById(id);

        if (facilityOptional.isPresent()) {
            Facility facility = facilityOptional.get();
            if (!facility.getUser().isActive()) {
                throw new IllegalStateException("Facility with ID " + id + " is not active");
            }
            return mapFacilityToResponse(facility);
        } else {
            // Handle the case where the facility with the specified ID is not found
            throw new ResourceNotFoundException("Facility with ID " + id + " not found");
        }
    }

    private List<FacilityResponseBody> mapFacilitiesToResponse(List<Facility> facilities) {
        List<FacilityResponseBody> responseBodies = new ArrayList<>();
        for (Facility facility : facilities) {
            if (facility.getUser().isActive()) {
                FacilityResponseBody responseBody = mapFacilityToResponse(facility);
                responseBodies.add(responseBody);
            }
        }
        return responseBodies;
    }

    private FacilityResponseBody mapFacilityToResponse(Facility facility) {
        return FacilityResponseBody.builder()
                .facilityId(facility.getId())
                .facilityUFID(facility.getUfid())
                .facilityState(facility.getState())
                .facilityDistrict(facility.getDistrict())
                .facilitySubDistrict(facility.getSubDistrict())
                .facilityCountry(facility.getCountry())
                .facilityType(facility.getType())
                .isFacilityActive(facility.getUser().isActive())
                .userId(facility.getUser().getId())
                .facilityEmail(facility.getUser().getEmail())
                .facilityName(facility.getUser().getFirstName())
                .facilityLastName(facility.getUser().getLastName())
                .facilityContact(facility.getUser().getContact())
                .facilityLoginId(facility.getUser().getLoginId())
                .build();
    }

    public HealthFacilityRegistry validateFromHFR(String ufid) {
        return hfrRepository.getByFacilityId(ufid);
    }


    //SOFT-DELETE
    public String removeFacility(String facId) {
        // Check if the facility with the given ID exists
        Optional<Facility> facilityOptional = facilityRepository.findFacilityById(facId);
        if (facilityOptional.isPresent()) {
            Facility facility = facilityOptional.get();

            // Remove the facility from its associated user
            User user = facility.getUser();
            user.setActive(false);

            // Save the updated user
            userRepository.save(user);

            // Facility soft deletion successful
            return "Facility with ID " + facId + " soft deleted successfully";
        } else {
            // Handle the case where the facility with the specified ID is not found
            throw new ResourceNotFoundException("Facility with ID " + facId + " not found");
        }
    }


    //////////////////////////////////////////////////////////PROFESSIONAL////////////////////////////////////////////////////
    public String addProfessional(Long hpId) {
        Logger logger = LoggerFactory.getLogger(getClass());



        logger.info("Received request to add professional with id: {}", hpId);

        Optional<Professional> existingProfessional = professionalRepository.findProfessionalExists(hpId);
        if (existingProfessional.isPresent()) {
            logger.warn("Professional with hpId {} already exists", hpId);
            return "Professional with the provided id already exists!";
        }

        HealthcareProfessionalsRegistry healthcareProfessionalsRegistry = hprRepository.validateProessionalWithHPR(hpId);

        if (healthcareProfessionalsRegistry == null) {
            logger.warn("Professional with id {} does not exist in Healthcare Professional Registry", hpId);
            return "Given professional does not exist in Healthcare Professional Registry!";
        }

        String affiliatedFacilityId = hprRepository.getAffiliatedFacilityId(hpId);

        if (affiliatedFacilityId.equalsIgnoreCase(facilityRepository.findUfidFromFacility(affiliatedFacilityId))) {
            logger.info("Professional associated facility id {} exists in Facility table.", affiliatedFacilityId);
        } else if (affiliatedFacilityId.equalsIgnoreCase(hfrRepository.checkIFFacilityExistsInHFR(affiliatedFacilityId))) {
            logger.info("Professional associated facility id {} does not exist in Facility table, but exists in HFR table", affiliatedFacilityId);
            //addFacility(affiliatedFacilityId);
        } else {
            logger.warn("Professional associated facility id {} does not exist in Facility table, and in HFR table", affiliatedFacilityId);
            return "Professional associated facility id does not exist in Facility and HFR tables.";
        }
        if ("Facility added successfully".equalsIgnoreCase(addFacility(affiliatedFacilityId)) ||
                "Facility with the provided id already exists!".equalsIgnoreCase(addFacility(affiliatedFacilityId))) {
            String loginId = generateRandomLoginId(6);
            String password = generateRandomPassword(8);
            String hashedPassword = bcryptPwdEncoder.encode(password);

            // Creating and saving the User
            User user = User.builder()
                    .contact(healthcareProfessionalsRegistry.getContactNumber())
                    .email(healthcareProfessionalsRegistry.getEmailId())
                    .firstName(healthcareProfessionalsRegistry.getFirstName())
                    .lastName(healthcareProfessionalsRegistry.getLastName())
                    .type(healthcareProfessionalsRegistry.getSpecialization())
                    .password(hashedPassword)
                    .isActive(true)
                    .loginId(loginId)
                    .build();


            logger.info("Creating user entry for professional: {}", healthcareProfessionalsRegistry.getFirstName() + healthcareProfessionalsRegistry.getLastName());

            // Creating the Facility and associating it with the User
            Professional professional = Professional.builder()
                    .licenseNumber(String.valueOf(healthcareProfessionalsRegistry.getHealthcareProfessionalId()))
                    .experience(healthcareProfessionalsRegistry.getYearsOfExperience())
                    .affiliatedFacilityId(healthcareProfessionalsRegistry.getAffiliatedFacilityId())
                    .specialization(healthcareProfessionalsRegistry.getSpecialization())
                    .systemOfMedicine(healthcareProfessionalsRegistry.getSystemOfMedicine())
                    .qualification(healthcareProfessionalsRegistry.getQualification())
                    .status(healthcareProfessionalsRegistry.getStatus())
                    .placeOfWork(healthcareProfessionalsRegistry.getPlaceOfWork())
                    .user(user)
                    .build();

            logger.info("Creating facility: {}", hpId);

            professionalRepository.save(professional);

            logger.info("Professional added successfully with license/id: {}", hpId);

            return "Success";
        } else {
            return "Error creating professional due to facility addition issues";
        }

    }

    //SOFT-DELETE
    public String removeProfessional(Long id) {

        Optional<Professional> professionalOptional = professionalRepository.findProfessionalExists(id);
        if (professionalOptional.isPresent()) {
            Professional professional = professionalOptional.get();

            // Remove the facility from its associated user
            User user = professional.getUser();
            user.setActive(false);

            // Save the updated user
            userRepository.save(user);

            // Facility soft deletion successful
            return "Professional with ID " + id + " soft deleted successfully";
        } else {
            // Handle the case where the facility with the specified ID is not found
            throw new ResourceNotFoundException("Professional with ID " + id + " not found");
        }
    }


    public static ProfessionalResponseBody mapProfessionalToResponse(Professional professional) {
        return ProfessionalResponseBody.builder()
                .professionalId(professional.getId())
                .firstName(professional.getUser().getFirstName())
                .lastName(professional.getUser().getLastName())
                .specialization(professional.getSpecialization())
                .systemOfMedicine(professional.getSystemOfMedicine())
                .contactNumber(professional.getUser().getContact())
                .emailId(professional.getUser().getEmail())
                .qualification(professional.getQualification())
                .yearsOfExperience(professional.getExperience())
                .status(professional.getStatus())
                .affiliatedFacilityId(professional.getAffiliatedFacilityId())
                .placeOfWork(professional.getPlaceOfWork())
                .build();
    }

    private List<ProfessionalResponseBody> mapProfessionalsToResponse(List<Professional> facilities) {
        List<ProfessionalResponseBody> responseBodies = new ArrayList<>();
        for (Professional professional : facilities) {
            if (professional.getUser().isActive()) {
                ProfessionalResponseBody responseBody = mapProfessionalToResponse(professional);
                responseBodies.add(responseBody);
            }
        }
        return responseBodies;
    }

    public ProfessionalResponseBody getProfessionalById(Long id) {
        Optional<Professional> professionalOptional = professionalRepository.findById(id);

        if (professionalOptional.isPresent()) {
            Professional professional = professionalOptional.get();
            if (!professional.getUser().isActive()) {
                throw new IllegalStateException("Professional with ID " + id + " is not active");
            }
            return mapProfessionalToResponse(professional);
        } else {
            // Handle the case where the facility with the specified ID is not found
            throw new ResourceNotFoundException("Professional with ID " + id + " not found");
        }
    }

    public List<ProfessionalResponseBody> getAllProfessionals() {
        List<Professional> professionals = professionalRepository.findAll();
        return mapProfessionalsToResponse(professionals);
    }


    public List<ProfessionalResponseBody> getAllProfessionalsByType(String type) {
        List<Professional> professionals = professionalRepository.findByTypeOrSpecialization(type);
        return mapProfessionalsToResponse(professionals);
    }

    public List<PatientCardDetailResponseBody> getAllPatientDetails() {
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(this::mapToPatientCardDetailResponseBody)
                .collect(Collectors.toList());
    }

    private PatientCardDetailResponseBody mapToPatientCardDetailResponseBody(Patient patient) {
        return PatientCardDetailResponseBody.builder()
                .id(patient.getId())
                .name(patient.getUser().getFirstName() + " " + patient.getUser().getLastName())
                .gender(patient.getGender())
                .age(patient.getAge())
                .bloodGroup(patient.getBloodGroup())
                .contact(patient.getGuardianContact())
                .build();
    }

    public PatientResponseBody getPatientDetailsById(Long patientId) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);

        Patient patient = optionalPatient.orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));

        return PatientResponseBody.builder()
                .id(patient.getId())
                .name(patient.getUser().getFirstName() + " " + patient.getUser().getLastName())
                .gender(patient.getGender())
                .age(patient.getAge())
                .bloodGroup(patient.getBloodGroup())
                .contact(patient.getGuardianContact())
                .build();
    }
}
