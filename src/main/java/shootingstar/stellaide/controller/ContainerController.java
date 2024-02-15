package shootingstar.stellaide.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.stellaide.controller.dto.container.CreateContainerReqDto;
import shootingstar.stellaide.controller.dto.container.FindContainerDto;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.exception.ErrorCode;
import shootingstar.stellaide.service.ContainerService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/container")
public class ContainerController {

    private final ContainerService containerService;

    @GetMapping("/search/containers")
    public ResponseEntity<?> searchContainers(@RequestParam(value = "group") String group,
                                              @RequestParam(value = "query") String query,
                                              @RequestParam(value = "align") String align) {

        List<FindContainerDto> containers = containerService.getContainer(group, query, align);
        return ResponseEntity.ok().body(containers);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createContainer(@Valid @RequestBody CreateContainerReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);

        containerService.createContainer(reqDto.getType(), reqDto.getName(), reqDto.getDescription(), accessToken);

        return ResponseEntity.ok("컨테이너 생성에 성공하였습니다.");
    }

    @DeleteMapping("/delete/{containerId}")
    public ResponseEntity<String> deleteContainer(@Size(min = 36, max = 36) @PathVariable("containerId") String containerId) {
        containerService.deleteContainer(containerId);
        return ResponseEntity.ok().body("컨테이너 삭제에 성공하였습니다.");
    }

    private static String getTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization"); // 헤더에 존재하는 엑세스 토큰을 받아온다.

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 접두어 제거
        } else {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
        return token;
    }
}
