package com.had.adminservice;

import com.had.adminservice.entity.*;
import com.had.adminservice.exception.ResourceNotFoundException;
import com.had.adminservice.repository.*;
import com.had.adminservice.responseBody.FacilityResponseBody;
import com.had.adminservice.responseBody.PatientCardDetailResponseBody;
import com.had.adminservice.responseBody.PatientResponseBody;
import com.had.adminservice.responseBody.ProfessionalResponseBody;
import com.had.adminservice.service.AdminService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceUnitTests {

    @InjectMocks
    private AdminService adminService;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private FacilityRepository facilityRepository;


    @Mock
    private ProfessionalRepository professionalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HealthFacilityRegistryRepository hfrRepository;

    @Mock
    private HealthcareProfessionalsRegistryRepository hprRepository;

    @Mock
    private PatientRepository patientRepository;


    private static final String TEST_FACILITY_ID = "1";
    private static final Long TEST_ID = 1L;


//---------------------------------------------FACILITY--------------------------------------------------------------//

    // Test: getFacilityById - Success
    @Test
    void testGetFacilityById_Success() {
        Facility mockFacility = new Facility();
        User mockUser = new User();
        mockUser.setActive(true);
        mockFacility.setUser(mockUser);

        when(facilityRepository.findById(TEST_ID)).thenReturn(Optional.of(mockFacility));

        FacilityResponseBody response = adminService.getFacilityById(TEST_ID);

        assertNotNull(response);
        assertTrue(response.isFacilityActive());
    }

    // Test: getFacilityById - Not Found
    @Test
    void testGetFacilityById_NotFound() {
        when(facilityRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.getFacilityById(TEST_ID));
    }

    // Test: getAllFacilities
    @Test
    void testGetAllFacilities() {
        List<Facility> facilities = new ArrayList<>();
        Facility activeFacility = new Facility();
        User activeUser = new User();
        activeUser.setActive(true);
        activeFacility.setUser(activeUser);
        facilities.add(activeFacility);

        when(facilityRepository.findAll()).thenReturn(facilities);

        List<FacilityResponseBody> response = adminService.getAllFacilities();

        assertEquals(1, response.size());
        assertTrue(response.get(0).isFacilityActive());
    }



    // Test: removeFacility - Not Found
    @Test
    void testRemoveFacility_NotFound() {
        when(facilityRepository.findFacilityById(TEST_FACILITY_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.removeFacility(TEST_FACILITY_ID));
    }

    //-----------------------------------------------PROFESSIONAL-----------------------------------------


    // Test: addProfessional - Already Exists
    @Test
    void testAddProfessional_AlreadyExists() {
        when(professionalRepository.findProfessionalExists(TEST_ID)).thenReturn(Optional.of(new Professional()));

        String result = adminService.addProfessional(TEST_ID);

        assertEquals("Professional with the provided id already exists!", result);
        verify(professionalRepository, never()).save(any(Professional.class));
    }


    //------------------------------------------------PATIENT------------------------------------------------------
    // Test: getAllPatients
    @Test
    void testGetAllPatients() {
        List<Patient> patients = new ArrayList<>();
        Patient patient = new Patient();
        User user = new User();
        user.setFirstName("Alice");
        user.setLastName("Smith");
        patient.setUser(user);
        patient.setGender("Female");
        patients.add(patient);

        when(patientRepository.findAll()).thenReturn(patients);

        List<PatientCardDetailResponseBody> response = adminService.getAllPatientDetails();

        assertEquals(1, response.size());
        assertEquals("Alice Smith", response.get(0).getName());
    }

    // Test: getPatientDetailsById - Success
    @Test
    void testGetPatientDetailsById_Success() {
        Patient mockPatient = new Patient();
        User mockUser = new User();
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockPatient.setUser(mockUser);

        when(patientRepository.findById(TEST_ID)).thenReturn(Optional.of(mockPatient));

        PatientResponseBody response = adminService.getPatientDetailsById(TEST_ID);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
    }


    //------------------------------------MISC--------------------------------------------------

    @Test
    void testGenerateRandomLoginId() {
        String loginId = adminService.generateRandomLoginId(8);
        assertNotNull(loginId);
        assertEquals(8, loginId.length());
    }

    @Test
    void testGenerateRandomPassword() {
        String password = adminService.generateRandomPassword(10);
        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void validateFromHFR_ValidUfid_ReturnsHealthFacilityRegistry() {
        String ufid = "advancedlab018@ch.ndhm";
        HealthFacilityRegistry mockRegistry = new HealthFacilityRegistry();
        mockRegistry.setFacilityId(ufid); // Set relevant properties as needed

        Mockito.when(hfrRepository.getByFacilityId(ufid)).thenReturn(mockRegistry);

        HealthFacilityRegistry result = adminService.validateFromHFR(ufid);

        Assertions.assertNotNull(result, "The result should not be null");
        Assertions.assertEquals(ufid, result.getFacilityId(), "The facility ID should match");
        Mockito.verify(hfrRepository, Mockito.times(1)).getByFacilityId(ufid);
    }

}
