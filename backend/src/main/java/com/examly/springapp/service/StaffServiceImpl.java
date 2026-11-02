package com.examly.springapp.service;

import com.examly.springapp.config.JwtService;
import com.examly.springapp.dto.request.LoginRequestDto;
import com.examly.springapp.dto.request.RegisterRequestDto;
import com.examly.springapp.dto.request.StaffRegisterRequestDto;
import com.examly.springapp.dto.response.AuthResponseDto;
import com.examly.springapp.dto.response.StaffResponseDto;
import com.examly.springapp.dto.response.UserResponseDto;
import com.examly.springapp.exception.BadRequestException;
import com.examly.springapp.exception.DuplicateResourceException;
import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.Role;
import com.examly.springapp.model.Staff;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.StaffRepository;
import com.examly.springapp.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public StaffServiceImpl(
            StaffRepository staffRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponseDto loginStaff(LoginRequestDto loginDto) {
        String identifier = loginDto.getUsername().trim();
        Staff staff = staffRepository.findByUsername(identifier)
                .or(() -> staffRepository.findByEmail(identifier))
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(loginDto.getPassword(), staff.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        staff.setLastLoginDate(LocalDateTime.now());
        staffRepository.save(staff);

        String token = jwtService.generateToken(staff.getUsername(), staff.getRole().name());

        return AuthResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .id(staff.getStaffId())
                .username(staff.getUsername())
                .role(staff.getRole().name())
                .expiresIn(jwtService.getExpirationMs())
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDto loginUser(LoginRequestDto loginDto) {
        String identifier = loginDto.getUsername().trim();
        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        return AuthResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .id(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .expiresIn(jwtService.getExpirationMs())
                .build();
    }

    @Override
    @Transactional
    public StaffResponseDto registerStaff(StaffRegisterRequestDto requestDto) {
        String username = requestDto.getUsername().trim();
        String email = requestDto.getEmail().trim().toLowerCase();

        if (staffRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Staff username already exists: " + username);
        }
        if (staffRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Staff email already exists: " + email);
        }

        Role role = requestDto.getRole() != null ? requestDto.getRole() : Role.ROLE_STAFF;
        if (role != Role.ROLE_STAFF && role != Role.ROLE_ADMIN) {
            throw new BadRequestException("Invalid staff role: " + role);
        }

        Staff staff = Staff.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(requestDto.getPassword()))
                .role(role)
                .build();

        Staff saved = staffRepository.save(staff);
        return mapStaffToDto(saved);
    }

    @Override
    @Transactional
    public UserResponseDto registerUser(RegisterRequestDto requestDto) {
        String username = requestDto.getUsername().trim();
        String email = requestDto.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already exists: " + email);
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(requestDto.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        User saved = userRepository.save(user);
        return mapUserToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponseDto> getAllStaff() {
        return staffRepository.findAll()
                .stream()
                .map(this::mapStaffToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponseDto getStaffByUsername(String username) {
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + username));
        return mapStaffToDto(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return mapUserToDto(user);
    }

    private StaffResponseDto mapStaffToDto(Staff staff) {
        return StaffResponseDto.builder()
                .staffId(staff.getStaffId())
                .username(staff.getUsername())
                .email(staff.getEmail())
                .role(staff.getRole().name())
                .lastLoginDate(staff.getLastLoginDate())
                .createdAt(staff.getCreatedAt())
                .build();
    }

    private UserResponseDto mapUserToDto(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
