package shootingstar.stellaide.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shootingstar.stellaide.controller.dto.duplication.CheckEmailReqDto;
import shootingstar.stellaide.controller.dto.duplication.CheckNicknameReqDto;
import shootingstar.stellaide.service.CheckDuplicateService;

@RestController
@RequestMapping("/api/check-duplicate")
@RequiredArgsConstructor
public class CheckDuplicateController {

    private final CheckDuplicateService duplicateService;

    @PostMapping("/email")
    public ResponseEntity<String> checkEmail(@RequestBody @Valid CheckEmailReqDto req) {
        duplicateService.checkDuplicateEmail(req.getEmail());
        return ResponseEntity.ok().body("사용가능한 이메일입니다.");
    }

    @PostMapping("/nickname")
    public ResponseEntity<String> checkNickname(@RequestBody @Valid CheckNicknameReqDto req) {
        duplicateService.checkDuplicateNickname(req.getNickname());
        return ResponseEntity.ok().body("사용가능한 닉네임입니다.");
    }
}
