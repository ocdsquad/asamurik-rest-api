package com.asamurik_rest_api.controller;

import com.asamurik_rest_api.common.response.ErrorCode;
import com.asamurik_rest_api.dto.validation.ValidateUpdateUserDTO;
import com.asamurik_rest_api.handler.ResponseHandler;
import com.asamurik_rest_api.service.UserService;
import com.asamurik_rest_api.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class UserControllerTest extends AbstractTestNGSpringContextTests {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test(priority = 60)
    public void testGetUserProfileById_ValidToken() {
        String token = "validToken";
        String username = "testUser";

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userService.findByUsername(eq(username), any(HttpServletRequest.class)))
                .thenReturn(ResponseEntity.ok("User profile"));

        ResponseEntity<Object> response = userController.getUserProfileById(token, request);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), "User profile");
        verify(jwtUtil).validateToken(token);
        verify(jwtUtil).getUsernameFromToken(token);
        verify(userService).findByUsername(eq(username), any(HttpServletRequest.class));
    }

    @Test(priority = 70)
    public void testGetUserProfileById_InvalidToken() {
        String token = "invalidToken";

        when(jwtUtil.validateToken(token)).thenReturn(false);

        ResponseEntity<Object> response = userController.getUserProfileById(token, request);
        ResponseEntity<Object> expectedResponse = new ResponseHandler().handleResponse(
                ErrorCode.UNAUTHORIZED.getMessage(),
                HttpStatus.UNAUTHORIZED,
                null,
                null,
                request
        );
        assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertEquals(response.getBody(), expectedResponse.getBody());
        verify(jwtUtil).validateToken(token);
        verifyNoInteractions(userService);
    }

    @Test(priority = 80)
    public void testUpdateUserProfile_ValidToken() throws Exception {
        String token = "validToken";
        String username = "testUser";
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        ValidateUpdateUserDTO userDTO = new ValidateUpdateUserDTO();
        userDTO.setFullname("Test User");

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(request.getParameter("fullname")).thenReturn("Test User");
        when(userService.updateByUsername(eq(username), eq(file), any(), any(HttpServletRequest.class)))
                .thenReturn(ResponseEntity.ok("Profile updated"));

        ResponseEntity<Object> response = userController.updateUserProfile(token, file, request);
        ResponseEntity<Object> expectedResponse = new ResponseHandler().handleResponse(
                ErrorCode.UNAUTHORIZED.getMessage(),
                HttpStatus.UNAUTHORIZED,
                null,
                null,
                request
        );
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), "Profile updated");
        verify(jwtUtil).validateToken(token);
        verify(jwtUtil).getUsernameFromToken(token);
        verify(userService).updateByUsername(eq(username), eq(file), any(), any(HttpServletRequest.class));
    }

    @Test(priority = 90)
    public void testUpdateUserProfile_InvalidToken() {
        String token = "invalidToken";
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

        when(jwtUtil.validateToken(token)).thenReturn(false);

        ResponseEntity<Object> response = userController.updateUserProfile(token, file, request);
        ResponseEntity<Object> expectedResponse = new ResponseHandler().handleResponse(
                ErrorCode.UNAUTHORIZED.getMessage(),
                HttpStatus.UNAUTHORIZED,
                null,
                null,
                request
        );
        assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertEquals(response.getBody(), expectedResponse.getBody());
        verify(jwtUtil).validateToken(token);
        verifyNoInteractions(userService);
    }
}