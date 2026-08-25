package com.project.qampus.controllers;

import com.project.qampus.dto.LoginRequestDTO;
import com.project.qampus.dto.RegisterRequestDTO;
import com.project.qampus.dto.ResponseDTO;
import com.project.qampus.model.BlacklistedToken;
import com.project.qampus.model.User;
import com.project.qampus.model.enums.Role;
import com.project.qampus.repositories.BlacklistedTokenRepository;
import com.project.qampus.repositories.UserRepository;
import com.project.qampus.service.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private BlacklistedTokenRepository blacklistRepository;

    @InjectMocks
    private AuthController authController;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId("user-1");
        existingUser.setName("Carlos");
        existingUser.setEmail("carlos@qampus.com");
        existingUser.setPassword("encoded_pass");
        existingUser.setRole(Role.STUDENT);
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        LoginRequestDTO loginDTO = new LoginRequestDTO("carlos@qampus.com", "password123");

        when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("password123", "encoded_pass")).thenReturn(true);
        when(tokenService.generateToken(existingUser)).thenReturn("jwt.token.here");

        ResponseEntity<ResponseDTO> response = authController.login(loginDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        assertEquals("Carlos", responseDTO.name());
        assertEquals("jwt.token.here", responseDTO.token());
    }

    @Test
    void shouldReturnBadRequestOnLoginWhenPasswordDoesNotMatch() {
        LoginRequestDTO loginDTO = new LoginRequestDTO("carlos@qampus.com", "wrongpass");

        when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongpass", "encoded_pass")).thenReturn(false);

        ResponseEntity<ResponseDTO> response = authController.login(loginDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldRegisterNewUserSuccessfully() {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO("Lucas", "lucas@qampus.com", "pass123", Role.STUDENT);

        when(userRepository.findByEmail(registerDTO.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDTO.password())).thenReturn("encoded_pass");
        when(tokenService.generateToken(any(User.class))).thenReturn("new.user.token");

        ResponseEntity<ResponseDTO> response = authController.register(registerDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Lucas", response.getBody().name());
        assertEquals("new.user.token", response.getBody().token());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldReturnBadRequestOnRegisterWhenEmailAlreadyExists() {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO("Carlos", "carlos@qampus.com", "pass123", Role.STUDENT);

        when(userRepository.findByEmail(registerDTO.email())).thenReturn(Optional.of(existingUser));

        ResponseEntity<ResponseDTO> response = authController.register(registerDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLogoutUserAndAddTokenToBlacklist() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid.token.to.blacklist");

        ResponseEntity<Void> response = authController.logout(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(blacklistRepository, times(1)).save(any(BlacklistedToken.class));
    }

    @Test
    void shouldReturnBadRequestOnLogoutWithoutBearerToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        ResponseEntity<Void> response = authController.logout(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(blacklistRepository, never()).save(any(BlacklistedToken.class));
    }
}
