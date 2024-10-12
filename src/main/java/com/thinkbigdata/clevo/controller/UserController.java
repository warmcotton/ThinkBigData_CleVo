package com.thinkbigdata.clevo.controller;

import com.thinkbigdata.clevo.dto.*;
import com.thinkbigdata.clevo.dto.post.CommentDto;
import com.thinkbigdata.clevo.dto.post.PostDto;
import com.thinkbigdata.clevo.dto.user.*;
import com.thinkbigdata.clevo.exception.DuplicateEmailException;
import com.thinkbigdata.clevo.exception.InvalidSessionException;
import com.thinkbigdata.clevo.exception.RefreshTokenException;
import com.thinkbigdata.clevo.service.PostService;
import com.thinkbigdata.clevo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PostService postService;

    @PostMapping ("/signup/user")
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid UserRegistrationDto registerDto) throws DuplicateEmailException {
        if (!registerDto.getPassword2().equals(registerDto.getPassword1()))
            throw new IllegalArgumentException("등록할 비밀번호가 일치하지 않습니다.");

        String sessionId = UUID.randomUUID().toString();
        UserDto userDto = userService.registerUser(registerDto, sessionId);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("sessionId", sessionId);

        return new ResponseEntity<>(userDto, responseHeaders, 200);
    }

    @PostMapping ("/signup/info")
    public ResponseEntity<UserDto> addUserInfo(@RequestHeader("sessionId") String sessionId, @RequestBody @Valid UserInfoDto userInfoDto) throws InvalidSessionException {
        if (sessionId.isBlank())
            throw new IllegalArgumentException("sessionId 정보가 유효하지 않습니다.");

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

    @GetMapping("/user/posts")
    public ResponseEntity<Page<PostDto>> userPosts(Authentication authentication, @PageableDefault(sort = "created", direction = Sort.Direction.DESC) Pageable page) {
        return ResponseEntity.ok(postService.getUserPostDtos(authentication.getName(), page));
    }

    @GetMapping("/user/comments")
    public ResponseEntity<Page<CommentDto>> userComments(Authentication authentication, @PageableDefault(sort = "created", direction = Sort.Direction.DESC) Pageable page) {
        return ResponseEntity.ok(postService.getUserCommentsDtos(authentication.getName(), page));
    }

    @PutMapping("/user-info")
    public ResponseEntity<UserDto> updateUserInfo(Authentication authentication, @RequestBody @Valid UserInfoUpdateDto updateDto) {
        UserDto userDto = userService.updateUserInfo(authentication.getName(), updateDto);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/user-profile")
    public ResponseEntity<UserDto> updateUserProfile(Authentication authentication, @RequestPart(required = false) @Valid UserProfileUpdateDto updateDto, @RequestPart(required = false) MultipartFile userImage) throws IOException {
        UserDto userDto = userService.updateUserProfile(authentication.getName(), updateDto, userImage);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/user-target")
    public ResponseEntity<UserDto> updateUserTarget(Authentication authentication,@RequestBody Map<String, Integer> target) {
        if(!target.containsKey("target")) throw new IllegalArgumentException("target 정보가 유효하지 않습니다.");
        if(target.get("target")==null) throw new IllegalArgumentException("target 정보가 유효하지 않습니다.");
        if(target.get("target") > 10 || target.get("target") < 1) throw new IllegalArgumentException("target 정보가 유효하지 않습니다.");
        UserDto userDto = userService.updateTarget(authentication.getName(), target.get("target"));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody Map<String, String> login) {
        if (!login.containsKey("email") || !login.containsKey("password"))
            throw new IllegalArgumentException("email, password 정보가 유효하지 않습니다.");
        if (login.get("email") == null || login.get("password") == null)
            throw new IllegalArgumentException("email, password 정보가 유효하지 않습니다.");

        TokenDto token = userService.login(login.get("email"), login.get("password"));

        if (token == null)
            throw new IllegalArgumentException("not match");

        return ResponseEntity.ok(token);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token, Authentication authentication) {
        if (token.isBlank())
            throw new IllegalArgumentException("token 정보가 유효하지 않습니다.");

        if (StringUtils.hasText(token) && token.startsWith("Bearer"))
            token = token.substring(7);
        else throw new IllegalArgumentException("token 정보가 유효하지 않습니다.");

        userService.logout(token, authentication.getName());
        return ResponseEntity.status(200).build();
    }

    @PostMapping("/find/password")
    public ResponseEntity<?> findPassword(@RequestBody Map<String, String> check) {
        if (!check.containsKey("email") || !check.containsKey("name") || !check.containsKey("birth"))
            throw new IllegalArgumentException("email, name, birth 정보가 유효하지 않습니다.");
        if (check.get("email") == null || check.get("name") == null || check.get("birth") == null)
            throw new IllegalArgumentException("email, name, birth 정보가 유효하지 않습니다.");

        userService.findPassword(check.get("email"), check.get("name"), check.get("birth"));
        return ResponseEntity.ok(null);
    }

    @PutMapping("/update/password")
    public ResponseEntity<?> updatePassword(Authentication authentication, @RequestBody @Valid PasswordUpdateDto passwordDto) {
        userService.updatePassword(authentication.getName(), passwordDto);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/refresh/token")
    public ResponseEntity<TokenDto> refreshToken(@RequestBody Map<String, String> token) throws RefreshTokenException {
        if (!token.containsKey("refresh"))
            throw new IllegalArgumentException("refresh 정보가 유효하지 않습니다.");
        if (token.get("refresh") == null)
            throw new IllegalArgumentException("refresh 정보가 유효하지 않습니다.");

        TokenDto tokenDto = userService.refreshToken(token.get("refresh"));

        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/user/delete")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, String> password, Authentication authentication) {
        if (!password.containsKey("password")) throw new IllegalArgumentException("password 정보가 유효하지 않습니다.");
        if (password.get("password") == null) throw new IllegalArgumentException("password 정보가 유효하지 않습니다.");
        userService.deleteUser(authentication.getName(), password.get("password"));
        return ResponseEntity.ok(null);
    }
}
