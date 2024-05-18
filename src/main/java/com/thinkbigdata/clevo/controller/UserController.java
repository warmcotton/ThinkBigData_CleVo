package com.thinkbigdata.clevo.controller;

import com.thinkbigdata.clevo.dto.TokenDto;
import com.thinkbigdata.clevo.dto.UserDto;
import com.thinkbigdata.clevo.dto.UserInfoDto;
import com.thinkbigdata.clevo.dto.UserRegistrationDto;
import com.thinkbigdata.clevo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping ("/signup/user")
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid UserRegistrationDto registerDto) {
        String sessionId = UUID.randomUUID().toString();
        UserDto userDto = userService.registerUser(registerDto, sessionId);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("sessionId", sessionId);

        return new ResponseEntity<>(userDto, responseHeaders, 200);
    }

    @PostMapping ("/signup/info")
    public ResponseEntity<UserDto> addUserInfo(@RequestHeader("sessionId") String sessionId, @RequestBody @Valid UserInfoDto userInfoDto) {
        UserDto userDto = userService.addUserInfo(userInfoDto, sessionId);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/user")
    public ResponseEntity<UserDto> getUser(Authentication authentication) {
        UserDto userDto = userService.getUser(authentication.getName());
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/user")
    public ResponseEntity<UserDto> updateUser(Authentication authentication, @RequestPart @Valid UserDto updateDto, @RequestPart MultipartFile userImage) {
        UserDto userDto = userService.updateUser(authentication.getName(), updateDto, userImage);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody Map<String, String> login) {
        if (!login.containsKey("email") || !login.containsKey("password"))
            throw new RuntimeException("bad request");

        TokenDto token = userService.login(login.get("email"), login.get("password"));

        if (token == null)
            throw new RuntimeException("not match");

        return ResponseEntity.ok(token);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token, Authentication authentication) {
        if (token.isBlank())
            throw new RuntimeException("bad request");

        if (StringUtils.hasText(token) && token.startsWith("Bearer"))
            token = token.substring(7);
        else throw new RuntimeException("bad request");

        userService.logout(token, authentication.getName());
        return ResponseEntity.status(200).build();
    }

    @PostMapping("/refresh/token")
    public ResponseEntity<TokenDto> refreshToken(@RequestBody Map<String, String> token) {
        if (!token.containsKey("refresh"))
            throw new RuntimeException("bad request");

        TokenDto tokenDto = userService.refreshToken(token.get("refresh"));

        return ResponseEntity.ok(tokenDto);
    }
}
