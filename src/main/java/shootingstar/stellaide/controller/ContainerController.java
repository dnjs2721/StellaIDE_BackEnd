package shootingstar.stellaide.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.stellaide.controller.dto.container.CreateContainerReqDto;
import shootingstar.stellaide.controller.dto.container.AllContainerDto;
import shootingstar.stellaide.controller.dto.container.*;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.exception.ErrorCode;
import shootingstar.stellaide.service.ContainerService;
import shootingstar.stellaide.service.dto.ContainerTreeResDto;
import shootingstar.stellaide.service.dto.GetRoomResDto;
import shootingstar.stellaide.service.dto.SpringContainerResDto;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/container")
public class ContainerController {

    private final ContainerService containerService;

    @GetMapping("/search")
    public ResponseEntity<?> searchContainers(HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);

        AllContainerDto containers = containerService.getContainer(accessToken);
        return ResponseEntity.ok().body(containers);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createContainer(@Valid @RequestBody CreateContainerReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);

        containerService.createContainer(reqDto.getType(), reqDto.getName(), reqDto.getDescription(), accessToken);

        return ResponseEntity.ok().body("컨테이너 생성에 성공하였습니다.");
    }

    @PatchMapping("/edit/{containerId}")
    public ResponseEntity<String> editContainer(@PathVariable("containerId") String containerId, @Valid @RequestBody EditContainerReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);

        containerService.editContainer(containerId, reqDto.getDescription(), accessToken);

        return ResponseEntity.ok().body("컨테이너 수정이 성공하였습니다.");
    }

    @DeleteMapping("/delete/{containerId}")
    public ResponseEntity<String> deleteContainer(@Size(min = 36, max = 36) @PathVariable("containerId") String containerId, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);

        containerService.deleteContainer(containerId, accessToken);

        return ResponseEntity.ok().body("컨테이너 삭제에 성공하였습니다.");
    }

    @PostMapping("/share/{containerId}/{userNickname}")
    public ResponseEntity<String> shareContainer(@Size(min = 36, max = 36) @PathVariable("containerId") String containerId, @PathVariable("userNickname") String userNickname, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);

        containerService.shareContainer(containerId, userNickname, accessToken);

        return ResponseEntity.ok().body("컨테이너 공유에 성공하였습니다.");
    }

    @DeleteMapping("/unshare/{containerId}/{userNickname}")
    public ResponseEntity<String> unshareContainer(@Size(min = 36, max = 36) @PathVariable("containerId") String containerId, @PathVariable("userNickname") String userNickname, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);

        containerService.unshareContainer(containerId, userNickname, accessToken);

        return ResponseEntity.ok().body("컨테이너 공유 해제에 성공하였습니다.");
    }

    @GetMapping("/treeInfo/{containerId}")
    public ResponseEntity<ContainerTreeResDto> getTreeInfo(@Size(min = 36, max = 36) @PathVariable("containerId") String containerId) {
        ContainerTreeResDto treeInfo = containerService.getTreeInfo(containerId);
        return ResponseEntity.ok().body(treeInfo);
    }

    @GetMapping("/fileContent")
    public ResponseEntity<String> getFileContent(@ModelAttribute FileContentReqDto reqDto) {
        String fileContent = containerService.getFileContent(reqDto.getContainerId(), reqDto.getFilePath());
        return ResponseEntity.ok().body(fileContent);
    }

    @PostMapping("/createFile")
    public ResponseEntity<String> createFile(@RequestBody @Valid CreateFileReqDto reqDto) {
        containerService.createFile(reqDto.getContainerId(), reqDto.getPath(), reqDto.getFileName());
        return ResponseEntity.ok("");
    }

    @PostMapping("/createDirectory")
    public ResponseEntity<String> createFile(@RequestBody @Valid CreateDirectoryReqDto reqDto) {
        containerService.createDirectory(reqDto.getContainerId(), reqDto.getPath(), reqDto.getDirectoryName());
        return ResponseEntity.ok("");
    }

    @PostMapping("/execution")
    public ResponseEntity<String> executionFile(@RequestBody @Valid ExecutionFileReqDto reqDto) {
        containerService.executionFile(reqDto.getContainerId(), reqDto.getPath());
        return ResponseEntity.ok("");
    }

    @GetMapping("/getRoomId/{containerId}")
    public ResponseEntity<GetRoomResDto> getRoomId(@Size(min = 36, max = 36) @PathVariable("containerId") String containerId, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        GetRoomResDto roomInfo = containerService.getRoomId(containerId, accessToken);
        return ResponseEntity.ok().body(roomInfo);
    }

    @PostMapping("/execution/spring")
    public ResponseEntity<SpringContainerResDto> executionSpring(@RequestBody @Valid ExecutionSpringReqDto reqDto) {
        SpringContainerResDto res = containerService.executionSpring(reqDto.getContainerId());
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/stop/spring")
    public ResponseEntity<String> stopSpring(@RequestBody @Valid ExecutionSpringReqDto reqDto) {
        containerService.stopSpring(reqDto.getContainerId());
        return ResponseEntity.ok().body("");
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
