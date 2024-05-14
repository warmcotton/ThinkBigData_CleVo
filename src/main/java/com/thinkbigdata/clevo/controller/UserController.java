package com.thinkbigdata.clevo.controller;

import com.thinkbigdata.clevo.dto.UserDto;
import com.thinkbigdata.clevo.dto.UserRegistrationDto;
import com.thinkbigdata.clevo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping ("/registration")
    public ResponseEntity<UserDto> register(@RequestPart MultipartFile userImage, @RequestPart @Valid UserRegistrationDto registerDto) {
        UserDto userDto = userService.registerUser(registerDto, userImage);
        return ResponseEntity.ok(userDto);
    }
}
