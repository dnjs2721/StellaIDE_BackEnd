package shootingstar.stellaide.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.stellaide.controller.dto.container.FindContainerDto;
import shootingstar.stellaide.controller.dto.container.SaveContainerDto;
import shootingstar.stellaide.service.ContainerService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ContainerController {

    private final ContainerService containerService;

    @GetMapping("/search/containers")
    public ResponseEntity<?> searchContainers(@RequestParam(value = "group") String group,
                                              @RequestParam(value = "query") String query,
                                              @RequestParam(value = "align") String align) {

        List<FindContainerDto> containers = containerService.getContainer(group, query, align);
        return ResponseEntity.ok().body(containers);
    }

    @PostMapping("/create/container")
    public ResponseEntity<?> createContainer(@Validated @RequestBody SaveContainerDto saveContainerDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        containerService.create(saveContainerDto);

        return ResponseEntity.ok("");
    }

    @DeleteMapping("/delete/container/{cid}")
    public void deleteContainer(@PathVariable UUID cid) {
        containerService.delete(cid);



    }
}
