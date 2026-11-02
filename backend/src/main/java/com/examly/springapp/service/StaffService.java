package com.examly.springapp.service;

import com.examly.springapp.dto.request.LoginRequestDto;
import com.examly.springapp.dto.request.RegisterRequestDto;
import com.examly.springapp.dto.request.StaffRegisterRequestDto;
import com.examly.springapp.dto.response.AuthResponseDto;
import com.examly.springapp.dto.response.StaffResponseDto;
import com.examly.springapp.dto.response.UserResponseDto;

import java.util.List;

public interface StaffService {

    AuthResponseDto loginStaff(LoginRequestDto loginDto);

    AuthResponseDto loginUser(LoginRequestDto loginDto);

    StaffResponseDto registerStaff(StaffRegisterRequestDto requestDto);

    UserResponseDto registerUser(RegisterRequestDto requestDto);

    List<StaffResponseDto> getAllStaff();

    StaffResponseDto getStaffByUsername(String username);

    UserResponseDto getUserByUsername(String username);
}
