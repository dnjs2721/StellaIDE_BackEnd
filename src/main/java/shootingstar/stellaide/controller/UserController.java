package shootingstar.stellaide.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shootingstar.stellaide.controller.dto.user.SignupReqDto;
import shootingstar.stellaide.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid SignupReqDto req) {
        userService.signup(req.getEmail(), req.getPassword(), req.getNickname());
        return ResponseEntity.ok("성공적으로 회원가입 되었습니다.");
    }
}
