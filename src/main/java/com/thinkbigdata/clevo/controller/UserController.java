package com.thinkbigdata.clevo.controller;

import com.thinkbigdata.clevo.dto.*;
import com.thinkbigdata.clevo.dto.user.*;
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
        if (sessionId.isBlank())
            throw new RuntimeException("bad request");

        UserDto userDto = userService.addUserInfo(userInfoDto, sessionId);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/user")
    public ResponseEntity<UserDto> getUser(Authentication authentication) {
        UserDto userDto = userService.getUser(authentication.getName());
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<UserDashBoardDto> userDashboard(Authentication authentication) {
        UserDashBoardDto dashBoardDto = userService.userDashboard(authentication.getName());
        return ResponseEntity.ok(dashBoardDto);
    }

    //nickname, level, target, topic, image
    @PutMapping("/user-info")
    public ResponseEntity<UserDto> updateUserInfo(Authentication authentication, @RequestBody @Valid UserInfoUpdateDto updateDto) {
        UserDto userDto = userService.updateUserInfo(authentication.getName(), updateDto);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/user-profile")
    public ResponseEntity<UserDto> updateUserProfile(Authentication authentication, @RequestPart @Valid UserProfileUpdateDto updateDto, @RequestPart(required = false) MultipartFile userImage) {
        UserDto userDto = userService.updateUserProfile(authentication.getName(), updateDto, userImage);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody Map<String, String> login) {
        if (!login.containsKey("email") || !login.containsKey("password"))
            throw new RuntimeException("bad request");
        if (login.get("email") == null || login.get("password") == null)
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

    @PostMapping("/find/password")
    public ResponseEntity<?> findPassword(@RequestBody Map<String, String> check) {
        if (!check.containsKey("email") || !check.containsKey("name") || !check.containsKey("birth"))
            throw new RuntimeException("bad request");
        if (check.get("email") == null || check.get("name") == null || check.get("birth") == null)
            throw new RuntimeException("bad request");

        userService.findPassword(check.get("email"), check.get("name"), check.get("birth"));
        return ResponseEntity.ok(null);
    }

    @PutMapping("/update/password")
    public ResponseEntity<?> updatePassword(Authentication authentication, @RequestBody @Valid PasswordUpdateDto passwordDto) {
        userService.updatePassword(authentication.getName(), passwordDto);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/refresh/token")
    public ResponseEntity<TokenDto> refreshToken(@RequestBody Map<String, String> token) {
        if (!token.containsKey("refresh"))
            throw new RuntimeException("bad request");
        if (token.get("refresh") == null)
            throw new RuntimeException("bad request");

        TokenDto tokenDto = userService.refreshToken(token.get("refresh"));

        return ResponseEntity.ok(tokenDto);
    }
}
