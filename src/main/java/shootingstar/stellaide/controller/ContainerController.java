package shootingstar.stellaide.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> createContainer(@Valid @RequestBody CreateContainerReqDto reqDto,
                                                  HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        containerService.createContainer(reqDto.getContainerType(), reqDto.getContainerName(), reqDto.getContainerDescription(), accessToken);
        return ResponseEntity.ok().body("컨테이너 생성에 성공하였습니다.");
    }

    @PatchMapping("/edit")
    public ResponseEntity<String> editContainer(@Valid @RequestBody EditContainerReqDto reqDto,
                                                HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);

        containerService.editContainer(reqDto.getContainerId(), reqDto.getContainerDescription(), accessToken);

        return ResponseEntity.ok().body("컨테이너 수정이 성공하였습니다.");
    }

    @DeleteMapping("/delete/{containerId}")
    public ResponseEntity<String> deleteContainer(@Size(min = 36, max = 36) @PathVariable("containerId") String containerId,
                                                  HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);

        containerService.deleteContainer(containerId, accessToken);

        return ResponseEntity.ok().body("컨테이너 삭제에 성공하였습니다.");
    }

    @PostMapping("/share")
    public ResponseEntity<String> shareContainer(@Valid @RequestBody ShareContainerReqDto reqDto,
                                                 HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);

        containerService.shareContainer(reqDto.getContainerId(), reqDto.getUserNickname(), accessToken);

        return ResponseEntity.ok().body("컨테이너 공유에 성공하였습니다.");
    }

    @DeleteMapping("/unshare/{containerId}/{userNickname}")
    public ResponseEntity<String> unshareContainer(@Size(min = 36, max = 36) @PathVariable("containerId") String containerId,
                                                   @NotBlank @PathVariable("userNickname") String userNickname,
                                                   HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);

        containerService.cancelContainerSharing(containerId, userNickname, accessToken);

        return ResponseEntity.ok().body("컨테이너 공유 해제에 성공하였습니다.");
    }

    @GetMapping("/treeInfo/{containerId}")
    public ResponseEntity<ContainerTreeResDto> getTreeInfo(@Size(min = 36, max = 36) @PathVariable("containerId") String containerId) {
        ContainerTreeResDto treeInfo = containerService.getTreeInfo(containerId);
        return ResponseEntity.ok().body(treeInfo);
    }

    @GetMapping("/fileContent")
    public ResponseEntity<String> getFileContent(@Valid @ModelAttribute FileContentReqDto reqDto) {
        String fileContent = containerService.getFileContent(reqDto.getContainerId(), reqDto.getFilePath());
        return ResponseEntity.ok().body(fileContent);
    }

    @PostMapping("/saveFile")
    public ResponseEntity<String> saveFile(@RequestBody @Valid SaveFileReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        containerService.saveFile(reqDto.getContainerId(), reqDto.getPath(), reqDto.getFileName(), reqDto.getFileContent(), accessToken);
        return ResponseEntity.ok("");
    }

    @PostMapping("/createFile")
    public ResponseEntity<String> createFile(@RequestBody @Valid CreateFileReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        containerService.createFile(reqDto.getContainerId(), reqDto.getPath(), reqDto.getFileName(), accessToken);
        return ResponseEntity.ok("");
    }

    @PostMapping("/createDirectory")
    public ResponseEntity<String> createDirectory(@RequestBody @Valid CreateDirectoryReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        containerService.createDirectory(reqDto.getContainerId(), reqDto.getPath(), reqDto.getDirectoryName(), accessToken);
        return ResponseEntity.ok("");
    }

    @PostMapping("/copyFile")
    public ResponseEntity<String> copyFile(@RequestBody @Valid CreateFileReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        containerService.copyFile(reqDto.getContainerId(), reqDto.getPath(), reqDto.getFileName(), accessToken);
        return ResponseEntity.ok("");
    }

    @PostMapping("/copyDirectory")
    public ResponseEntity<String> copyDirectory(@RequestBody @Valid CreateDirectoryReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        containerService.copyDirectory(reqDto.getContainerId(), reqDto.getPath(), reqDto.getDirectoryName(), accessToken);
        return ResponseEntity.ok("");
    }

    @PostMapping("/moveFile")
    public ResponseEntity<String> moveFile(@RequestBody @Valid MoveFileReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        containerService.moveFile(reqDto.getContainerId(), reqDto.getCurrentPath(), reqDto.getMovedPath(), reqDto.getFileName(), accessToken);
        return ResponseEntity.ok("");
    }

    @PostMapping("/moveDirectory")
    public ResponseEntity<String> moveDirectory(@RequestBody @Valid MoveDirectoryReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        containerService.moveDirectory(reqDto.getContainerId(), reqDto.getCurrentPath(), reqDto.getMovedPath(), reqDto.getDirectoryName(), accessToken);
        return ResponseEntity.ok("");
    }

    @PostMapping("/renameFile")
    public ResponseEntity<String> renameFile(@RequestBody @Valid RenameFileReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        containerService.renameFile(reqDto.getContainerId(), reqDto.getPath(), reqDto.getFileName(), reqDto.getChangeName(), accessToken);
        return ResponseEntity.ok("");
    }

    @PostMapping("/renameDirectory")
    public ResponseEntity<String> renameDirectory(@RequestBody @Valid RenameDirectoryReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        containerService.renameDirectory(reqDto.getContainerId(), reqDto.getPath(), reqDto.getDirectoryName(), reqDto.getChangeName(), accessToken);
        return ResponseEntity.ok("");
    }

    @DeleteMapping("/deleteFile")
    public ResponseEntity<String> deleteFile(@RequestBody @Valid CreateFileReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        containerService.deleteFile(reqDto.getContainerId(), reqDto.getPath(), reqDto.getFileName(), accessToken);
        return ResponseEntity.ok("");
    }

    @DeleteMapping("/deleteDirectory")
    public ResponseEntity<String> deleteDirectory(@RequestBody @Valid CreateDirectoryReqDto reqDto, HttpServletRequest request) {
        String accessToken = getTokenFromHeader(request);
        containerService.deleteDirectory(reqDto.getContainerId(), reqDto.getPath(), reqDto.getDirectoryName(), accessToken);
        return ResponseEntity.ok("");
    }

    @PostMapping("/execution")
    public ResponseEntity<String> executionFile(@RequestBody @Valid ExecutionFileReqDto reqDto) {
        String output = containerService.executionFile(reqDto.getContainerId(), reqDto.getPath());
        return ResponseEntity.ok(output);
    }

    @GetMapping("/getRoomId/{containerId}")
    public ResponseEntity<GetRoomResDto> getRoomId(@Size(min = 36, max = 36) @PathVariable("containerId") String containerId,
                                                   HttpServletRequest request) {
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
