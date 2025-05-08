package com.nashrookie.lavish.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.nashrookie.lavish.dto.filter.UserFilterDto;
import com.nashrookie.lavish.dto.request.LoginRequest;
import com.nashrookie.lavish.dto.request.RegisterRequest;
import com.nashrookie.lavish.dto.request.UpdateUserIsActiveDto;
import com.nashrookie.lavish.dto.response.AuthResponse;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.dto.response.UserInAdminDto;
import com.nashrookie.lavish.entity.AppUserDetails;
import com.nashrookie.lavish.entity.BlockedUser;
import com.nashrookie.lavish.entity.Role;
import com.nashrookie.lavish.entity.User;
import com.nashrookie.lavish.exception.ResourceNotFoundException;
import com.nashrookie.lavish.exception.UsernameAlreadyExistsException;
import com.nashrookie.lavish.repository.BlockedUserRepository;
import com.nashrookie.lavish.repository.RoleRepository;
import com.nashrookie.lavish.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private PasswordEncoder encoder;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private BlockedUserRepository blockedUserRepository;
    
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private Role userRole;
    private Role adminRole;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AppUserDetails appUserDetails;
    private List<User> mockUsers;

    @BeforeEach
    void setUp() {
        // Set refreshLifetime value using reflection
        ReflectionTestUtils.setField(userService, "refreshLifetime", 86400000L);

        // Create test user role
        userRole = new Role();
        userRole.setId(1);
        userRole.setName("USER");

        // Create admin role
        adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName("ADMIN");

        // Create test user
        mockUser = User.builder()
                .id(1L)
                .username("test@example.com")
                .password("hashedPassword")
                .firstname("Test")
                .lastname("User")
                .isActive(true)
                .build();
        mockUser.addRole(userRole);

        // Create test register request
        registerRequest = new RegisterRequest(
                "new@example.com",
                "password123",
                "New",
                "User"
        );

        // Create test login request
        loginRequest = new LoginRequest(
                "test@example.com",
                "password123"
        );

        // Create app user details
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        appUserDetails = AppUserDetails.builder()
                .id(1L)
                .username("test@example.com")
                .password("hashedPassword")
                .roles(roles)
                .build();

        // Create list of mock users
        mockUsers = List.of(mockUser);
    }

    @Test
    void register_WithNewUsername_ShouldRegisterUserAndReturnAuthResponse() throws UsernameAlreadyExistsException {
        // Arrange
        when(userRepository.findByIsActiveTrueAndUsername(registerRequest.email())).thenReturn(Optional.empty());
        when(encoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        
        User newUser = User.builder()
                .id(2L)
                .username(registerRequest.email())
                .password("encodedPassword")
                .firstname(registerRequest.firstname())
                .lastname(registerRequest.lastname())
                .build();
        newUser.addRole(userRole);
        
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(jwtService.generateAccessToken(eq(2L), eq(registerRequest.email()), anySet()))
                .thenReturn("jwtToken");

        // Act
        AuthResponse response = userService.register(registerRequest);

        // Assert
        assertEquals(2L, response.id());
        assertEquals(registerRequest.email(), response.username());
        assertEquals(List.of("USER"), response.roles());
        assertEquals("jwtToken", response.accessToken());
        
        verify(userRepository).findByIsActiveTrueAndUsername(registerRequest.email());
        verify(encoder).encode(registerRequest.password());
        verify(roleRepository).findByName("USER");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateAccessToken(eq(2L), eq(registerRequest.email()), anySet());
    }

    @Test
    void register_WithExistingUsername_ShouldThrowUsernameAlreadyExistsException() {
        // Arrange
        when(userRepository.findByIsActiveTrueAndUsername(registerRequest.email())).thenReturn(Optional.of(mockUser));

        // Act & Assert
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.register(registerRequest));
        
        verify(userRepository).findByIsActiveTrueAndUsername(registerRequest.email());
        verify(encoder, never()).encode(any());
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void verify_WithValidCredentials_ShouldReturnAuthResponse() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(appUserDetails);
        when(jwtService.generateAccessToken(eq(1L), eq("test@example.com"), anySet())).thenReturn("jwtToken");

        // Act
        AuthResponse response = userService.verify(loginRequest);

        // Assert
        assertEquals(1L, response.id());
        assertEquals("test@example.com", response.username());
        assertEquals(List.of("USER"), response.roles());
        assertEquals("jwtToken", response.accessToken());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateAccessToken(eq(1L), eq("test@example.com"), anySet());
    }

    @Test
    void getUserDetails_WithValidId_ShouldReturnUser() {
        // Arrange
        when(userRepository.findWithRolesById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        User result = userService.getUserDetails(1L);

        // Assert
        assertEquals(mockUser, result);
        verify(userRepository).findWithRolesById(1L);
    }

    @Test
    void getUserDetails_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(userRepository.findWithRolesById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserDetails(999L));
        verify(userRepository).findWithRolesById(999L);
    }

    @Test
    void getUsersAdminView_ShouldReturnPaginationResponse() {
        // Arrange
        UserFilterDto filterDto = new UserFilterDto("test", 0, 10, "username", "asc");
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(mockUsers, pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);

        // Act
        PaginationResponse<UserInAdminDto> result = userService.getUsersAdminView(filterDto, pageable);

        // Assert
        assertEquals(0, result.getPage());
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getData().size());
        assertEquals(mockUser.getId(), result.getData().get(0).id());
        assertEquals(mockUser.getUsername(), result.getData().get(0).username());
        verify(userRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getUsers_ShouldReturnPageOfUsers() {
        // Arrange
        UserFilterDto filterDto = new UserFilterDto("test", 0, 10, "username", "asc");
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(mockUsers, pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);

        // Act
        Page<User> result = userService.getUsers(filterDto, pageable);

        // Assert
        assertEquals(userPage, result);
        verify(userRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void updateUserActiveStatus_WithValidIdAndSetActive_ShouldActivateUser() {
        // Arrange
        UpdateUserIsActiveDto updateDto = new UpdateUserIsActiveDto(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        userService.updateUserActiveStatus(1L, updateDto);

        // Assert
        assertTrue(mockUser.getIsActive());
        verify(userRepository).findById(1L);
        verify(blockedUserRepository).deleteById(mockUser.getUsername());
        verify(userRepository).save(mockUser);
    }

    @Test
    void updateUserActiveStatus_WithValidIdAndSetInactive_ShouldDeactivateUser() {
        // Arrange
        UpdateUserIsActiveDto updateDto = new UpdateUserIsActiveDto(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        userService.updateUserActiveStatus(1L, updateDto);

        // Assert
        assertFalse(mockUser.getIsActive());
        verify(userRepository).findById(1L);
        verify(blockedUserRepository).save(any(BlockedUser.class));
        verify(userRepository).save(mockUser);
    }

    @Test
    void updateUserActiveStatus_WithAdminUser_ShouldThrowAuthorizationDeniedException() {
        // Arrange
        UpdateUserIsActiveDto updateDto = new UpdateUserIsActiveDto(false);
        User adminUser = User.builder()
                .id(2L)
                .username("admin@example.com")
                .password("hashedPassword")
                .firstname("Admin")
                .lastname("User")
                .isActive(true)
                .build();
        adminUser.addRole(adminRole);
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        // Act & Assert
        assertThrows(AuthorizationDeniedException.class, () -> userService.updateUserActiveStatus(2L, updateDto));
        verify(userRepository).findById(2L);
        verify(blockedUserRepository, never()).save(any());
        verify(blockedUserRepository, never()).deleteById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserActiveStatus_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Arrange
        UpdateUserIsActiveDto updateDto = new UpdateUserIsActiveDto(false);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUserActiveStatus(999L, updateDto));
        verify(userRepository).findById(999L);
        verify(blockedUserRepository, never()).save(any());
        verify(blockedUserRepository, never()).deleteById(any());
        verify(userRepository, never()).save(any());
    }
}
