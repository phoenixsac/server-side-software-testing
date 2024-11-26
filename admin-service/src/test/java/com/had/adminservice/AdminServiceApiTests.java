package com.had.adminservice;

import com.had.adminservice.controllers.AdminController;
import com.had.adminservice.exception.ResourceNotFoundException;
import com.had.adminservice.service.AdminService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class AdminServiceApiTests {

    private AdminService adminService;


    // Mock setup for each test
    @BeforeEach
    public void initializeMocks() {
        adminService = Mockito.mock(AdminService.class);
        AdminController adminController = new AdminController();
        adminController.setAdminService(adminService); // Use the setter here
        RestAssuredMockMvc.standaloneSetup(adminController);
    }

    // Base URI setup for RestAssured
    @BeforeAll
    public static void setupBaseURI() {
        RestAssured.baseURI = "http://localhost:8082";
    }

    //----------------------------------------------------------------FACILITY----------------------------------------------------------//

    // Test: Add Facility - Success
    @Test
    public void testAddFacility_Success() {
        given()
                .param("facilityId", "sunrisehospital007@ch.ndhm")
                .when()
                .post("/admin/add-facility")
                .then()
                .statusCode(200)
                .body(equalTo("Success"));
    }


    // add facility with empty facility id
    @Test
    public void testAddFacility_EmptyId() {
        given()
                .param("facilityId", "")
                .when()
                .post("/admin/add-facility")
                .then()
                .statusCode(400)
                .body(equalTo("Error processing data: Facility ID incorrect format."));
    }


    // Test: Add Facility - Already Exists
    @Test
    public void testAddFacility_AlreadyExists() {
        given()
                .param("facilityId", "advancedlab018@ch.ndhm")  // Assume this ID already exists
                .when()
                .post("/admin/add-facility")
                .then()
                .statusCode(400)  // Expecting a 400 response for already existing facility
                .body(equalTo("Facility with the provided id already exists!"));
    }


    // Test: Get All Facilities
    @Test
    public void testGetAllFacilities() {
        when()
                .get("/admin/all-facilities")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0)); // Assumes at least one facility exists
    }



    // Test: Get All Facilities by Type
    @Test
    public void testGetAllFacilitiesByType() {
        given()
                .param("type", "Hospital") // Example facility type
                .when()
                .get("/admin/all-facilities-by-type")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0)); // Assumes facilities of that type exist
    }

    // Test: Get Facility By ID - Success
    @Test
    public void testGetFacilityById_Success() {
        given()
                .param("id", 2)  // Example facility ID
                .when()
                .get("/admin/facility-by-id")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("facilityName", equalTo("lab1")); // Assuming this facility has the name 'Test Facility'
    }

    // Test: Get Facility By ID - Not Found
    @Test
    public void testGetFacilityById_NotFound() {
        given()
                .param("id", 999)  // Non-existing facility ID
                .when()
                .get("/admin/facility-by-id")
                .then()
                .statusCode(404)
                .body(equalTo("Facility with ID 999 not found"));
    }

    // Test: Remove Facility - Success
    @Test
    public void testRemoveFacility_Success() {
        given()
                .param("facilityId", "healthscan002@ch.ndhm")
                .when()
                .delete("/admin/remove-facility")
                .then()
                .statusCode(200)
                .body(equalTo("Facility with ID healthscan002@ch.ndhm deleted successfully."));
    }

    // Test: Remove Facility - Not Found
    @Test
    public void testRemoveFacility_NotFound() {
        given()
                .param("facilityId", "F999") // Non-existing facility ID
                .when()
                .delete("/admin/remove-facility")
                .then()
                .statusCode(404)
                .body(equalTo("Facility not found: Facility with ID F999 not found"));
    }

    @Test
    public void testFacilityById_ResourceNotFoundException() {
        Mockito.when(adminService.getFacilityById(111L)).thenThrow(new ResourceNotFoundException("Facility not found"));

        RestAssuredMockMvc.given()
                .queryParam("id", 111L)
                .when()
                .get("/admin/facility-by-id")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .body(equalTo("Facility not found"));
    }

    @Test
    public void testFacilityById_InternalServerError() {
        Mockito.when(adminService.getFacilityById(1L)).thenThrow(new RuntimeException("Unexpected error"));

        RestAssuredMockMvc.given()
                .queryParam("id", 1L)
                .when()
                .get("/admin/facility-by-id")
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(equalTo("Internal server error"));
    }

    @Test
    public void testAddFacility_InternalServerError() {
        Mockito.when(adminService.addFacility("facility-123")).thenThrow(new RuntimeException("Database error"));

        RestAssuredMockMvc.given()
                .queryParam("facilityId", "facility-123")
                .when()
                .post("/admin/add-facility")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Error processing data: Database error"));
    }

    @Test
    public void testRemoveFacility_ResourceNotFoundException() {
        Mockito.doThrow(new ResourceNotFoundException("Facility not found"))
                .when(adminService).removeFacility("facility-123");

        RestAssuredMockMvc.given()
                .queryParam("facilityId", "facility-123")
                .when()
                .delete("/admin/remove-facility")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(equalTo("Facility not found: Facility not found"));
    }


    @Test
    public void testGetAllFacilities_InternalServerError() {
        Mockito.when(adminService.getAllFacilities()).thenThrow(new RuntimeException("Database error"));

        RestAssuredMockMvc.when()
                .get("/admin/all-facilities")
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }



    //----------------------------------------------------------------PROFESSIONAL----------------------------------------------------------//
    @Test
    public void testAddProfessional_FacilityNotExist() {
        given()
                .param("hpId", 23456789012345L)  // Replace with a relevant id for testing
                .when()
                .post("admin/add-professional")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Professional associated facility id does not exist in Facility and HFR tables."));
    }


    // add pro with negative facility id
    @Test
    public void testAddProfessional_NegId() {
        given()
                .param("hpId", "-889456")
                .when()
                .post("/admin/add-professional")
                .then()
                .statusCode(400)
                .body(equalTo("Incorrect format for hpId."));
    }



    // Test: Add Professional - Already Exists
    @Test
    public void testAddProfessional_AlreadyExists() {
        given()
                .param("hpId", 10123456789012L)  // Assume this professional already exists
                .when()
                .post("/admin/add-professional")
                .then()
                .statusCode(400)
                .body(equalTo("Professional with the provided id already exists!"));
    }


    @Test
    void testAddProfessional_Success() {
        given()
                .queryParam("hpId", 34567890123456L) // Replace with a valid hpId for the success scenario
                .when()
                .post("/admin/add-professional")
                .then()
                .statusCode(200) // Expecting HTTP 200 for success
                .contentType("application/json")
                .body(equalTo("Success")); // Assuming "Success" is the response body for a successful addition
    }


    // Test: Remove Professional - Success
    @Test
    public void testRemoveProfessional_Success() {
        given()
                .param("id", 10123456789012L)  // Example professional ID
                .when()
                .delete("/admin/remove-professional")
                .then()
                .statusCode(200)
                .body(equalTo("Professional with ID 10123456789012 deleted successfully."));
    }

    // Test: Remove Professional - Not Found
    @Test
    public void testRemoveProfessional_NotFound() {
        given()
                .param("id", 999) // Non-existing professional ID
                .when()
                .delete("/admin/remove-professional")
                .then()
                .statusCode(404)
                .body(equalTo("Professional not found: Professional with ID 999 not found"));
    }

    // Test: Get All Professionals
    @Test
    public void testGetAllProfessionals() {
        when()
                .get("/admin/all-professionals")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0)); // Assumes at least one professional exists
    }

    // Test: Get All Professionals by Type
    @Test
    public void testGetAllProfessionalsByType() {
        given()
                .param("type", "Doctor") // Example professional type
                .when()
                .get("/admin/all-professionals-by-type")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0)); // Assumes professionals of that type exist
    }

    // Test: Get Professional By ID - Success
    @Test
    public void testGetProfessionalById_Success() {
        given()
                .param("id", 5)  // Example professional ID
                .when()
                .get("/admin/professional-by-id")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("doc3_fname")); // Assuming this professional has the first name 'John'
    }

    // Test: Get Professional By ID - Not Found
    @Test
    public void testGetProfessionalById_NotFound() {
        given()
                .param("id", 999)  // Non-existing professional ID
                .when()
                .get("/admin/professional-by-id")
                .then()
                .statusCode(404)
                .body(equalTo("Professional with ID 999 not found"));
    }



    //----------------------------------------------------------------PATIENT----------------------------------------------------------//

    // Test: Get All Patient Details
    @Test
    public void testGetAllPatientDetails() {
        when()
                .get("/admin/all-patients")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0)); // Assumes at least one patient exists
    }

    // Test: Get Patient Details By ID - Success
    @Test
    public void testGetPatientDetailsById_Success() {
        given()
                .param("patientId", 1)  // Example patient ID
                .when()
                .get("/admin/details-by-id")
                .then()
                .statusCode(200)
                .body("name", equalTo("Aromal A")); // Assuming this patient has the name 'John Doe'
    }

    // Test: Get Patient Details By ID - Not Found
    @Test
    public void testGetPatientDetailsById_NotFound() {
        given()
                .param("patientId", 999)  // Non-existing patient ID
                .when()
                .get("/admin/details-by-id")
                .then()
                .statusCode(404)
                .body(equalTo("Patient not found with ID: 999"));
    }


    @Test
    public void testGetAllPatients_InternalServerError() {
        Mockito.when(adminService.addFacility("facility-123")).thenThrow(new RuntimeException("Database error"));

        RestAssuredMockMvc.given()
                .queryParam("facilityId", "facility-123")
                .when()
                .post("/admin/add-facility")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Error processing data: Database error"));
    }

}
