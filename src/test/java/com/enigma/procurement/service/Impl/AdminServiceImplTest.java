package com.enigma.procurement.service.Impl;

import com.enigma.procurement.entity.Admin;
import com.enigma.procurement.entity.UserCredential;
import com.enigma.procurement.models.request.AdminRequest;
import com.enigma.procurement.models.response.AdminResponse;
import com.enigma.procurement.repository.AdminRepository;
import com.enigma.procurement.service.AdminService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminService adminService;


    //Entity
    Admin admin;
    @BeforeEach
    void setUp() {

        adminService = new AdminServiceImpl(adminRepository);
        admin = new Admin();
        admin.setId("1");
        admin.setName("Ari");
        admin.setPhoneNumber("012345");
    }


    @Test
    void testCreateAdminSuccess() {

        when(adminRepository.save(admin)).thenReturn(admin);

        Admin result = adminService.create(admin);

        assertNotNull(result);
        assertEquals("Ari", result.getName());
        assertEquals("012345", result.getPhoneNumber());
    }

    @Test
    void testDuplicateAdminEmail(){
        when(adminRepository.save(admin)).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
           adminService.create(admin);
        });
    }


    @Test
    void testFindByIdSuccess() {
        when(adminRepository.findById("1")).thenReturn(Optional.of(admin));

        Admin result = adminService.findById("1");

        Assertions.assertEquals(admin.getId(), result.getId());
    }

    @Test
    void testNotFoundId(){
        when(adminRepository.findById("12")).thenThrow(ResponseStatusException.class);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            adminService.findById("12");
        });
    }

    @Test
    void testGetEmailByIdSuccess() {

        admin.setUserCredential(new UserCredential());
        admin.getUserCredential().setEmail("ari@test.com");

        admin.getUserCredential().setEmail("ari@test.com");

        when(adminRepository.findAdminById(admin.getId())).thenReturn(Optional.of(admin));

        String email = adminService.getEmailById(admin.getId());

        assertNotNull(email);
        assertEquals("ari@test.com", email);
    }

    @Test
    void TestNotFoundEmail(){
        when(adminRepository.findById("12")).thenThrow(ResponseStatusException.class);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            adminService.findById("12");
        });
    }

    @Test
    void testUpdateAdminSuccess() {
        AdminRequest request = new AdminRequest();
        request.setAdminId("1");
        request.setName("Ari");
        request.setPhoneNumber("012345");

        when(adminRepository.findById("1")).thenReturn(java.util.Optional.of(admin));
        when(adminRepository.save(admin)).thenReturn(admin);

        AdminResponse result = adminService.update(request);

        assertNotNull(result);
        assertEquals("Ari", result.getName());
    }


    @Test
    void testUpdateAdminNotFound() {
        AdminRequest request = new AdminRequest();
        request.setAdminId("999");
        request.setName("Ari");
        request.setPhoneNumber("012345");

        when(adminRepository.findById("999")).thenReturn(java.util.Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            adminService.update(request);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Admin not found", exception.getReason());
    }

}